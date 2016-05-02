package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by albertowusu-asare on 5/1/16.
 */
public class RealmDbClient implements LocalCacheClient {
    private Realm realm = Realm.getDefaultInstance();
    private final String ALL ="all";

    @Override
    public void saveArticles(List<RealmArticle> articles) {
        Log.i("RealmDbClient:", "In save Articles...");
        for(RealmArticle article : articles){
            saveArticle(article);
        }
    }

    @Override
    public void saveArticle(RealmArticle article) {
        Log.i("RealDbClient:","Saving article" + article.getArticleID() +"...");
        String categoryName = article.getAuthor().getName();
        article.setCategory(categoryName);
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
        Log.i("RealDBClient","Attempting to querry local db for " + categoryName);
        if (categoryName == null || categoryName.equals(ALL)) {
            return getAll();
        }
        RealmQuery<RealmArticle> query = realm.where(RealmArticle.class);
        query.equalTo("category", categoryName);

        RealmResults<RealmArticle> result1 = query.findAll();
        List<RealmArticle> articles = result1.subList(0,result1.size());

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

    }

    @Override
    public void updateNumEntriesPerCategory(String category, int updatedArticlesSize) {

    }

    @Override
    public void updateNumEntriesAll(int numRecentUpdates, String latestDateUpdated) {

    }

    @Override
    public List<RealmArticle> getArticlesAfter(String category, String date) {
        return null;
    }

    @Override
    public void setNumArticlesPerPage(int numArticlesPerPage) {

    }


}
