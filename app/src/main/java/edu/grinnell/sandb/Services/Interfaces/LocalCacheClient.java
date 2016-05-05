package edu.grinnell.sandb.Services.Interfaces;

import android.util.Pair;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import io.realm.Realm;

/**
 *
 * Extracts the main functionality of a local cache client.
 * All the classes that implement this class will provide concrete implementations as to how to
 * interact with the different Android local caching systems.
 *
 * @author Albert Owusu-Asare
 * @version 0.1 Wed Mar 16 01:09:53 CDT 2016
 */
public interface LocalCacheClient {
    /**
     * Persists a list of article objects to the local cache.
     * @param articles the list of articles.
     * @return {@code true} if the persistence was successful.
     */
    void saveArticles(List<RealmArticle> articles);

    /**
     * Persists an article object to the local cache.
     * @return {@code true} if the persistence was successful.
     */
   void saveArticle(RealmArticle article);


    /**
     * Returns a list of the most recent articles belonging to a particular category.
     *
     * Note that the number of articles returns depends on a set default value for what a page is.
     * This value may differ per implementation.
     * @param categoryName the category to query the local cache by.
     * @return the list of Articles of category : "category".
     * //TODO params
     */
    List<RealmArticle> getArticlesByCategory(String categoryName, int pageNum);



    /**
     * @return true if the cache is empty.
     */
    boolean isCacheEmpty();
    /**
     * Returns all the articles
     * @return list of all the articles
     */
    List<RealmArticle> getAll(int pageNum);

    /**
     * Drops the table referenced to by clazz in the SQLiteDb
     * @param tableName the name of the SQLite table
     */
    void deleteAllEntries(String tableName);


    /**
     * Returns a list of Articles posted from {@code date} and belonging to the category
     * {@code category}
     * @param category
     * @param date
     * @return the list of articles satisfying the query.
     */
    List<RealmArticle> getArticlesAfter(String category,Date date);



    /**
     * Updates the number of articles of the specific category that exist in the database
     */

    void initialize();

    /**
      *
      * @return the metaData on the state of the local cache.
      */
    Map<String, Pair<Integer, String>> getDbMetaData();





}
