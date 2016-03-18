package edu.grinnell.sandb.Services.Implementation;

import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Interfaces.AppNetworkClientAPI;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;

/**
 * Implements the AppNetworkClientAPI interface and handles the main data requests of the
 * application.
 *
 * <p>The client connects to a local caching system and also a remote database.
 * The local caching system connection is achieved through some client that connects to the
 * Android SQLite databases. The remote database connection is achieved through a client that
 * implements REST endpoint network connections.</p>
 *
 * @author  Albert Owusu-Asare
 * @see edu.grinnell.sandb.Services.Interfaces.AppNetworkClientAPI
 * @see edu.grinnell.sandb.Services.Interfaces.LocalCacheClient
 */
public class NetworkClient implements AppNetworkClientAPI {
    private LocalCacheClient localClient;
    private RemoteServiceAPI remoteClient;
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;
    private int numArticlesPerPage;
    private int currentPage;

    public NetworkClient(){
        this(new ORMDbClient(DEFAULT_NUM_ARTICLES_PER_PAGE)
                ,new WordPressService(DEFAULT_NUM_ARTICLES_PER_PAGE));
    }
    public NetworkClient(LocalCacheClient localClient, RemoteServiceAPI remoteClient){
        this.currentPage = 0;
        this.numArticlesPerPage = DEFAULT_NUM_ARTICLES_PER_PAGE;
        this.localClient = localClient;
        this.remoteClient = remoteClient;

    }
    @Override
    public List<Article> getArticles(String category) {
        updateLocalCache();
        return localClient.getArticlesByCategory(category);
    }

    @Override
    public List<Article> getNextPage(String category, int currentPageNumber, int lastArticleId) {
        updateLocalCache();
        return localClient.getNextPage(category,currentPageNumber, lastArticleId);
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
     *  Updates the local cache when necessary with data from the remote server.
     */
    public void updateLocalCache() {
        List<Article> updates = null;
        Article localFirst;
        if(localClient.isCacheEmpty()) {
            updates =remoteClient.getAll(currentPage, numArticlesPerPage);
        }
        else if(remoteClient.isUpdated(localFirst = this.localClient.getFirst())){
            updates =remoteClient.getAfter(localFirst.getPubDate());
        }

        localClient.saveArticles(updates);
    }


}
