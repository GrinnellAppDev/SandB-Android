package edu.grinnell.sanb.TestServices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.grinnell.sanb.TestUtils;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.WordPressService;
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
    @Mock  private WordPressService mockRemoteClient;
    @Mock private Article mockArticle;
    @Mock private List<Article> mockAllArticles;
    /* Allows the stubbing of methods  when necessary. Otherwise real objects are called. */
    @Spy private NetworkClient client;
    @Mock List<String> mockCategories;
    private static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;
    private final String ALL = "ALL";
    private Random random;

    @Before
    public void initMocks() {
        random = new Random();
       // mockRemoteClient = new WordPressService();
        client = new NetworkClient(mockLocalClient,mockRemoteClient);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetArticles(){
        String category = getRandomArticleCategory();
        List<Article> localClientQueryResults = new ArrayList<>();
        doNothing().when(client).updateLocalCache(category);
        doReturn(localClientQueryResults).when(mockLocalClient)
                .getArticlesByCategory(category.toLowerCase());
        doReturn(mockLocalClient.getArticlesByCategory(category.toLowerCase())).when(client).getArticles(category.toLowerCase());
        List<RealmArticle> results = client.getArticles(category);
        verify(client, times(1)).updateLocalCache(category);
        verify(mockLocalClient,times(1)).getArticlesByCategory(category.toLowerCase());
    }

    @Test
    public void updateLocalCacheFirstTimeCall(){
        String category = "All";
        Constants.FIRST_CALL_TO_UPDATE = true;
        client.updateLocalCache(category);
        verify(client,times(1)).firstTimeSyncLocalAndRemoteData();
        verify(client,times(0)).syncLocalAndRemoteData(category);
    }

    @Test
    public void updateLocalCacheNotFirstTimeCall(){
        String category = getRandomArticleCategory();
        Constants.FIRST_CALL_TO_UPDATE = false;
        client.updateLocalCache(category);
        verify(client,times(0)).firstTimeSyncLocalAndRemoteData();
        verify(client,times(1)).syncLocalAndRemoteData(category);
    }

    @Test
    public void firstTimeSyncLocalAndRemoteData(){
       // doNothing().when(mockRemoteClient).getAll(0,1,anyString());
        client.firstTimeSyncLocalAndRemoteData();
        verify(mockRemoteClient,times(1)).getAll(anyInt(),anyInt(),anyString());
    }

/*
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
    */
/*
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
        assertEquals("Expected numArticlesPerpage = Actual", expectedNumArticlesPerPage, actual);
    }

    /*
    @Test
    public void testUpdateLocalCacheWhenCacheEmpty(){
        mockEmptyCache(true);
        int currentPage = 0;
        client.updateLocalCache();
        List<Article> updates= new ArrayList<>();
        verify(mockRemoteClient,times(1)).getAll(currentPage, client.getNumArticlesPerPage());
        verify(mockLocalClient,times(1)).saveArticles(updates);
    }
    */

    /*
    @Test
    public void testUpdateLocalCacheWhenCacheNotEmpty(){
        mockEmptyCache(false);
        //TODO : Implement test Update when cache not empty
    }
    */




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

    private String getRandomArticleCategory(){
        String [] categories = Constants.CATEGORIES;
        int index =random.nextInt(categories.length -1);
        return categories[index];
    }


}