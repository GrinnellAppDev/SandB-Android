package edu.grinnell.sandb.Services;





import java.util.List;

import edu.grinnell.sandb.Model.Article;

import static junit.framework.Assert.assertTrue;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class NetworkClientTest {
    //@Test
    public void testGetArticles(){
        Article.deleteAll(Article.class);
        SandBNetClient client = new SandBNetClient();
        client.getArticles(2);
      List<Article> articles = Article.listAll(Article.class);
        assertTrue("Check that articles is not empty :"  ,!articles.isEmpty());
    }

}
