package edu.grinnell.sandb.img;

import com.orm.SugarRecord;

/* Class to store the article images and image titles in a db */
public class Image extends SugarRecord<Image> {

	protected String articleTitle;
	protected String url;
	protected String imgTitle;

    public Image(){}

	public Image(String articleTitle, String imageURL,
			String articleImgTitle) {
        this.articleTitle = articleTitle;
		url = imageURL;
		imgTitle = articleImgTitle;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public String getURL() {
		return url;
	}

	public String getImgTitle() {
		return imgTitle;
	}

}
