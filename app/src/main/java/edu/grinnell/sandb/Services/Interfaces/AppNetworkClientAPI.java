package edu.grinnell.sandb.Services.Interfaces;

import android.util.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;

/**
 * A client API that describes the core networking functionality for the application.
 *
 * <P>An object of an implementation of this client will serve all the networking demands of the
 * application. The methods in this file abstracts the different data needs of the application</P>
 * @author Albert Owusu-Asare
 * @version 1.1 Wed Mar 16 13:31:47 CDT 2016
 */
public interface AppNetworkClientAPI {
    /**
     * Fetches all articles according to a specific category
     * @return
     * //TODO : params
     */
    List<RealmArticle> getArticles(String category,int pageNum);

    /**
     * Fetches all the articles according to a specific category by the page we are currently on.
     * @param category
     * @param pageNumber
     * @return a list of Articles for the page
     */
    List<RealmArticle> getNextPage(String category, int pageNumber);

    /**
     * Fetches the most recent articles by category.
     * @param category the category of articles to fetch
     * @param mostRecentArticleDate the most recent article fetched from the given category
     * @return a list of the latest articles in the give category
     */
    List<RealmArticle> getLatestArticles(String category,Date mostRecentArticleDate);


    /**
     * Sets the number of articles per page in our application.
     * @param number the number of articles per page.
     */
    void setNumArticlesPerPage(int number);

    /**
     * @return the number of articles per page.
     */
    int getNumArticlesPerPage();

    /**
     * Clears the local Cahce
     */
    void deleteLocalCache();

    /**
     * Performs the initial fetch of data on start up of the application.
     */
    void initialDataFetch();

    /**
     * @return the metaData on the state of the local cache.
     */
     Map<String, Pair<Integer, String>> getDbMetaData();



}
