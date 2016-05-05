package edu.grinnell.sandb.Model;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.text.ParseException;
import java.util.Date;

import edu.grinnell.sandb.Util.ISO8601;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmArticle extends RealmObject {
<<<<<<< HEAD
    @SerializedName("ID")
    @PrimaryKey
    protected int articleID;
    protected String title;
    @SerializedName("URL")
    protected String link;
    @SerializedName("content")
    protected String body; //TODO : change to content
    @SerializedName("date")
    protected String pubDate;
    protected Date realmDate;
    @SerializedName("excerpt")
    protected String description;
    protected String category;
    @SerializedName("featured_image")
    protected String featuredImgUrl;

    @Ignore
    protected RealmArticleAuthor author;

    public RealmArticle() {
    }
=======
        @SerializedName("ID")
        @PrimaryKey
        protected int articleID;
        protected String title;
        @SerializedName("URL")
        protected String link;
        @SerializedName("content")
        protected String body; //TODO : change to content
        @SerializedName("date")
        protected String pubDate;
        protected Date realmDate;
        @SerializedName("excerpt")
        protected String description;
        protected String category;
        @SerializedName("featured_image")
        String thumbnailUrl;
        @Ignore
        protected RealmArticleAuthor author;

        public RealmArticle() {
        }
>>>>>>> httpClientIntegrationRealm

    public RealmArticle(String articleTitle, String articleBody) {
        title = articleTitle;
        body = articleBody;

    }

<<<<<<< HEAD
    public RealmArticle(int id, String url, String title, String content, String date, String excerpt
            , RealmArticleAuthor author) {
        this.articleID = id;
        this.link = url;
        this.title = title;
        this.body = content;
        this.pubDate = date;
        this.description = excerpt;
        //this.categories = categories;
        this.author = author;
    }
=======
        public RealmArticle(int id,String url, String title,String content,String date,String excerpt
                , RealmArticleAuthor author){
            this.articleID = id;
            this.link = url;
            this.title = title;
            this.body = content;
            this.pubDate = date;
            this.description = excerpt;
            this.author = author;
        }
>>>>>>> httpClientIntegrationRealm

    public RealmArticle(String articleTitle, String articleLink,
                        String publicationDate, String description,
                        String articleBody, String author) {
        this(articleTitle, articleBody);
        this.link = articleLink;
        this.pubDate = publicationDate;
        this.description = description;
    }

    public int getArticleID() {
        return articleID;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }


    public RealmArticleAuthor getAuthor() {
        return this.author;
    }


    public String getDescription() {
        return description;
    }

    public String getFeaturedImgUrl() {
        return featuredImgUrl;
    }

<<<<<<< HEAD
    public Date getRealmDate() {
        return this.realmDate;
    }
=======
        public String getThumbnailUrl(){return this.thumbnailUrl;}

        public String getBody() {
            return body;
        }
>>>>>>> httpClientIntegrationRealm

    public String getBody() {
        return body;
    }

<<<<<<< HEAD
    public String getPubDate() {
        return pubDate;
    }
=======

        public void setArticleID(int articleID) {
            this.articleID = articleID;
        }
>>>>>>> httpClientIntegrationRealm

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBody(String body) {
        this.body = body;
    }

<<<<<<< HEAD
    public void setFeaturedImgUrl(String url) {
        this.featuredImgUrl = url;
    }

    public void setRealmDate(final String iso8601Date) {
        try {
            this.realmDate = ISO8601.toDate(iso8601Date);
        } catch (ParseException e) {
            this.realmDate = null;
=======
        public void setThumbnailUrl(String thumbnailUrl){this.thumbnailUrl = thumbnailUrl;}

        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("{ ");
            //sb.append(" id: " + this.id);
            sb.append("   url: " + this.link);
            sb.append("   title: " + this.title);
            sb.append("   content: " + this.body);
            sb.append("   excerpt: " + this.description);
            sb.append("   date: " + this.pubDate);
            //sb.append("   categories: " + this.categories);
            sb.append("   author " + this.author);
            sb.append("}\n");
            return sb.toString();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof Article))
                return false;
            Article other = (Article) obj;
            return pubDate == null ? other.getPubDate() == null : pubDate.equals(other.getPubDate());
>>>>>>> httpClientIntegrationRealm
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ ");
        //sb.append(" id: " + this.id);
        sb.append("   url: " + this.link);
        sb.append("   title: " + this.title);
        sb.append("   content: " + this.body);
        sb.append("   excerpt: " + this.description);
        sb.append("   date: " + this.pubDate);
        //sb.append("   categories: " + this.categories);
        sb.append("   author " + this.author);
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Article))
            return false;
        Article other = (Article) obj;
        return pubDate == null ? other.getPub_date() == null : pubDate.equals(other.getPub_date());
    }

}
