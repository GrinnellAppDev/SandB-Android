package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * @author  Albert Owusu-Asare
 * @version 1.1 Tue May  3 15:34:36 CDT 2016
 */
public class RealmImage extends RealmObject {
    @SerializedName("ID")
    private int id;
    @SerializedName("URL")
    private String url;
    private String guid;
    @SerializedName("mime_type")
    private String mimeType;
    private int width;
    private int height;

    public RealmImage(){}
    public RealmImage(int id, String url, String guid, String mimeType,int width, int height){
        this.id = id;
        this.url = url;
        this.guid = guid;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
    }

    /* Setters for the various private fields */
    public void setId(int id) {this.id = id;}
    public void setUrl(String url){this.url = url;}
    public void setGuid(String guid){this.guid = guid;}
    public void setMimeType(String mimeType){this.mimeType = mimeType;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}

    /* Getters for the various private fields */
    public int getId(){return this.id;}
    public String getUrl(){return this.url;}
    public String getMimeType(){return this.mimeType;}
    private int getWidth(){return this.width;}
    private int getHeight(){return this.height;}
}
