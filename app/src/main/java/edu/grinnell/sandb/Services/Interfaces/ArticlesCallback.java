package edu.grinnell.sandb.Services.Interfaces;

import java.util.List;

/**
 * Created by prabir on 3/17/16, AppDev Grinnell.
 */
public interface ArticlesCallback {

    void onArticlesRetrieved(List<Article> articles);
}
