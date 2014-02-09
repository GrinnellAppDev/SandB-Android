package edu.grinnell.sandb.comments;

public class Comment {

	protected int id;
	protected String link;
	protected String postDate;
	protected String body;
	protected String author;
	protected String articleLink;

	public Comment(String link, String postDate, String body,
			String author, String article) {
		this.link = link;
		this.postDate = postDate;
		this.body = body;
		this.author = author;
		this.articleLink = article;
	}
	
	public Comment(int id, String link, String postDate, String body,
			String author, String article) {
		this(link, postDate, body, author, article);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getLink() {
		return link;
	}

	public String getPostDate() {
		return postDate;
	}

	public String getBody() {
		return body;
	}

	public String getAuthor() {
		return author;
	}
	
	public String getArticleLink() {
		return articleLink;
	}
}