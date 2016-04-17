package edu.grinnell.sandb;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by prabir on 3/7/16, AppDev Grinnell.
 */
public class Constants {

    // WordPress JSON API endpoint URL
    public static final String JSON_API_URL = "http://www.thesandb.com/api/get_recent_posts?count=50/";

    // convert the font size saved in settings to sp
    public static final int[] FONT_SIZE_TO_SP = {12, 14, 16, 18};

    // a map : category title => key
    public static final Map<String, String> CATEGORY_TITLE_TO_KEY = new LinkedHashMap<>(); // LinkedHashMap retains insertion ordering
    // Fill in the a map to correspond to section tabs for the article list
    static {
        CATEGORY_TITLE_TO_KEY.put("All", null);
        CATEGORY_TITLE_TO_KEY.put("News", "News");
        CATEGORY_TITLE_TO_KEY.put("Arts", "Arts");
        CATEGORY_TITLE_TO_KEY.put("Community", "Community");
        CATEGORY_TITLE_TO_KEY.put("Features", "Features");
        CATEGORY_TITLE_TO_KEY.put("Opinion", "Opinion");
        CATEGORY_TITLE_TO_KEY.put("Sports", "Sports");
    }

    public static final String[] CATEGORIES =
            (String[]) CATEGORY_TITLE_TO_KEY.keySet()
                    .toArray(new String[CATEGORY_TITLE_TO_KEY.size()]);
}
