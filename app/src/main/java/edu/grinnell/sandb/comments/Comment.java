package edu.grinnell.sandb.comments;

import com.orm.SugarRecord;

public class Comment extends SugarRecord<Comment> {

	protected int commentId;
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
		this.commentId = id;
	}

	public int getCommentId() {
		return commentId;
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
