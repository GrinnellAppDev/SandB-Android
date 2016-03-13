package edu.grinnell.sandb;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import edu.grinnell.sandb.Model.Article;

import static junit.framework.Assert.assertTrue;

/**
 * Created by albertowusu-asare on 3/12/16.
 */
public class SandBNetClientTest {
    @Test
    public void getArticlesTest(){
        SandBNetClient client = new SandBNetClient();
        Random random = new Random();
        int numArticles = random.nextInt(10);
        List<Article> articles =client.getArticles(numArticles);
        assertTrue("Num articles = expected",articles.size() == numArticles);
    }
}
