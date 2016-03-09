package edu.grinnell.sandb.Model;


import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

public class Article extends SugarRecord<Article> {
	//@SerializedName("id")
    protected int articleID;
	@SerializedName("title")
	protected String title;
	@SerializedName("url")
	protected String link;
	@SerializedName("date")
	protected String pubDate;
	@SerializedName("categories")
	protected List<ArticleCategory> categories;
	protected String category; //TODO : deprecate this field if not needed
	@SerializedName("excerpt")
	protected String description;
	@SerializedName("content")
	protected String body; //TODO : change body to content
	protected String comments;
	protected String aut;
	@SerializedName("author")
	protected ArticleAuthor author;

    public Article() {
    }

    public Article(String articleTitle, String articleBody) {
		title = articleTitle;
		body = articleBody;
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

}