package edu.grinnell.sanb.TestServices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.grinnell.sanb.TestUtils;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;
import edu.grinnell.sandb.Util.StringUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;



/**
 * This file contains local unit tests for the NetworkClient Implementation
 * @Author Albert Owusu-Asare
 * @version 1.1 Thu Mar 17 00:43:03 CDT 2016
 */
@RunWith(MockitoJUnitRunner.class)
public class NetworkClientTest {

    @Mock private LocalCacheClient mockLocalClient;
    @Mock  private RemoteServiceAPI mockRemoteClient;
    @Mock private Article mockArticle;
    @Mock private List<Article> mockAllArticles;
    @Mock private NetworkClient client;
    @Mock List<String> mockCategories;
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;
    private final String ALL = "ALL";
    private Random random;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        random = new Random();
        client = new NetworkClient(mockLocalClient,mockRemoteClient);
    }

    @Test
    public void testGetArticles(){
        final int randomNumArticles = random.nextInt(4) +1;
        mockEmptyCache(true);
        when(mockLocalClient.getArticlesByCategory(ALL)).then(new CustomAnswer(randomNumArticles));
        List<Article> articles = client.getArticles(ALL);
        int expectedNumArticles = randomNumArticles;
        int actualNumArticles = articles.size();
        assertTrue("Expected size of articles returned ", expectedNumArticles == actualNumArticles);
    }

    @Test
    public void testGetNextPage(){
        final int currentPage = 0;
        final int numArticlesPerPage = random.nextInt(20);
        mockEmptyCache(true);
        when(mockLocalClient.getNextPage(ALL, currentPage, numArticlesPerPage))
                .then(new CustomAnswer(numArticlesPerPage));
        List<Article> articles = client.getNextPage(ALL, currentPage, numArticlesPerPage);
        int expectedNumArticles = numArticlesPerPage;
        int actualNumArticles = articles.size();
        assertTrue("Expected size of articles returned ", expectedNumArticles == actualNumArticles);
    }

    @Test
    public void testGetCategories (){
        mockEmptyCache(true);
        mockCategories.add("ALL");
        mockCategories.add("News");
        mockCategories.add("Sports");
        when(mockLocalClient.getCategories())
                .thenReturn(mockCategories);
        List<String> actualResult = client.getCategories();
        assertEquals("Expected List equals Actual list", actualResult, mockCategories);
    }

    @Test
    public void testSetAndGetNumArticlesPerPage(){
        int expectedNumArticlesPerPage = random.nextInt(19)+1;
        client.setNumArticlesPerPage(expectedNumArticlesPerPage);
        int actual = client.getNumArticlesPerPage();
        assertEquals("Expected numArticlesPerpage = Actual", expectedNumArticlesPerPage,actual);
    }

    @Test
    public void testUpdateLocalCacheWhenCacheEmpty(){
        mockEmptyCache(true);
        int currentPage = 0;
        client.updateLocalCache();
        List<Article> updates= new ArrayList<>();
        verify(mockRemoteClient,times(1)).getAll(currentPage, client.getNumArticlesPerPage());
        verify(mockLocalClient,times(1)).saveArticles(updates);
    }

    @Test
    public void testUpdateLocalCacheWhenCacheNotEmpty(){
        mockEmptyCache(false);
        //TODO : Implement test Update when cache not empty
    }



    /* Private Helper methods */
    private void mockEmptyCache(boolean val){
        when(mockLocalClient.isCacheEmpty()).thenReturn(val); //for the update method
    }

    private class CustomAnswer implements Answer<List<Article>> {
          int numArticles;
        CustomAnswer(int numArticles){
            this.numArticles = numArticles;
        }

        @Override
        public List<Article> answer(InvocationOnMock invocation) throws Throwable {
            List<Article> articles = TestUtils.generateRandomArticles(numArticles);
            return articles;
        }
    }

}