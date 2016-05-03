package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Util.ISO8601;
import edu.grinnell.sandb.Util.StringUtility;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

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
        RealmArticle articleSavedrealm = realm.copyToRealmOrUpdate(article);
        realm.commitTransaction();
    }

    @Override
    public RealmArticle getFirst() {
        return null;
    }

    @Override
    public List<RealmArticle> getArticlesByCategory(String categoryName,int pageNum) {
        Log.i("RealDBClient", "Attempting to querry local db for " + categoryName);
        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll(pageNum);
        }
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("category", categoryName);
        /* Sort to get most recent articles first */
        RealmResults<RealmArticle> result1 = query.findAll().sort("realmDate",Sort.DESCENDING);

        int [] pageIndexes = getPageIndexes(pageNum,result1.size());

        // fetch more data from remote
        Log.i("RealmDbClient", "From "+ pageIndexes[0] +"to " + pageIndexes[1]);
        Log.i("RealDbClient", "Getting page" + pageNum + "of "+ categoryName +" Size " + result1.size());
          /* Same page indexes denotes no more pages. See Java Sublist */
        List<RealmArticle> articles = result1.subList(pageIndexes[0],pageIndexes[1]);
        articles = realm.copyFromRealm(articles);
        Log.i("RealmDBClient", "Number of " + categoryName + "articles currently = " +
                this.dbMetaData.get(categoryName).first);

       return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    private int[] getPageIndexes(int pageNum, int querryResultsSize) {
        int [] arr = new int[2];
        int begin = (pageNum -1) * Constants.DEFAULT_NUM_ARTICLES_PER_PAGE;
        int end = begin + Constants.DEFAULT_NUM_ARTICLES_PER_PAGE;
        arr[0] = begin;
        if(end > querryResultsSize){
            end = querryResultsSize;
        }
        arr[1] = end;
        return arr;
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
    public List<RealmArticle> getAll(int pageNum) {
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        RealmResults<RealmArticle> result1 = query.findAll().sort("realmDate", Sort.DESCENDING);
        int[] pageIndexes = getPageIndexes(pageNum, result1.size());
        List<RealmArticle> articles = result1.subList(pageIndexes[0], pageIndexes[1]);
        articles = realm.copyFromRealm(articles);
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
        String pairDate = data.second;

        Date previousDate;

        try {
             previousDate = ISO8601.toDate(pairDate);
        } catch (ParseException e) {
            previousDate = new Date();
        }

        Date articleDate = article.getRealmDate();
        // Incoming date less recent than previous data
        if(articleDate.compareTo(previousDate) <= 0){
            pairDate =  ISO8601.fromCalendar(articleDate);
        }


        Pair<Integer, String> newData = new Pair(numArticles +1, pairDate);
        dbMetaData.put(category,newData);
    }


}
