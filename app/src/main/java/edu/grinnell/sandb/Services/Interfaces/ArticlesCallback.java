package edu.grinnell.sandb.Services.Interfaces;

import java.util.List;

import edu.grinnell.sandb.Model.Article;

/**
 * Created by prabir on 3/17/16, AppDev Grinnell.
 */
public interface ArticlesCallback {

    void onArticlesRetrieved(List<Article> articles);
}
