package edu.grinnell.sandb;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.WebRequestTask;
import edu.grinnell.sandb.xmlpull.WebRequestTask.Result;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	public String FEED_URL = "http://www.thesandb.com/feed";
	
	private WebRequestTask mWRT;
	
	private TextView mMainText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMainText = (TextView) findViewById(R.id.maintext);
		
		mWRT = new WebRequestTask(this, new WebRequestTask.RetrieveDataListener() {
			
			@Override
			public void onRetrieveData(Result result) {
				// TODO Auto-generated method stub
				Log.d("DataRecieved", result.getValue());
				mMainText.setText(result.getValue());
			}
		});
		
		mWRT.execute(FEED_URL);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
