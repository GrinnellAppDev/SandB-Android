package edu.grinnell.sandb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.img.DbImageGetter;
import edu.grinnell.sandb.xmlpull.FeedContent;


public class ArticleDetailFragment extends Fragment {

	public static final String ARTICLE_ID_KEY = "article_id";
	private static final String ADF = "ArticleDetailFragment";
	private Article mArticle;

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		
		Bundle b = getArguments();
		
		if (b != null && FeedContent.articles != null) {
			mArticle = FeedContent.articles.get(b.getInt(ARTICLE_ID_KEY, 0));
		} else {
			// Navigate Up..
			Intent upIntent = new Intent(getActivity(), MainActivity.class);
			NavUtils.navigateUpTo(getActivity(), upIntent);
		}
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        
        ((TextView) rootView.findViewById(R.id.article_title)).setText(mArticle.getTitle());
        TextView body = (TextView) rootView.findViewById(R.id.article_body);
        //body.setText(Html.fromHtml(mArticle.getBody(), new URLImageGetterAsync(body, getActivity()), null));
                
        body.setText(Html.fromHtml(mArticle.getBody(), new DbImageGetter(getActivity()), null));
        
        Log.d(ADF, mArticle.getTitle());
        
        return rootView;
    }
	
}
