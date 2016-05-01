package edu.grinnell.sanb.TestServices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.ArticleAuthor;
import edu.grinnell.sandb.Services.Implementation.SugarDbClient;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


/**
 * Unit testing for the ORM client
 * @Author Albert Owusu-Asare
 * @Version 1.1 Thu Mar 17 15:51:19 CDT 2016
 */
@RunWith(MockitoJUnitRunner.class)
public class SugarDbClientTest {
    @Mock private Article article;
    @Mock private ArticleAuthor author;
    /* Allows the stubbing of methods  when necessary. Otherwise real methods are called. */
    @Spy private SugarDbClient sugarDbClient;
    private Random random;

    @Before
    public void setUp(){
        sugarDbClient = new SugarDbClient();
        random = new Random();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    /*
     * Tests the saving of an Article.
     *
     * <p> The behavior of setting the article category is mocked. we verify that indeed
     * the category field was set and the article was saved. </p>
     */
    public void saveArticleTest(){
        String category = "SPORTS";
        when(article.getAuthor()).thenReturn(author);
        when(article.getAuthor().getName()).thenReturn(category);
        doCallRealMethod().when(article).getCategory();
        sugarDbClient.saveArticle(article);
        verify(article, times(1)).setCategory(category);
        verify(article, times(1)).save();
    }

    /*
     * Verify that indeed the saveArticle method is called some number of times.
     */
    @Test
    public void saveArticlesTest(){
        List<Article> articles =  new ArrayList<>();
        for(int i = 0; i < 10 ; i++)
            articles.add(article);
        doNothing().when(sugarDbClient).saveArticle(article);
        sugarDbClient.saveArticles(articles);
        verify(sugarDbClient, times(1)).saveArticle(article);
    }

    /*
    Verifies that when null argument passed, the getAll() method is called once and that the results
    of the getAll() call are returned.
   */
    @Test
    public void getArticlesByCategoryNullCategory(){
        String category = null;
        List<Article> getAllReturnList = new ArrayList();
        doReturn(getAllReturnList).when(sugarDbClient).getAll();
        sugarDbClient.getArticlesByCategory(category);
        verify(sugarDbClient,times(1)).getAll();
    }

    /*
      Verifies that when "ALL" argument passed, the getAll() method is called once and that the results
    of the getAll() call are returned.

    @Test
    public void getArticlesByCategoryAllCategory(){
        String category = "ALL ";
        List<Article> getAllReturnList = new ArrayList();
        doReturn(getAllReturnList).when(sugarDbClient).getAll();
        sugarDbClient.getArticlesByCategory(category);
        verify(sugarDbClient,times(1)).getAll();
    }
    */







}
