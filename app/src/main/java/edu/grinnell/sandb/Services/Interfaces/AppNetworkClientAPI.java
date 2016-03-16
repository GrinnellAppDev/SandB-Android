package edu.grinnell.sandb.Services.Interfaces;

import java.util.List;

import edu.grinnell.sandb.Model.Article;

/**
 * A client API that describes the core networking functionality for the application.
 *
 * <P>An object of an implementation of this client would serve all the networking demands of the
 * application. The methods in this file abstracts the different data needs of the application</P>
 * @author Albert Owusu-Asare
 * @version 1.1 Wed Mar 16 13:31:47 CDT 2016
 */
public interface AppNetworkClientAPI {
    /**
     * Fetches all articles according to a specific category
     * @return
     */
    List<Article> getArticles(String category);

    /**
     * Fetches all the articles according to a specific category by the page we are currently on.
     * @param category
     * @param currentPageNumber
     * @param lastArticleId tells us from which article ID in the database to get the next page from.
     * @return a list of Articles for the page
     */
    List<Article> getNextPage(String category, int currentPageNumber,int lastArticleId);

    /**
     * @return all the categories articles can be classed into
     */
    List<String> getCategories();

    /**
     * Sets the number of articles per page in our application.
     * @param number the number of articles per page.
     */
    void setNumArticlesPerPage(int number);

    /**
     * @return the number of articles per page.
     */
    int getNumArticlesPerPage();

}
