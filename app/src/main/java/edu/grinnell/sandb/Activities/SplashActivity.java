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
import edu.grinnell.sandb.Util.NetworkUtil;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Startup Activity for S & B. Initializations to both the remote and local databases
 * are made in this class.
 *
 * @author Albert Owusu-Asare
 * @see NetworkClient
 * @see Observer
 * @see Realm
 * @since 4/21/16.
 */
public class SplashActivity extends AppCompatActivity implements Observer {
<<<<<<< HEAD
    NetworkClient networkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Realm model persistence config */
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        if (NetworkUtil.isNetworkEnabled(this)) {
            networkClient = new NetworkClient();
            networkClient.addObserver(this);
            networkClient.initialDataFetch();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
=======
    private NetworkClient networkClient;
    private static final String TAG = SplashActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Initializing Network Client");
        networkClient = new NetworkClient();
        networkClient.addObserver(this);
        networkClient.initialDataFetch();
>>>>>>> httpClientIntegrationRealm
    }

    @Override
    public void update(Observable observable, Object data) {
        SyncMessage syncMessage = (SyncMessage) data;
<<<<<<< HEAD
        if (updateSuccessful(syncMessage)) {
            Log.i("Splash Activity", "Update Type :INITIALIZE, Remote Call ; SUCCESS");
=======
        if(updateSuccessful(syncMessage)){
            Log.i(TAG, "Initial data fetch successful");
>>>>>>> httpClientIntegrationRealm
            networkClient.topUpCategories();

        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* Private Helper methods */
    private boolean updateSuccessful(SyncMessage message) {
        return message.getUpdateType().equals(Constants.UpdateType.INITIALIZE) &&
                message.getHttpStatusCode() == Constants.OK;
    }
}
