package edu.grinnell.sandb.Services.Implementation;

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
    //TODO : Implement Message Codes for each message

    private Object data;

    public SyncMessage( Object data){
        this.data = data;
    }



    public Object getMessageData(){
        return this.data;
    }
}