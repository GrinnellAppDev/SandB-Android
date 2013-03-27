package edu.grinnell.sandb.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Article {

	private int id;

	private String guid;
	private String title;
	private String link;
	private Date pubDate;
	private String category;
	private String description;
	private String body;
	private String comments;

	private static final String TAG = "Article";
	
	public Article (String articleTitle, String articleBody) {
		title = articleTitle;
		body = articleBody;
	}
	
	public Article (String articleTitle, String guid, String articleLink, String commentsLink,
			String description, String publicationDate, String category, String articleBody) {
		this(articleTitle, articleBody);
		this.guid = guid;
		this.link = articleLink;
		this.description = description;
		this.category = category;

		try {
			if (publicationDate != null) {
				this.pubDate = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ZZZZ", 
						Locale.ENGLISH).parse(publicationDate);
			} else {
				this.pubDate = new Date();
			}
		} catch (ParseException pe) {
			Log.d(TAG, "new Article", pe);
		}
	}
	
	public Article() {this("", "", "", "", "", "", "", "");}

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

	public Date getPubDate() {
		return pubDate;
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
}