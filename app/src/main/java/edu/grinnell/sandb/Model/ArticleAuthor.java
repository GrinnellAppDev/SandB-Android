package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class ArticleAuthor {
    private String name;
    public ArticleAuthor(String name){
        this.name = name;
    }
    /**
     * @return the name of the Author
     */
    public String getName(){
        return this.name;
    }
    @Override
    public String toString(){
        return getName();
    }

}
