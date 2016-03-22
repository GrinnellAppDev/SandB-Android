package edu.grinnell.sandb;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Relevant Constants used throughout the appication;
 * @Author prabir on 3/7/16, AppDev Grinnell.
 * @Author Albert Owusu-Asare
 */
public class Constants {

    /* convert the font size saved in settings to sp */
    public static final int[] FONT_SIZE_TO_SP = {12, 14, 16, 18};

    /* Remote Service Constants */
    public static final String PUBLIC_API =
            "https://public-api.wordpress.com/rest/v1.1/sites/www.thesandb.com/";
    public static final String JSON_API_URL =
            "http://www.thesandb.com/api/get_recent_posts?count=50/";
    public static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;

    /* Numeric Constants */
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int FIRST_PAGE = ONE;

    /* Table Names */

    public enum TableNames{
        ARTICLE("Article"),
        CATEGORY("Category");
        private final String name;

        private TableNames(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /* Snack Bar Messages */

    public enum SnackBarMessages{
        CONNECTED("Connection Established");
        private final String name;

        private SnackBarMessages(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /* Application state constants */
    public static boolean FIRST_CALL_TO_UPDATE = true;

    /* Article Category constants */
    public static String ARTICLE_CATEGORY_KEY = "category";
    public static final Map<String, String> titleToKey = new LinkedHashMap<String, String>(); // LinkedHashMap retains insertion ordering
    public static final String[] CATEGORIES;

    static {
        titleToKey.put("All", "All");
        titleToKey.put("News", "News");
        titleToKey.put("Arts", "Arts");
        titleToKey.put("Community", "Community");
        titleToKey.put("Features", "Features");
        titleToKey.put("Opinion", "Opinion");
        titleToKey.put("Sports", "Sports");
        CATEGORIES = titleToKey.keySet().toArray(new String[titleToKey.size()]);
    }

}
