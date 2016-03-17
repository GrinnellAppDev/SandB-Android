package edu.grinnell.sandb.Services.Interfaces;

import java.util.List;

import edu.grinnell.sandb.Model.Article;

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
    void saveArticles(List<Article> articles);

    /**
     * Persists an article object to the local cache.
     * @return {@code true} if the persistence was successful.
     */
   void saveArticle(Article article);

    /**
     * Returns the Article that sits on top  of the local  cache.
     *
     * <p> Note that it may be assumed that the data is stored in chronological order such that
     * the most recent {@link Article} in terms of time posted will be the most recent entry in the
     * Article cache.</p>
     * @return
     */
    Article getFirst();

    /**
     * Returns a list of the most recent articles belonging to a particular category.
     *
     * Note that the number of articles returns depends on a set default value for what a page is.
     * This value may differ per implementation.
     * @param categoryName the category to query the local cache by.
     * @return the list of Articles of category : "category".
     */
    List<Article> getArticlesByCategory(String categoryName);

    /**
     * @return a list of all the categories represented in the Articles cache.
     */
    List<String> getCategories();
    /**
     * Returns the next page of results as specified by the set default value for the number of
     * articles in a page.
     *
     * @param currentPageNumber the page that last accessed.
     * @param lastArticleId the id of the last article belonging to the last accessed page.
     * @return a list of Articles satisfying the query.
     */
    List<Article> getNextPage(int currentPageNumber,int lastArticleId);

    /**
     * @return true if the cache is empty.
     */
    boolean isCacheEmpty();
    /**
     * Returns all the articles
     * @return list of all the articles
     */
    List<Article> getAll();
}
