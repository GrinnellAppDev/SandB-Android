package edu.grinnell.sandb;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.grinnell.grinnellsandb.R;

public class ArticleDetailActivity extends SherlockFragmentActivity {
	
	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		setContentView(R.layout.activity_article_detail);
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (ofJoy == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(ArticleDetailFragment.ARTICLE_ID_KEY,
                    getIntent().getIntExtra(ArticleDetailFragment.ARTICLE_ID_KEY, 0));
            ArticleDetailFragment fragment = new ArticleDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.article_detail_container, fragment)
                    .commit();
        }
	}
    
}
