package edu.grinnell.sandb.util;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.Image;

public class DatabaseUtil {

    public static List<Image> getArticleImages(Article article) {
        Select<Image> imageQuery = Select.from(Image.class).where(Condition.prop("article_Title").eq(article.getTitle()));
        return imageQuery.list();
    }

    public static Image getArticleImage(Article article) {
        Select<Image> imageQuery = Select.from(Image.class).where(Condition.prop("article_Title").eq(article.getTitle()));
        return imageQuery.first();
    }

    public static List<Article> getArticleList() {
        return Article.listAll(Article.class);
    }

    public static Article getArticle(long id) {
        return Article.findById(Article.class, id);
    }

}
