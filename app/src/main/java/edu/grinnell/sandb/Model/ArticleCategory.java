package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class ArticleCategory {
    String title;
    public ArticleCategory(String title){
        this.title = title;
    }
    /**
     * @return the title of the category
     */
    public String getTitle(){return this.title;}
    @Override
    public String toString(){
        return getTitle();
    }
}
