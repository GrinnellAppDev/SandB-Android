package edu.grinnell.sandb.util;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.model.Article;
import edu.grinnell.sandb.model.Image;

public class DatabaseUtil {

    public static List<Image> getArticleImages(Article article) {
        Select<Image> imageQuery = Select.from(Image.class).where(Condition.prop("article_Title").eq(article.getTitle()));
        return imageQuery.list();
    }

    public static Image getArticleImage(Article article) {
        Select<Image> imageQuery = Select.from(Image.class).where(Condition.prop("article_Title").eq(article.getTitle()));
        return imageQuery.first();
    }

    public static boolean isImageCached(String url) {
        Select<Image> imageQuery = Select.from(Image.class).where(Condition.prop("url").eq(url));
        if (imageQuery.first() == null) {
            return false;
        }
        else return true;
    }

    public static List<Article> getArticlesByCategory(String category) {
        if (category == null) {
            return getArticleList();
        }
        else {
            Select<Article> categoryQuery = Select.from(Article.class).where(Condition.prop("category").eq(category));
            List<Article> articles = categoryQuery.list();
            if (articles == null) {
                return new ArrayList<>();
            } else return articles;
        }
    }

    public static List<Article> getArticleList() {
        return Article.listAll(Article.class);
    }

    public static Article getArticle(long id) {
        return Article.findById(Article.class, id);
    }

}
