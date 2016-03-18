package edu.grinnell.sanb.TestServices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.grinnell.sanb.TestUtils;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Implementation.ORMDbClient;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;

/**
 * Unit testing for the ORM client
 * @Author Albert Owusu-Asare
 * @Version 1.1 Thu Mar 17 15:51:19 CDT 2016
 */
@RunWith(MockitoJUnitRunner.class)
public class ORMDbClientTest {
    private LocalCacheClient localCacheClient;
    private Random random;

    @Before
    public void init(){
        localCacheClient = new ORMDbClient();
        random = new Random();
    }

    @Test
    public void saveArticlesTest(){
        localCacheClient.deleteAllEntries(Constants.TableNames.ARTICLE.toString());
        int numArticles = random.nextInt();


    }

}
