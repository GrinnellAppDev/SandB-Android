package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class SandBResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("count")
    private int count;
    @SerializedName("count_total")
    private int countTotal;
    @SerializedName("pages")
    private int pages;
    @SerializedName("posts")
    private List<Article> articles;

    SandBResponse(String status, int count, int countTotal, int pages, List<Article> articles){
        this.status = status;
        this.count = count;
        this.countTotal = countTotal;
        this.pages = pages;
        this.articles = articles;
    }
    public List<Article> getArticles(){ return this.articles;}
}
