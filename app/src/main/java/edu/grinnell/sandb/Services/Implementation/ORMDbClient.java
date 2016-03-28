package edu.grinnell.sandb.Services.Implementation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.ArticleCategory;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * This class implements an object relational mapper using sugar ORM to perform SQLite queries to
 * the local device
 *
 * @author Albert Owusu-Asare
 * @version 1.1 Wed Mar 16 19:03:18 CDT 2016
 * @see LocalCacheClient
 * @see Serializable
 */
public class ORMDbClient implements LocalCacheClient,Serializable {
    /* Sugar ORM order convention leaves a space before the specified order*/
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DESC";
    private static final String ALL ="all";
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;
    private int numArticlesPerPage;
    public ORMDbClient(){
        this.numArticlesPerPage = DEFAULT_NUM_ARTICLES_PER_PAGE;
    }
    public ORMDbClient(int numArticlesPerPage){
        this.numArticlesPerPage = numArticlesPerPage;
    }

    @Override
    public void saveArticles(List<Article> articles) {
        for(Article article : articles ){
            saveArticle(article);
        }
    }

    @Override
    public void saveArticle(Article article) {
        String categoryName = article.getAuthor().getName();
        article.setCategory(categoryName);
        article.save();
    }

    @Override
    public Article getFirst() {
        //SELECT * FROM Table ORDER BY date(dateColumn) DESC Limit 1 datetime(datetimeColumn)
        List<Article > articles =Select.from(Article.class).orderBy("datetime(pub_date)"+DESCENDING)
                .limit("1").list();
        return ((articles != null) && !articles.isEmpty()) ? articles.get(0) : null;
    }

    @Override
    public List<Article> getArticlesByCategory(String categoryName) {

        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll();
        }
        Select<Article> categoryQuery = Select.from(Article.class)
                .where(Condition.prop("category").eq(categoryName));
        List<Article> articles = categoryQuery.list();

        return (articles != null) ? articles : new ArrayList();
    }

    @Override
    public List<String> getCategories() {
        Select<ArticleCategory> categoryQuery = Select.from(ArticleCategory.class);
        List<ArticleCategory> articles = categoryQuery.list();
        return !(articles == null) ?  articles : new ArrayList();
    }

    @Override
    public List<Article> getNextPage(String category, int currentPageNumber, int lastArticleId) {
        Select<Article> categoryQuery = Select.from(Article.class).orderBy("pubDate"+DESCENDING)
                .where("category==" + category).where("articleId >" + Integer.toString(lastArticleId))
                .limit(Integer.toString(numArticlesPerPage));
        List<Article> articles = categoryQuery.list();
        return !(articles == null) ?  articles : new ArrayList();
    }

    @Override
    public boolean isCacheEmpty() {
        //return isArticleCacheEmpty() || isCategoryCacheEmpty();
        return isArticleCacheEmpty();
    }

    @Override
    public List<Article> getAll() {
        List<Article> articles= Article.find(Article.class, null, null, null,null,
                Integer.toString(numArticlesPerPage));
        return !(articles == null) ? articles : new ArrayList();
    }

    @Override
    public void deleteAllEntries(String tableName) {
        if(tableName.equals(Constants.TableNames.ARTICLE.toString()))
            Article.deleteAll(Article.class);
    }

    @Override
    public int getNumArticlesPerPage() {
        return numArticlesPerPage;
    }

    @Override
    public List<Article> getArticlesAfter(String category, String date) {
        return null;
    }

    /**
     * Checks if the article cache is empty
     * SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName}
     * Note.executeQuery("VACUUM");
     */
    private boolean isArticleCacheEmpty(){
        return Article.count(Article.class,null,null) == Constants.ZERO;
       // return (getFirst() == null);
    }

    /**
     * Checks if the category cache is empty
     * @return
     */
    private boolean isCategoryCacheEmpty(){
        return ArticleCategory.count(ArticleCategory.class,null,null) == Constants.ZERO;
    }

}
