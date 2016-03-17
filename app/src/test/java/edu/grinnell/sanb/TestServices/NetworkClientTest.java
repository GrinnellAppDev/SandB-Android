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
import java.util.List;
import java.util.Random;

import edu.grinnell.sanb.TestUtils;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
    private Random random;
    @Mock private NetworkClient client;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        random = new Random();
        client = new NetworkClient(mockLocalClient,mockRemoteClient);
    }

    @Test
    public void testGetArticlesALLWhenLocalCacheEmpty(){
        final int randomNumArticles = random.nextInt(4) +1;
        final String ALL = "ALL";
        when(mockLocalClient.isCacheEmpty()).thenReturn(true); //for the update method
        when(mockLocalClient.getArticlesByCategory(ALL)).then(new Answer<List<Article>>() {
            @Override
            public List<Article> answer(InvocationOnMock invocation) throws Throwable {
                List<Article> articles = TestUtils.generateRandomArticles(randomNumArticles);
                return articles;
            }
        });
        List<Article> articles = client.getArticles(ALL);
        int expectedNumArticles = randomNumArticles;
        int actualNumArticles = articles.size();
        assertTrue("Expected size of articles returned ",expectedNumArticles == actualNumArticles);
    }



}