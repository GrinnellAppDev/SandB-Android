package edu.grinnell.sandb.Services.Implementation;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.ArticleCategory;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Util.StringUtility;

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
public class SugarDbClient implements LocalCacheClient,Serializable {
    /* Sugar ORM order convention leaves a space before the specified order*/
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DESC";
    private static final String ALL ="all";
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 10;
    private int numArticlesPerPage;
    private int previousRowUpperBound = 1;
    private Map<String, Integer> categorySizeMap;
    private Map<String, Pair<Integer, String>> dbMetaData;
    public SugarDbClient(){
      //  this.numArticlesPerPage = DEFAULT_NUM_ARTICLES_PER_PAGE;
        this(DEFAULT_NUM_ARTICLES_PER_PAGE);

    }
    public SugarDbClient(int numArticlesPerPage){
        this.numArticlesPerPage = numArticlesPerPage;
        this.dbMetaData = new HashMap<>();
        populateCategorySizeMap();
        deleteAllEntries(Constants.TableNames.ARTICLE.toString());
    }

    private void populateCategorySizeMap() {
        this.categorySizeMap = new HashMap<>();
        initialize();
        for(String str : Constants.CATEGORIES ){
            this.categorySizeMap.put(str, 0);
        }
    }


    @Override
    public void saveArticles(List<Article> articles) {
        for(Article article : articles ){
            saveArticle(article);
        }
    }

    private void updateDbMetaData(Article article) {
        String category = article.getCategory();
        Pair<Integer, String> data = dbMetaData.get(category);
        Integer numArticles = data.first;
        Pair<Integer, String> newData = new Pair(numArticles +1, article.getPubDate());
        dbMetaData.put(category,newData);
    }

    @Override
    public void saveArticle(Article article) {
        String categoryName = article.getAuthor().getName();
        article.setCategory(categoryName);
        updateDbMetaData(article);
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
    public List<Article> getNextPage(String category, int currentPageNumber,
                                     String lastVisibleArticleDate) {
        List<Article> articles = Select.from(Article.class)
                .orderBy("datetime(pub_date)" + DESCENDING)
                .where(Condition.prop("pub_date").lt(lastVisibleArticleDate))
                .limit(Integer.toString(numArticlesPerPage)).list();
        return (articles != null) ? articles : new ArrayList();
    }



    @Override
    public boolean isCacheEmpty() {
        //return isArticleCacheEmpty() || isCategoryCacheEmpty();
        return isArticleCacheEmpty();
    }

    @Override
    public List<Article> getAll() {

        //List<Article> articles = Article.find(Article.class, null, null, null, null,
          //      Integer.toString(numArticlesPerPage));
        List<Article> articles = Article.listAll(Article.class);
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
    public void updateCategorySizes() {



    }

    @Override
    public void initialize() {
        Date currentDate = new Date();
        String currentDateISO8601 = StringUtility.dateToISO8601(currentDate);
        Pair<Integer, String> pair = new Pair(0,currentDateISO8601);
        for(String category : Constants.CATEGORIES) {
            this.dbMetaData.put(category.toLowerCase(), pair);
        }
    }

    private void updateCategorySize(String category,String latestDateUpdated) {
        //Querry here and update respe
    }

    @Override
    public void updateNumEntriesPerCategory(String category,int updatedArticlesSize) {
        int previousSize = categorySizeMap.get(category);
        categorySizeMap.put(category, previousSize + updatedArticlesSize);

    }

    @Override
    public void updateNumEntriesAll(int numRecentUpdates,String latestDateUpdated){
        updateNumEntriesPerCategory(Constants.ArticleCategories.ALL.toString(),numRecentUpdates);
        updateRespectiveCategorySizes(latestDateUpdated);
    }
    private void updateRespectiveCategorySizes(String latestDateUpdated) {
        Set<String> keySet = this.categorySizeMap.keySet();
        for(String str : keySet){
            updateCategorySize(str,latestDateUpdated);
        }
    }


    @Override
    public List<Article> getArticlesAfter(String category, String date) {
        if (date == null)
            return getArticlesByCategory(category);
        List<Article> articles = Select.from(Article.class)
                .orderBy("datetime(pub_date)" + DESCENDING)
                .where(Condition.prop("pub_date").gt(date))
                .limit(Integer.toString(numArticlesPerPage)).list();
        return (articles != null) ? articles : new ArrayList();
    }

    @Override
    public void setNumArticlesPerPage(int numArticlesPerPage) {
        this.numArticlesPerPage = numArticlesPerPage;
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
