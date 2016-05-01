package edu.grinnell.sanb.TestServices;

import org.junit.Before;
import org.junit.Test;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Services.Implementation.WordPressService;

import static org.junit.Assert.assertTrue;

/**
 * This file contains unit tests for the WordPressService Client
 * @Author Albert Owusu-Asare
 * @Version Thu Mar 17 16:19:14 CDT 2016
 *
 */
public class WordPressServiceTest {
    private WordPressService restClient;
    @Before
    public void init(){
        this.restClient = new WordPressService();
    }

    @Test
    public void testGetFirst(){

    }
}
