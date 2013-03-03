package edu.grinnell.sandb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.WebRequestTask;
import edu.grinnell.sandb.xmlpull.WebRequestTask.Result;
import edu.grinnell.sandb.xmlpull.XMLParseTask;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;
import edu.grinnell.sandb.xmlpull.XmlContent;

public class MainActivity extends FragmentActivity implements ArticleListFragment.Callbacks {

	public String FEED_URL = "http://www.thesandb.com/feed";
	
	private WebRequestTask mWRT;
	
	private ArticleListFragment mListFrag;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListFrag = (ArticleListFragment) 
				getSupportFragmentManager().findFragmentById(R.id.articles_fragment);
		XmlContent.articles = new ArrayList<Article>();
		
		if (XmlContent.articles != null && XmlContent.articles.size() > 0)
			mListFrag.update(XmlContent.articles);
		else {
		
			mWRT = new WebRequestTask(this, 
					new WebRequestTask.RetrieveDataListener() {
				
				@Override
				public void onRetrieveData(Result result) {
					
					InputStream s = result.getStream();
					Log.d("DataReceived", "resultingStream: " + result.getStream());
	
					//mMainText.setText(result.getStream().toString());
					if (s != null)
						parseXmlFromStream(result.getStream());
					else {
						mListFrag.setEmptyText(getString(R.string.no_articles));
						Log.d("DataReceived", "stream is NULL!");
					}
				}
			});
			
			mWRT.execute(FEED_URL);
		}
		
	}
	
	private void parseXmlFromStream(InputStream xmlStream) {
		XMLParseTask xpt= new XMLParseTask(this, 
				new XMLParseTask.ParseDataListener() {
			
			@Override
			public void onDataParsed(List<Article> articles) {
				Log.d("ParseDataListener", "onDataParsed!");
				XmlContent.articles = articles;
				mListFrag.update(articles);
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
	public void onItemSelected(int position) {
		if(mTwoPane) {
			// add two pane layout
		} else {
			Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
            detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY, position);
            startActivity(detailIntent);
		}		
	}

	@Override
	public void setListActivateState() {
		// TODO Auto-generated method stub
		
	}

}
