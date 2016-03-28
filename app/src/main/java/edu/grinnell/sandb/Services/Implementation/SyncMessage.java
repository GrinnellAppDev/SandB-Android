package edu.grinnell.sandb.Services.Implementation;



import edu.grinnell.sandb.Constants;

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

    private Object data;
    private int httpStatusCode;
    private String category;

    public SyncMessage( Object data){
        this(Constants.DEFAULT_HTTP_CODE,null, data);
    }
    public SyncMessage(int httpStatusCode, String category, Object data){
        this.data = data;
        this.httpStatusCode = httpStatusCode;
        this.category =category;
    }

    public Object getMessageData(){return this.data;}
    public int getHttpStatusCode(){return this.httpStatusCode;}
    public String getCategory() {return this.category;}
}