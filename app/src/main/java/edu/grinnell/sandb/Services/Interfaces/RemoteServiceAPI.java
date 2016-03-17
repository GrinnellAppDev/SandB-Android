package edu.grinnell.sandb.Services.Interfaces;

import java.util.Date;
import java.util.List;

import edu.grinnell.sandb.Model.Article;

/**
 * Extracts the functionality of how we interact with the remote database.
 *
 * Classes that implement this API will make remote service calls to some other Rest API for the
 * external database.
 * @author Albert Owusu-Asare
 * @verson "%I" "%G"
 */
public interface RemoteServiceAPI {
    /**
     * @return the Article at the top of the remote list of the remote Articles.
     *
     * Specifically, this method fetches the most recent Article according to ISO 8601 time.
     */
    Article getFirst();

    /**
     * Fetches all the articles pushed to the remote database after a set date
     * @param date the date after which to get all articles. DateTime string is in iso8601 format
     * @return all the articles pushed after the given date
     */
    List<Article> getAfter(String date);

    /**
     * Fetches all the Articles from the remote server
     * @return a list of all the articles .
     */
    List<Article> getAll();

    /**
     * Fetches all the articles from the remote server given a page number and a count of the
     * number of articles to return
     * @param page the page number
     * @param count the count of Articles to return
     * @return a list of all the articles that satisfies the query
     */
    List<Article> getAll(int page, int count);

    /**
     * Fetches all the articles from the remote server specifying which article fields to pull data.
     *
     * This prevents the loading  of extra information for those calls to the remote server only to
     * verify or check specific fields.
     * @param fields
     * @return
     */
    List<Article> getAll(List<String> fields);

}
