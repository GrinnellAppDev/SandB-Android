package edu.grinnell.sandb.Services.Implementation;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.ArticleCategory;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * This class implements an object relational mapper using sugar ORM to perform SQLite queries to
 * the local device
 *
 * @author Albert Owusu-Asare
 * @version 1.1 Wed Mar 16 19:03:18 CDT 2016
 */
public class ORMDbClient implements LocalCacheClient {
    /* Sugar ORM order convention leaves a space before the specified order*/
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DSC";
    private static final String ALL ="ALL";
    private int numArticlesPerPage;
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
        article.save();
    }

    @Override
    public Article getFirst() {
        List<Article> articles= Article.find(Article.class, null, null, null,
                "articleID"+ASCENDING, "1");
        return !(articles == null) ? articles.get(0) : null;
    }

    @Override
    public List<Article> getArticlesByCategory(String categoryName) {
        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll();
        }
        Select<Article> categoryQuery = Select.from(Article.class)
                .where(Condition.prop("category").eq(categoryName));
        List<Article> articles = categoryQuery.list();

        return !(articles == null) ? articles : new ArrayList();
    }

    @Override
    public List<String> getCategories() {
        Select<ArticleCategory> categoryQuery = Select.from(ArticleCategory.class);
        List<ArticleCategory> articles = categoryQuery.list();
        return !(articles == null) ?  articles : new ArrayList();
    }

    @Override
    public List<Article> getNextPage(int currentPageNumber, int lastArticleId) {
        Select<Article> categoryQuery = Select.from(Article.class).orderBy("pubDate"+DESCENDING)
                .where("articleId >"+Integer.toString(lastArticleId))
                .limit(Integer.toString(numArticlesPerPage));
        List<Article> articles = categoryQuery.list();
        return !(articles == null) ?  articles : new ArrayList();
    }

    @Override
    public boolean isCacheEmpty() {
        return isArticleCacheEmpty() || isCategoryCacheEmpty();
    }

    @Override
    public List<Article> getAll() {
        List<Article> articles= Article.find(Article.class, null, null, null,null,
                Integer.toString(numArticlesPerPage));
        return !(articles == null) ? articles : new ArrayList();
    }
    /**
     * Checks if the article cache is empty
     */
    private boolean isArticleCacheEmpty(){
        return (getFirst() == null);
    }

    /**
     * Checks if the category cache is empty
     * @return
     */
    private boolean isCategoryCacheEmpty(){
        List<ArticleCategory> articles= Article.find(ArticleCategory.class, null, null, null,
                "categoryName"+ASCENDING, "1");
        return !(articles == null && articles.isEmpty());
    }

}
