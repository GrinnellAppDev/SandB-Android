package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class ArticleAuthor {
    //private int id;
    @SerializedName("name")
    private String name;
    private String firstName;
    private String lastName;

}
