package edu.grinnell.sandb;

/**
 * Created by prabir on 3/7/16, AppDev Grinnell.
 */
public class Constants {

    // WordPress JSON API endpoint URL
    public static final String JSON_API_URL = "http://www.thesandb.com/api/get_recent_posts?count=50/";
    public static final int DEFAULT_NUM_ARTICLES_PER_PAGE = 50;

    // convert the font size saved in settings to sp
    public static final int[] FONT_SIZE_TO_SP = {12, 14, 16, 18};
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

}
