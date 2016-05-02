package edu.grinnell.sandb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.SyncMessage;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by albertowusu-asare on 4/21/16.
 */
public class SplashActivity extends AppCompatActivity implements Observer {
    NetworkClient networkClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        networkClient = new NetworkClient();
        networkClient.addObserver(this);
        Log.i("Splash Activity", "Calling initialize on network client");
        networkClient.initialize();

    }

    @Override
    public void update(Observable observable, Object data) {
        Log.i("Splash Activity", "Update Type :INITIALIZE");
        SyncMessage syncMessage = (SyncMessage) data;
        if(updateSuccessful(syncMessage)){
            Log.i("Splash Activity", "Update Type :INITIALIZE, Remote Call ; SUCCESS");
            for(String category : Constants.CATEGORIES){
                Log.i("Splash Activity", "Db Meta Data "+ category +" =" + networkClient.getDbMetaData().get(category.toLowerCase()).first);
            }


            Intent intent = new Intent(this, MainActivity.class);
            //top up remaining
            startActivity(intent);
            finish();
        }
    }

    /* Private Helper methods */
    private boolean updateSuccessful(SyncMessage message){
        return message.getUpdateType().equals(Constants.UpdateType.INITIALIZE) &&
                message.getHttpStatusCode() == Constants.OK;
    }
}
