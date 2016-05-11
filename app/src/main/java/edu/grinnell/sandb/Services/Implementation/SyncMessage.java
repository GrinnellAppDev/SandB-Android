package edu.grinnell.sandb.Services.Implementation;



import java.util.List;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.RealmArticle;

/**
 * This class encapsulates a message that is passed from the various observables to their
 * respective observers.
 *
 * <p> The class allows organize a bulk message that includes the 'data' that is to be transmitted,
 * along with other relevant data such as success/failure codes </p>
 * @author Albert Owusu-Asare
 * @version 1.1 on 3/18/16.
 */
public final class SyncMessage {

    Constants.UpdateType updateType;
    private int remoteHttpStatusCode;
    int pageRequested;
    private String categoryUpdated;
    String lastSyncedArticleDate;
    private Object data;


    public SyncMessage(Constants.UpdateType updateType,int remoteHttpStatusCode,int pageRequested,
                       String categoryUpdated, String lastSyncedArticleDate,Object data){
        this.updateType = updateType;
        this.remoteHttpStatusCode = remoteHttpStatusCode;
        this.pageRequested = pageRequested;
        this.categoryUpdated = categoryUpdated;
        this.lastSyncedArticleDate = lastSyncedArticleDate;
        this.data = data;
    }


    public SyncMessage(List<RealmArticle> data){
        remoteHttpStatusCode  = 200;
        categoryUpdated = "All";
        pageRequested =0;
        lastSyncedArticleDate = null;
        this.data = data;
    }

    public SyncMessage(int httpStatusCode, String category, List<RealmArticle> data){
        this(httpStatusCode,category,Constants.ZERO,null,data);
    }
    public SyncMessage(int httpStatusCode,String category, int pageRequested,
                       String lastArticleVisibleDate){
        this(httpStatusCode,category,pageRequested,lastArticleVisibleDate,null);


    }
    public SyncMessage(int httpStatusCode,String category, int pageRequested,
                       String lastArticleVisibleDate,List<RealmArticle> data){
        this.remoteHttpStatusCode  = httpStatusCode;
        this.categoryUpdated =category;
        this.pageRequested =pageRequested;
        this.lastSyncedArticleDate = lastArticleVisibleDate;
        this.data = data;
    }

    public void setMessageData(List<RealmArticle> data){ this.data = data;}
    public void setUpdateType(Constants.UpdateType updateType){this.updateType = updateType;}

    public Object getMessageData(){return this.data;}
    public int getHttpStatusCode(){return this.remoteHttpStatusCode;}
    public String getCategory() {return this.categoryUpdated;}
    public int getPageRequested(){return this.pageRequested;}
    public String getLastSyncedArticleDate(){return this.lastSyncedArticleDate;}
    public Constants.UpdateType getUpdateType() { return this.updateType;}

}