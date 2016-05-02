package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;

import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import io.realm.Realm;

/**
 * Created by albertowusu-asare on 5/1/16.
 */
public class RealmDbClient implements LocalCacheClient {
    private Realm realm = Realm.getDefaultInstance();

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
        realm.copyToRealm(article);
        realm.commitTransaction();
    }

    @Override
    public RealmArticle getFirst() {
        return null;
    }

    @Override
    public List<RealmArticle> getArticlesByCategory(String categoryName) {
        return null;
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
        return null;
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
