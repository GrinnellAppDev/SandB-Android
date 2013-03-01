package edu.grinnell.sandb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.WebRequestTask;
import edu.grinnell.sandb.xmlpull.WebRequestTask.Result;
import edu.grinnell.sandb.xmlpull.XMLParseTask;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;

public class MainActivity extends FragmentActivity implements ArticleListFragment.Callbacks {

	public String FEED_URL = "http://www.thesandb.com/feed";
	
	private WebRequestTask mWRT;
	
	//private TextView mMainText;
	private List<Article> mArticles;
	private ArticleListFragment mListFrag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListFrag = (ArticleListFragment) 
				getSupportFragmentManager().findFragmentById(R.id.articles_fragment);
		mArticles = new ArrayList<Article>();
		//mMainText = (TextView) findViewById(R.id.maintext);
		
		
		
		mWRT = new WebRequestTask(this, 
				new WebRequestTask.RetrieveDataListener() {
			
			@Override
			public void onRetrieveData(Result result) {
				Log.d("DataReceived", "resultingStream: " + result.getStream().toString());

				//mMainText.setText(result.getStream().toString());
				parseXmlFromStream(result.getStream());
			}
		});
		
		mWRT.execute(FEED_URL);
		
		
		
	}
	
	private void parseXmlFromStream(InputStream xmlStream) {
		XMLParseTask xpt= new XMLParseTask(this, 
				new XMLParseTask.ParseDataListener() {
			
			@Override
			public void onDataParsed(List<Article> articles) {
				Log.d("ParseDataListener", "onDataParsed!");
				mArticles = articles;
				mListFrag.update(articles);
				//mMainText.setText(articles.toString());
			}
		});
		
		xpt.execute(xmlStream);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemSelected(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setListActivateState() {
		// TODO Auto-generated method stub
		
	}

}
