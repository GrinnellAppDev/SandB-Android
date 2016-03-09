package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class ArticleCategory {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
}
