package edu.grinnell.sandb.Model;


import com.orm.SugarRecord;

public class Article extends SugarRecord<Article> {

    protected int articleID;

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

	public Article(String articleTitle, String articleLink,
			String publicationDate, String category, String description,
			String articleBody, String commentsLink, String author) {
		this(articleTitle, articleBody);
		this.link = articleLink;
		this.pubDate = publicationDate;
		this.category = category;
		this.description = description;
		this.comments = commentsLink;
		this.author = author;
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
		return author;
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
        this.author = author;
    }

}