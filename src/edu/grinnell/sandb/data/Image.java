package edu.grinnell.sandb.data;

/* Class to store the article images and image titles in a db */
public class Image {

	protected int id;
	
	protected byte[] image;
	protected String imgTitle;

	protected Image(byte[] articleImage, String articleImgTitle){
		image = articleImage;
		imgTitle = articleImgTitle;
	}
	
	protected Image(int articleID, byte[] articleImage, String articleImgTitle){
		this(articleImage, articleImgTitle);
		id = articleID;
	}
	
	public int getId() {
		return id;
	}
	
	public byte[] getImg() {
		return image;
	}
	
	public String getImgTitle() {
		return imgTitle;
	}

}
