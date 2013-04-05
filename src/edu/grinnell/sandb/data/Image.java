package edu.grinnell.sandb.data;

/* Class to store the article images and image titles in a db */
public class Image {

	protected int id;
	protected int articleId;
	protected String url;
	protected byte[] image;
	protected String imgTitle;

	protected Image(int ArticleID, String articleURL, byte[] articleImage,
			String articleImgTitle) {
		articleId = ArticleID;
		url = articleURL;
		image = articleImage;
		imgTitle = articleImgTitle;
	}

	protected Image(int ID, int ArticleID, String articleURL,
			byte[] articleImage, String articleImgTitle) {
		this(ArticleID, articleURL, articleImage, articleImgTitle);
		id = ID;
	}

	public int getId() {
		return id;
	}

	public int getArticleId() {
		return articleId;
	}

	public String getURL() {
		return url;
	}

	public byte[] getImg() {
		return image;
	}

	public String getImgTitle() {
		return imgTitle;
	}

}
