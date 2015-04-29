package edu.grinnell.sandb.util;

import edu.grinnell.sandb.model.Article;
import edu.grinnell.sandb.model.Image;

public class BodyImageGetter {

	// Read an image from the body as a byte array
	public static void readImages(Article article) {

        addImage(article , "<div");
//		addImage(body, articleID, "<a");
        addImage(article, "<img");
	}

	private static void addImage(Article article, String tag) {
        Image newImage = new Image();
        String body = article.getBody();
		int tagStart = 0;
		String url = "";
		String title = "";

		while ((tagStart = body.indexOf(tag, tagStart + 1)) >= 0) {
			url = getSubstring("src=\"", body, tagStart);
            newImage = new Image(article.getTitle(), url, title);
		}

        if (newImage.getURL() != null) {
            if (!DatabaseUtil.isImageCached(newImage.getURL())) {
                newImage.save();
            }
        }
	}

	// return a string starting immediately after the key, and ending at the
	// first quotation mark
	private static String getSubstring(String key, String body, int start) {
		int subStart = 0;
		int subEnd = 0;
		String substring = "";

		// start at beginning of link
		subStart = body.indexOf(key, start) + key.length();

		if (subStart >= 0) {
			subEnd = body.indexOf("\"", subStart);
		}
		
		if (subEnd >= subStart) {
			substring = body.substring(subStart, subEnd);
		}

		return substring;
	}
}
