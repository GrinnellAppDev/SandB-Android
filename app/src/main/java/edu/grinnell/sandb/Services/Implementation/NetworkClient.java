package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
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
 * @see Observer
 * @see SyncMessage
 */
public class NetworkClient extends Observable implements Observer, AppNetworkClientAPI {
    private LocalCacheClient localClient;
    private RemoteServiceAPI remoteClient;
    private int numArticlesPerPage;
    private int currentPage;

    public NetworkClient(){
        this(new ORMDbClient());
    }

    public NetworkClient(LocalCacheClient localCacheClient){
        this(localCacheClient, new WordPressService(localCacheClient));
    }

    public NetworkClient(LocalCacheClient localClient, RemoteServiceAPI remoteClient){
        this.currentPage = 0;
        this.numArticlesPerPage = Constants.DEFAULT_NUM_ARTICLES_PER_PAGE;
        this.localClient = localClient;
        this.remoteClient = remoteClient;
        addRemoteServiceListeners();
    }


    @Override
    public List<Article> getArticles(String category) {
       // updateLocalCache(); TODO :fix bug with remote pull
        return localClient.getArticlesByCategory(category.toLowerCase());
    }

    @Override
    public List<Article> getNextPage(String category, int currentPageNumber, int lastArticleId) {
        updateLocalCache();
        return localClient.getNextPage(category, currentPageNumber, lastArticleId);
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

    @Override
    public void deleteLocalCache() {
        localClient.deleteAllEntries(Constants.TableNames.ARTICLE.toString());
    }

    /**
     *  Updates the local cache when necessary with data from the remote server.
     */
    public void updateLocalCache() {
        if(localClient.isCacheEmpty()) {
            remoteClient.getAll(currentPage, numArticlesPerPage);
        }
        syncLocalAndRemoteData(false);
    }

    public void syncLocalAndRemoteData(boolean firstCall){
        Article localFirst = null;
        if(!firstCall) {
             localFirst= this.localClient.getFirst();
        }
        remoteClient.syncWithLocalCache(localFirst);
    }
    @Override
    public void update(Observable observable, Object data) {
        Log.d("Network Client", "Reached Update");
        SyncMessage message = (SyncMessage) data;
        if(message != null){
            setChanged();
            notifyObservers(message);
        }
    }

    private void addRemoteServiceListeners() {
        List<Observer> remoteServiceObservers = new ArrayList<>(1);
        remoteServiceObservers.add(this);
        this.remoteClient.addObservers(remoteServiceObservers);
    }

    public void firstTimeSyncLocalAndRemoteData(){
        this.remoteClient.getAll();
    }

}
