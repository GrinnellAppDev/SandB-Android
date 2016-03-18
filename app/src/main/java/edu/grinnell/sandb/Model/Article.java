package edu.grinnell.sandb.Model;


import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

public class Article extends SugarRecord<Article> {
	@SerializedName("id")
    protected int articleID;
	protected String title;
	@SerializedName("url")
	protected String link;
	@SerializedName("content")
	protected String body; //TODO : change body to content
	@SerializedName("date")
	protected String pubDate;
	@SerializedName("excerpt")
	protected String description;
	protected List<ArticleCategory> categories;
	protected ArticleAuthor author;

	/*Deprecate */
	protected String category; //TODO : deprecate this field if not needed
	protected transient String comments;
	protected String aut;


    public Article() {
    }

    public Article(String articleTitle, String articleBody) {
		title = articleTitle;
		body = articleBody;

	}


	public Article(int id,String url, String title,String content,String date,String excerpt,
				   List<ArticleCategory> categories, ArticleAuthor author){
		this.articleID = id;
		this.link = url;
		this.title = title;
		this.body = content;
		this.pubDate = date;
		this.description = excerpt;
		this.categories = categories;
		this.author = author;
	}

	public Article(String articleTitle, String articleLink,
			String publicationDate, String category, String description,
			String articleBody, String commentsLink, String author) {
		this(articleTitle, articleBody);
		this.link = articleLink;
		this.pubDate = publicationDate;
		this.category = category;
		this.description = description;
		this.comments = commentsLink;
		//this.author = author;
	}

	public int getArticleID() {
		return articleID;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getComments() {
		return comments;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getBody() {
		return body;
	}

	public String getPubDate() {
		return pubDate;
	}

	public String getAuthor() {
		//return author;
		return aut;
	}


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

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setAuthor(String author) {
      //  this.author = author;
		this.aut = author;
    }

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{ ");
		sb.append(" id: " + this.id);
		sb.append("   url: " + this.link);
		sb.append("   title: " + this.title);
		sb.append("   content: " + this.body);
		sb.append("   excerpt: " + this.description);
		sb.append("   date: " + this.pubDate);
		sb.append("   categories: " + this.categories);
		sb.append("   author " + this.author);
		sb.append("}\n");
		return sb.toString();
	}




}