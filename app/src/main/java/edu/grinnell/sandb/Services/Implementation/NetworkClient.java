package edu.grinnell.sandb.Services.Implementation;

import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Interfaces.AppNetworkClientAPI;
import edu.grinnell.sandb.Services.Interfaces.ArticlesCallback;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;

/**
 * Implements the AppNetworkClientAPI interface and handles the main data requests of the
 * application.
 * <p/>
 * <p>The client connects to a local caching system and also a remote database.
 * The local caching system connection is achieved through some client that connects to the
 * Android SQLite databases. The remote database connection is achieved through a client that
 * implements REST endpoint network connections.</p>
 *
 * @author Albert Owusu-Asare
 * @see edu.grinnell.sandb.Services.Interfaces.AppNetworkClientAPI
 * @see edu.grinnell.sandb.Services.Interfaces.LocalCacheClient
 */
public abstract class NetworkClient implements AppNetworkClientAPI, ArticlesCallback {
    private LocalCacheClient localClient;
    private RemoteServiceAPI remoteClient;
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;
    private int numArticlesPerPage;
    private int currentPage;
    private String category;

    public NetworkClient() {
        this.currentPage = 0;
        this.numArticlesPerPage = DEFAULT_NUM_ARTICLES_PER_PAGE;
        this.localClient = new ORMDbClient(DEFAULT_NUM_ARTICLES_PER_PAGE);
        this.remoteClient = new WordPressService(DEFAULT_NUM_ARTICLES_PER_PAGE);

    }

    @Override
    public void getArticles(boolean isOnline, String category) {
        this.category = category;
        // if there is an internet connection, then try to update
        if (isOnline)
            updateLocalCache();
        // otherwise, just pull from the cache
        else
            onArticlesRetrieved(localClient.getArticlesByCategory(category));
    }

    @Override
    public List<String> getCategories() {
        updateLocalCache();
        return localClient.getCategories();
    }

    @Override
    public void setNumArticlesPerPage(int number) {
        this.numArticlesPerPage = number;
    }

    @Override
    public int getNumArticlesPerPage() {
        return this.numArticlesPerPage;
    }


    /**
     * Updates the local cache when necessary with data from the remote server.
     */
    public void updateLocalCache() {

        // callback to save the articles to cache
        final ArticlesCallback saveToCacheCallback = new ArticlesCallback() {
            @Override
            public void onArticlesRetrieved(List<Article> articles) {
                localClient.saveArticles(articles);
                // notify that the articles have been saved
                onLocalCacheUpdated();
            }
        };


        if (localClient.isCacheEmpty()) {
            // if there are no articles in the cache, get all articles
            remoteClient.getAll(currentPage, numArticlesPerPage, saveToCacheCallback);
        } else {

            // callback to check if cache is updated
            ArticlesCallback checkCacheUpdatedCallback = new ArticlesCallback() {
                @Override
                public void onArticlesRetrieved(List<Article> articles) {
                    Article localFirst = localClient.getFirst();
                    // check to see if the the titles of the newest article
                    // in the server is the same as the newest article in the cache
                    if (!articles.get(0).getTitle().equals(localFirst.getTitle())) {
                        // if the not the same, then the cache is outdated,
                        // so update the cache to get the articles that haven't been cached
                        remoteClient.getAfter(localFirst.getPubDate(), saveToCacheCallback);
                    } else {
                        // otherwise, notify that the cache has been updated
                        onLocalCacheUpdated();
                    }
                }
            };

            remoteClient.getFirst(checkCacheUpdatedCallback);

        }

    }

    /**
     * onArticlesRetrieved will be called after the cache has been updated
     */
    public void onLocalCacheUpdated() {
        onArticlesRetrieved(localClient.getArticlesByCategory(category));
    }


    /**
     * This will be overridden by the UI
     *
     * @param articles
     */
    @Override
    public abstract void onArticlesRetrieved(List<Article> articles);
}
