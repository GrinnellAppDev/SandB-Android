package edu.grinnell.sandb.data;


import com.orm.SugarRecord;

public class Article extends SugarRecord<Article> {

	protected int articleID;

	protected String guid;
	protected String title;
	protected String link;
	protected String pubDate;
	protected String category;
	protected String description;
	protected String body;
	protected String comments;
	protected String author;

    public Article() {
    }

    public Article(String articleTitle, String articleBody) {
		title = articleTitle;
		body = articleBody;
	}

	public Article(String guid, String articleTitle, String articleLink,
			String publicationDate, String category, String description,
			String articleBody, String commentsLink, String author) {
		this(articleTitle, articleBody);
		this.guid = guid;
		this.link = articleLink;
		this.pubDate = publicationDate;
		this.category = category;
		this.description = description;
		this.comments = commentsLink;
		this.author = author;
	}

    public Article(int id, String guid, String articleTitle,
			String articleLink, String publicationDate, String category,
			String description, String articleBody, String commentsLink,
			String author) {
		this(guid, articleTitle, articleLink, publicationDate, category,
				description, articleBody, commentsLink, author);
		this.articleID = id;
	}

	public int getArticleID() {
		return articleID;
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

	public String getPubDate() {
		return pubDate;
	}

	public String getAuthor() {
		return author;
	}
}