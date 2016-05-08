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
    }

    @Override
    public void update(Observable observable, Object data) {
        SyncMessage syncMessage = (SyncMessage) data;
        if (updateSuccessful(syncMessage)) {
            Log.i("Splash Activity", "Update Type :INITIALIZE, Remote Call ; SUCCESS");
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
