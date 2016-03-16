package edu.grinnell.sandb.Services.Implementation;

import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;

/**
 * Created by albertowusu-asare on 3/16/16.
 */
public class ORMDbClient implements LocalCacheClient {
    @Override
    public boolean saveArticles(List<Article> articles) {
        return false;
    }

    @Override
    public boolean saveArticle(Article article) {
        return false;
    }

    @Override
    public Article getFirst() {
        return null;
    }

    @Override
    public List<Article> getArticlesByCategory(String categoryName) {
        return null;
    }

    @Override
    public List<String> getCategories() {
        return null;
    }

    @Override
    public List<Article> getNextPage(int currentPageNumber, int lastArticleId) {
        return null;
    }

    @Override
    public boolean isCacheEmpty() {
        return false;
    }
}
