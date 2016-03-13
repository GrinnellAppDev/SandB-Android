package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by albertowusu-asare on 3/13/16.
 * Encapsulates the response body object received from the http call
 */
public class QueryResponse {
    public final String status;
    private  int count;
    @SerializedName("count_total")
    private int countTotal;
    private int pages;
    @SerializedName("posts")
    List<Article> articles;

    public QueryResponse(String status,int count,int countTotal,int pages, List<Article> posts) {
        this.status = status;
        this.articles = posts;
        this.count = count;
        this.countTotal = countTotal;
        this.pages = pages;
    }

    /**
     * @return the status of the article
     */
    public String getStatus() {return this.status;}

    /**
     * @return the count of posts queried
     */
    public int getCount(){return this.count;}
    /**
     * @return the total amount of posts in the system
     */
    public int getCountTotal(){return this.countTotal;}
    /**
     * @return the number of pages the holds the posts
     */

    public int getPages(){return this.pages;}
    /**
     * @return all the posts as queried from the call
     */
    public List<Article> getPosts(){return this.articles;}
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("{ ");
        sb.append(" status: " + this.status+ "\n");
        sb.append("   count: " + this.count+ "\n");
        sb.append("   countTotal: " + this.countTotal+ "\n");
        sb.append("   pages: " + this.pages+ "\n");
        sb.append("   posts: " + "[\n");
        for(Article article : this.articles){
            sb.append("            "+article);
        }
        sb.append("          ]\n");
        sb.append("}");
        return sb.toString();
    }
}
