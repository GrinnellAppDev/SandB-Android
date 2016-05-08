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
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Util.ISO8601;
import edu.grinnell.sandb.Util.StringUtility;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A local database client using the Realm persistence.
 * @author Albert Owusu-Asare
 * Created by albertowusu-asare on 5/1/16.
 */
public class RealmDbClient implements LocalCacheClient {
    private Realm realm;
    private Map<String, Pair<Integer, String>> dbMetaData = new HashMap<>();
    private final String ALL ="all";
    private final String TAG= RealmDbClient.class.getName();

    public RealmDbClient(){
        initialize();
    }

    @Override
    public void saveArticles(List<RealmArticle> articles) {
        Log.i(TAG, "In save Articles...");
        realm =  Realm.getDefaultInstance();
        for(RealmArticle article : articles){
            saveArticle(article);
        }
        realm.close();
    }

    @Override
    public void saveArticle(RealmArticle article) {
        Log.i(TAG, "Saving article" + article.getArticleID() + "...");
        /* We obtain the category through the author name. This is due to the structure of
           json the end point returns.*/
        String categoryName = article.getAuthor().getName();
        article.setCategory(categoryName);
        article.setRealmDate(article.getPubDate());
        updateDbMetaData(article);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(article);
        realm.commitTransaction();
    }


    @Override
    public List<RealmArticle> getArticlesByCategory(String categoryName,int pageNum) {
        realm =  Realm.getDefaultInstance();
        Log.i(TAG, "Attempting to query local db for " + categoryName);
        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll(pageNum);
        }
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo(Constants.ArticleTableColumnNames.CATEGORY, categoryName);
        /* Sort to get most recent articles first */
        RealmResults<RealmArticle> result1 = query.findAll().
                sort(Constants.ArticleTableColumnNames.REALM_DATE, Sort.DESCENDING);

        int [] pageIndexes = getPageIndexes(pageNum,result1.size());
        /* Same page indexes denotes no more pages. See Java AbstractList.subList() */
        List<RealmArticle> articles = result1.subList(pageIndexes[0],pageIndexes[1]);
        articles = realm.copyFromRealm(articles);
        realm.close();
       return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    /**
     * Calculates the indexes that denotes the bounds of the sub list of our query results that
     * contains the desired page.
     * @param pageNum
     * @param querryResultsSize
     * @return
     */
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
    public boolean isCacheEmpty() {
        realm =  Realm.getDefaultInstance();
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        realm.close();
        return query.findAll().isEmpty();

    }

    @Override
    public List<RealmArticle> getAll(int pageNum) {
        realm =  Realm.getDefaultInstance();
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        RealmResults<RealmArticle> result1 = query.findAll()
                .sort(Constants.ArticleTableColumnNames.REALM_DATE, Sort.DESCENDING);
        int[] pageIndexes = getPageIndexes(pageNum, result1.size());
        List<RealmArticle> articles = result1.subList(pageIndexes[0], pageIndexes[1]);
        articles = realm.copyFromRealm(articles);
        realm.close();
        return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
    }

    @Override
    public void deleteAllEntries(String tableName) {
        if(tableName.equals(Constants.TableNames.ARTICLE)){
            realm =  Realm.getDefaultInstance();
            realm.where(RealmArticle.class).findAll().deleteAllFromRealm();
            realm.close();
        }
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
    public Map<String, Pair<Integer, String>> getDbMetaData() {
        return dbMetaData;
    }

    @Override
    public List<RealmArticle> getArticlesAfter(String category, Date date) {
        realm =  Realm.getDefaultInstance();
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("category", category);
        query.greaterThan("realmDate", date);
        RealmResults<RealmArticle> result1 = query.findAll();


        List<RealmArticle> articles = result1.subList(0,result1.size());
        realm.close();
        return (articles.size() ==0) ? new ArrayList<RealmArticle>() : articles;
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
