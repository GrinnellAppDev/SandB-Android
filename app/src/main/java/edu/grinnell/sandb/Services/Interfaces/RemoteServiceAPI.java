package edu.grinnell.sandb.Services.Interfaces;

import java.util.List;
import java.util.Observer;

import edu.grinnell.sandb.Model.RealmArticle;

/**
 * Extracts the functionality of how we interact with the remote database.
 * <p/>
 * Classes that implement this API will make remote service calls to some other Rest API for the
 * external database.
 *
 * @author Albert Owusu-Asare
 * @verson "%I" "%G"
 */
public interface RemoteServiceAPI {
    /**
     * Through an asynchronous call the first remote post is retrieved
     * Specifically, this method fetches the most recent Article according to ISO 8601 time.
     */
    void getFirst();

    /**
     * Fetches all the articles pushed to the remote database after a set date
     *
     * @param date     the date after which to get all articles. DateTime string is in iso8601 format
     * @param category the category that
     */

    void getAfter(String date, String category);

    /**
     * Fetches all the articles from the remote server given a page number and a count of the
     * number of articles to return
     *
     * @param page            the page number
     * @param count           the count of Articles to return
     * @param lastArticleDate
     */
    void getAll(int page, int count, String lastArticleDate);


    /**
     * Fetches all the articles from the remote server specifying which article fields to pull data.
     * <p/>
     * This prevents the loading  of extra information for those calls to the remote server only to
     * verify or check specific fields.
     *
     * @param fields
     */
    void getAll(List<String> fields);


    /**
     * Check if the remote service has been updated since the last time the local cache was updated
     *
     * @param localFirst
     * @return
     */
    boolean isUpdated(RealmArticle localFirst);

    /**
     * Adds Observers to the remoteService to listen for any data changes
     *
     * @param observers
     */
    void addObservers(List<Observer> observers);

    /**
     * Makes sure that the local client is synced to the remote client
     *
     * @param localFirst
     * @param category   the category that we are trying to sync data for
     */
    void syncWithLocalCache(RealmArticle localFirst, String category);

    void getNextPage(int page, int offset, String category, int number);

    void initialize();

    void getByCategory(String category, String lastArticleUpdated, Integer topUpNum);

}
