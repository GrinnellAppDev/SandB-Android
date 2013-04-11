package edu.grinnell.sandb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.ArticleTable;
import edu.grinnell.sandb.img.DbImageGetter;

public class ArticleDetailFragment extends SherlockFragment {

	public static final String ARTICLE_ID_KEY = "article_id";
	private Article mArticle;
	
	public static final String TAG = "ArticleDetailFragment";
	
	public ArticleDetailFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		
		setHasOptionsMenu(true);

		Bundle b = (ofJoy == null) ? getArguments() : ofJoy;

		if (b != null) {
			int id = b.getInt(ARTICLE_ID_KEY);
			ArticleTable table = new ArticleTable(getSherlockActivity());
			table.open();
			Log.d(TAG, "Looking for article with id = " + id);
			mArticle = table.findById(id);	
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 

		View rootView = inflater.inflate(R.layout.fragment_article_detail,
				container, false);

		((TextView) rootView.findViewById(R.id.article_title)).setText(mArticle
				.getTitle());
		TextView body = (TextView) rootView.findViewById(R.id.article_body);
		// body.setText(Html.fromHtml(mArticle.getBody(), new
		// URLImageGetterAsync(body, getActivity()), null));

		// display = getSherlockActivity().getWindowManager().getDefaultDisplay();
		//Point size = new Point();
		//display.getSize(size);
		int width = 1280;
		int height = 720;
		
		DbImageGetter dbig = new DbImageGetter(getSherlockActivity(), width, height);
		body.setText(Html.fromHtml(mArticle.getBody(), dbig, null));

		Log.d(TAG, mArticle.getTitle());

		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mArticle == null) {
			// Navigate Up..
			Intent upIntent = new Intent(getSherlockActivity(), MainActivity.class);
			NavUtils.navigateUpTo(getSherlockActivity(), upIntent);
		}

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.article_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_settings:
			// startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
			break;
		case R.id.menu_share:
			share();
		default:
			break;
		}
		return false;
	}
	
	public void share(){

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");

		String shareBody = mArticle.getLink();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"S&B Article Link");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

		//Share to any compatible app on the device
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ARTICLE_ID_KEY, mArticle.getId());
		super.onSaveInstanceState(outState);
	}

}
