package edu.grinnell.sanb;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Model.Article;

/**
 * Created by albertowusu-asare on 3/17/16.
 */
public class TestUtils {
    /**
     * Generates mock Articles for testing
     * @param numArticles
     * @return
     */
    public static List<Article> generateRandomArticles(int numArticles) {
        List<Article> articles = new ArrayList<>();
        for(int i =0; i < numArticles;i++){
            articles.add(generateArticle(i));
        }
        return articles;
    }

    /**
     * Generates a sample article for testing
     * @param num
     * @return
     */
    public static Article generateArticle(int num){
        String articleTitle = "Title " +num;
        String articleBody = "Body " +num;
        Article article = new Article(articleTitle,articleBody);
        return article;
    }

}
