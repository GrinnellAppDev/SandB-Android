package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;
import android.util.Pair;

<<<<<<< HEAD
=======

>>>>>>> httpClientIntegrationRealm
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.AppNetworkClientAPI;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;
import edu.grinnell.sandb.Util.ISO8601;
import edu.grinnell.sandb.Util.StringUtility;

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
 * @see Observer
 * @see SyncMessage
 * @see Serializable
 */
public class NetworkClient extends Observable implements Observer, AppNetworkClientAPI, Serializable {
    private LocalCacheClient localClient;
    private RemoteServiceAPI remoteClient;
    private int numArticlesPerPage;
    private int currentPage;
    private boolean syncing;
<<<<<<< HEAD
=======
    private static final String TAG= NetworkClient.class.getName();
>>>>>>> httpClientIntegrationRealm

    public NetworkClient() {
        this(new RealmDbClient());
    }

    public NetworkClient(LocalCacheClient localCacheClient) {
        this(localCacheClient, new WordPressService(localCacheClient));
    }

    public NetworkClient(LocalCacheClient localClient, RemoteServiceAPI remoteClient) {
        this.currentPage = 0;
        this.numArticlesPerPage = Constants.DEFAULT_NUM_ARTICLES_PER_PAGE;
        this.localClient = localClient;
        this.remoteClient = remoteClient;
        ;
        this.syncing = false;
        addRemoteServiceListeners();
    }


    @Override
    public List<RealmArticle> getArticles(String category, int pageNum) {
        return localClient.getArticlesByCategory(category,pageNum);
    }

<<<<<<< HEAD
    public List<RealmArticle> getNextPage(String category, int currentPageNumber) {
        updateLocalCache(category);
        return localClient.getArticlesByCategory(category);
    }

    @Override
    public List<String> getCategories() {
        // updateLocalCache();
        return localClient.getCategories();
    }

    public List<RealmArticle> getInitialArticles(String category) {
        return getNextPage(category, 0);
    }
=======
    @Override
    public List<RealmArticle> getNextPage(String category, int pageNumber){
        Log.i(TAG,"Fetching  Page " + pageNumber + "for "+ category);
        List<RealmArticle> articles = localClient.getArticlesByCategory(category,pageNumber);
        if(articles.isEmpty()) {
            Map<String, Pair<Integer, String>> dbMetaData = localClient.getDbMetaData();
            Pair<Integer, String> pair = dbMetaData.get(category);
            int numberToFetch = pair.first % Constants.DEFAULT_NUM_ARTICLES_PER_PAGE;
            remoteClient.getNextPage(pageNumber,Constants.DEFAULT_NUM_ARTICLES_PER_PAGE,category,numberToFetch);
        }
        return  articles;
    }



>>>>>>> httpClientIntegrationRealm

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

    @Override
    public void initialDataFetch() {
        remoteClient.initialize();
    }

    @Override
    public Map<String, Pair<Integer, String>> getDbMetaData() {
        return localClient.getDbMetaData();
    }

<<<<<<< HEAD
    public List<RealmArticle> getLatestArticles(String category, Date mostRecentArticleDate) {
        if (syncing) {
            remoteClient.getAfter(ISO8601.fromCalendar(mostRecentArticleDate), category);
            //updateLocalCache(category);
=======
    @Override
    public List<RealmArticle> getLatestArticles(String category,Date mostRecentArticleDate){
        if(syncing){
            remoteClient.getAfter(ISO8601.fromCalendar(mostRecentArticleDate),category);
>>>>>>> httpClientIntegrationRealm
        }
        return localClient.getArticlesAfter(category.toLowerCase(), mostRecentArticleDate);
    }

    /**
<<<<<<< HEAD
     * Updates the local cache when necessary with data from the remote server.
=======
     *  Updates the local cache when necessary with data from the remote server.
     *
>>>>>>> httpClientIntegrationRealm
     */
    public void updateLocalCache(String category) {
        if (category.equals(Constants.ArticleCategories.ALL.toString())
                && Constants.FIRST_CALL_TO_UPDATE) {
            Constants.FIRST_CALL_TO_UPDATE = false;
            firstTimeSyncLocalAndRemoteData();
<<<<<<< HEAD
        } else {
            syncLocalAndRemoteData(category);
        }
    }

    public void syncLocalAndRemoteData(String category) {
        RealmArticle localFirst = this.localClient.getFirst();
        remoteClient.syncWithLocalCache(localFirst, category);
=======
        }else {
           syncLocalAndRemoteData(category);
        }
    }

    public void syncLocalAndRemoteData(String category){
        //TODO : Check if this method is needed or not
      //  RealmArticle localFirst= this.localClient.getFirst();
      //  remoteClient.syncWithLocalCache(localFirst, category);
>>>>>>> httpClientIntegrationRealm
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "Message Reached Update");
        SyncMessage message = (SyncMessage) data;
<<<<<<< HEAD
        if (message != null) {
=======
        if(message != null) {
            syncing = false;
>>>>>>> httpClientIntegrationRealm
            if (message.updateType.equals(Constants.UpdateType.INITIALIZE)) {
                Log.i(TAG, "Update Type :INITIALIZE, Remote Call ; SUCCESS");
            }
<<<<<<< HEAD
            if (message.updateType.equals(Constants.UpdateType.REFRESH)) {
                Log.i("Network Client", "Update type : REFRESH");
                setChanged();
                notifyObservers(message);
=======
            if(message.updateType.equals(Constants.UpdateType.REFRESH)){
                Log.i(TAG,"Update type : REFRESH");
            }
            if(message.updateType.equals(Constants.UpdateType.NEXT_PAGE)){
                Log.i(TAG,"Update type : NEXT_PAGE for "+ message.getCategory());
>>>>>>> httpClientIntegrationRealm
            }
            setChanged();
            notifyObservers(message);
        }
<<<<<<< HEAD
        */
    }


    public void firstTimeSyncLocalAndRemoteData() {
        String currentTimeAsISO = StringUtility.dateToISO8601(new Date());
        this.remoteClient.getAll(currentPage, numArticlesPerPage, currentTimeAsISO);
    }

    private void addRemoteServiceListeners() {
        List<Observer> remoteServiceObservers = new ArrayList<>(1);
        remoteServiceObservers.add(this);
        this.remoteClient.addObservers(remoteServiceObservers);
    }

    public void setSyncing(boolean syncing) {
        this.syncing = syncing;
    }

    private void topUpArticlesPerCategory() {
       /* for(Map.Entry<String,Integer> entry : categoryMap){
            String category = entry.getKey();
            int mod;
            if(category != "ALL" && ((mod = (entry.getValue() % numArticlesPerPage)) != 0)){
                String date = localClient.getLastDate(category);
                remoteClient.getAfter(category,date);

            }
        }
        */

    }

    public void topUpCategories() {
        Map<String, Pair<Integer, String>> dbMetaData = localClient.getDbMetaData();
        for (String category : Constants.CATEGORIES) {
            if (!category.toLowerCase().equals("all")) {
=======

    }

    /**
     *  Enforces that there are enough articles per category to fill up the UI tab.
     */
    public void topUpCategories(){
        Map<String, Pair<Integer, String>> dbMetaData = localClient.getDbMetaData();
        for(String category : Constants.CATEGORIES){
            if(!category.toLowerCase()
                    .equals(Constants.ArticleCategories.ALL.toString().toLowerCase())){
>>>>>>> httpClientIntegrationRealm
                Pair<Integer, String> pair = dbMetaData.get(category.toLowerCase());
                Integer numSoFar = pair.first;
                Integer topUpNum = Constants.DEFAULT_NUM_ARTICLES_PER_PAGE -
                        (numSoFar % Constants.DEFAULT_NUM_ARTICLES_PER_PAGE);
                String dateBefore = pair.second;
<<<<<<< HEAD
                Log.i("Network Client", "Topping up " + category + "after " + dateBefore + " by " + topUpNum);

=======
                Log.i(TAG, "Topping up " +numSoFar +" " + category + "after " + dateBefore
                        +  " by " + topUpNum);
>>>>>>> httpClientIntegrationRealm
                remoteClient.getByCategory(category, dateBefore, topUpNum);
            }
        }

    }

<<<<<<< HEAD
}
=======
    public void  setSyncing(boolean sync){
        this.syncing = sync;
    }

    private void firstTimeSyncLocalAndRemoteData(){
        String currentTimeAsISO = StringUtility.dateToISO8601(new Date());
        this.remoteClient.getAll(currentPage, numArticlesPerPage, currentTimeAsISO);
    }


    private void addRemoteServiceListeners() {
        List<Observer> remoteServiceObservers = new ArrayList<>(1);
        remoteServiceObservers.add(this);
        this.remoteClient.addObservers(remoteServiceObservers);
    }



}
>>>>>>> httpClientIntegrationRealm
