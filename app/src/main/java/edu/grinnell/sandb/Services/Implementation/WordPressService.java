package edu.grinnell.sandb.Services.Implementation;

import java.util.Date;
import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;

/**
 * Created by albertowusu-asare on 3/16/16.
 */
public class WordPressService implements RemoteServiceAPI {
    @Override
    public Article getFirst() {
        return null;
    }

    @Override
    public List<Article> getAfter(Date date) {
        return null;
    }

    @Override
    public List<Article> getAll() {
        return null;
    }

    @Override
    public List<Article> getAll(int page, int count) {
        return null;
    }

    @Override
    public List<Article> getAll(List<String> fields) {
        return null;
    }
}
