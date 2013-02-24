package edu.grinnell.sandb.xmlpull;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;

public class XMLParseTask extends AsyncTask<String, Void, List<Article>> {

	private Context mAppContext;
	private ParseDataListener mParseDataListener;
	private ProgressDialog mStatus;
		
	public static final String XMP 	= "XMLParseTask";
	
	public XMLParseTask(Context context, ParseDataListener pdl) {
		super();
		mAppContext = context;
		mParseDataListener = pdl;		
	}

	/* Setup the progress bar. */
	@Override
	protected void onPreExecute() {
		mStatus = ProgressDialog.show(mAppContext,"","Parsing Feed...", true);
	}
	
	@Override
	protected List<Article> doInBackground(String... arg0) {
		
		
		List<Article> articles;
		
		articles = parseArticlesFromString(arg0[0]);
		
		return articles;
	}
	
	/* Stop the dialog and notify the main thread that the new menu
	 * is loaded. */
	@Override
	protected void onPostExecute(List<Article> articles) {
		
		Log.i(XMP, "xml parsed!");
		
		try {
			// dismiss loading..
			mStatus.dismiss();
			// notify the UI thread listener ..
			mParseDataListener.onDataParsed(articles);
		} catch (Exception e) {
			Log.d(XMP, e.toString());
		}
		
		
		super.onPostExecute(articles);
	}
	
	protected static List<Article> parseArticlesFromString(String xmlstr) {
		List<Article> articles = new ArrayList<Article>();
		
		return articles;
	}

	
	/* Listener you should implement for the callback method in the UI thread
	 * and pass to the constructor of GetMenuTask */
	public interface ParseDataListener {
		public void onDataParsed(List<Article> articles);
	}
	
	public class Article {
		private String title;
		private String body;
		
		public Article (String articleTitle, String articleBody) {
			title = articleTitle;
			body  = articleBody;
		}
		public Article() {this("", "");}
		public String getTitle() {return title;}
		public String getBody() {return body;}
		public Article setTitle(String t) {title = t; return this;}
		public Article setBody(String b) {body = b; return this;}
	}
}
