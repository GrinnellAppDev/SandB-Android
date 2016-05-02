package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Util.StringUtility;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by albertowusu-asare on 5/1/16.
 */
public class RealmDbClient implements LocalCacheClient {
    private Realm realm = Realm.getDefaultInstance();
    private Map<String, Pair<Integer, String>> dbMetaData = new HashMap<>();
    private final String ALL ="all";

    public RealmDbClient(){
        initialize();
    }

    @Override
    public void saveArticles(List<RealmArticle> articles) {
        Log.i("RealmDbClient:", "In save Articles...");
        for(RealmArticle article : articles){
            saveArticle(article);
        }
    }

    @Override
    public void saveArticle(RealmArticle article) {
        Log.i("RealDbClient:", "Saving article" + article.getArticleID() + "...");
        String categoryName = article.getAuthor().getName();
        article.setCategory(categoryName);
        article.setRealmDate(article.getPubDate());
        updateDbMetaData(article);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(article);
        realm.commitTransaction();
    }

    @Override
    public RealmArticle getFirst() {
        return null;
    }

    @Override
    public List<RealmArticle> getArticlesByCategory(String categoryName) {
        Log.i("RealDBClient", "Attempting to querry local db for " + categoryName);
        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll();
        }
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("category", categoryName);

        RealmResults<RealmArticle> result1 = query.findAll();
        List<RealmArticle> articles = result1.subList(0,result1.size());
        Log.i("RealmDBClient", "Number of " + categoryName + "articles currently = " +
                this.dbMetaData.get(categoryName).first);

       return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    @Override
    public List<String> getCategories() {
        return null;
    }

    @Override
    public List<Article> getNextPage(String categoryName, int currentPageNumber, String lastVisibleArticleDate) {
        return null;
    }


    @Override
    public boolean isCacheEmpty() {
        return false;
    }

    @Override
    public List<RealmArticle> getAll() {
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        RealmResults<RealmArticle> result1 = query.findAll();
        List<RealmArticle> articles = result1.subList(0,10);
        return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    @Override
    public void deleteAllEntries(String tableName) {

    }

    @Override
    public int getNumArticlesPerPage() {
        return 0;
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

    @Override
    public void updateNumEntriesPerCategory(String category, int updatedArticlesSize) {

    }

    @Override
    public void updateNumEntriesAll(int numRecentUpdates, String latestDateUpdated) {

    }

    @Override
    public Map<String, Pair<Integer, String>> getDbMetaData() {
        return dbMetaData;
    }

    @Override
    public List<RealmArticle> getArticlesAfter(String category, Date date) {
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("category", category);
        query.greaterThan("realmDate", date);
        RealmResults<RealmArticle> result1 = query.findAll();
        List<RealmArticle> articles = result1.subList(0,result1.size());
        return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    @Override
    public void setNumArticlesPerPage(int numArticlesPerPage) {

    }

    private void updateDbMetaData(RealmArticle article) {
        String category = article.getCategory();
        Pair<Integer, String> data = dbMetaData.get(category);
        Integer numArticles = data.first;
        Pair<Integer, String> newData = new Pair(numArticles +1, article.getPubDate());
        dbMetaData.put(category,newData);
    }


}
