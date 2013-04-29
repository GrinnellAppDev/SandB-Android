package edu.grinnell.sandb.data;

import java.util.Date;

public class Article {

	protected int id;

	protected String guid;
	protected String title;
	protected String link;
	protected Date pubDate;
	protected String category;
	protected String description;
	protected String body;
	protected String comments;
	protected String author;

	
	protected Article (String articleTitle, String articleBody) {
		title = articleTitle;
		body = articleBody;
	}
	
	protected Article (String guid, String articleTitle, String articleLink,
			long publicationDate, String category, String description,
			String articleBody, String commentsLink, String author) {
		this(articleTitle, articleBody);
		this.guid = guid;
		this.link = articleLink;
		this.pubDate = new Date(publicationDate);
		this.category = category;
		this.description = description;
		this.comments = commentsLink;
		this.author = author;
	}
	
	protected Article (int id, String guid, String articleTitle, String articleLink,
			long publicationDate, String category, String description,
			String articleBody, String commentsLink, String author) {
		this(guid, articleTitle, articleLink, publicationDate, 
				category, description, articleBody, commentsLink, author);
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public String getGuid() {
		return guid;
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
	
	public Date getPubDate() {
		return pubDate;
	}
	
	public String getAuthor() {
		return author;
	}
}