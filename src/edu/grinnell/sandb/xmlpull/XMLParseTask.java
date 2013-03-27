package edu.grinnell.sandb.xmlpull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import edu.grinnell.sandb.Utility;
import edu.grinnell.sandb.data.Article;

public class XMLParseTask extends AsyncTask<InputStream, Void, List<Article>> {

	private Context mAppContext;
	private ParseDataListener mParseDataListener;
	private ProgressDialog mStatus;
	
	private static final String ns = null;
		
	public static final String XMP 	= "XMLParseTask";
	
	public XMLParseTask(Context appContext, ParseDataListener pdl) {
		super();
		mAppContext = appContext;
		mParseDataListener = pdl;		
	}

	/* Setup the loading dialog. */
	@Override
	protected void onPreExecute() {
		//mStatus = ProgressDialog.show(mAppContext,"","Parsing Feed...", true);
	}
	
	@Override
	protected List<Article> doInBackground(InputStream... arg0) {
				
		try {
			return parseArticlesFromStream(arg0[0]);
		} catch (IOException ioe) {
			Log.e(XMP, "parseArticlesFromStream", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(XMP, "parseArticlesFromStream", xppe);
		} catch (Exception e) {
			Log.e(XMP, "parseArticlesFromStream", e);
		}
		return new ArrayList<Article>();
	}
	
	/* Stop the dialog and notify the main thread that the new menu
	 * is loaded. */
	@Override
	protected void onPostExecute(List<Article> articles) {
		
		Log.i(XMP, "xml parsed!");
		
		// dismiss loading..
		//mStatus.dismiss();
		// notify the UI thread listener ..
		mParseDataListener.onDataParsed(articles);
		
		super.onPostExecute(articles);
	}
	
	protected static List<Article> parseArticlesFromStream(InputStream xmlstream)
		throws XmlPullParserException, IOException {
		
		
		Reader in = new InputStreamReader(xmlstream);
		
		 try {
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in);
	            parser.nextTag();
	            return readFeed(parser);
	        } finally {
	            in.close();
	        }	
	}
	
	private static List<Article> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Article> articles = new ArrayList<Article>();

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                continue;
            } else if (name.equals("item")) {
            	articles.add(readArticle(parser));
            } else {
                skip(parser);
            }
        }
        return articles;
    }
	
	// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private static Article readArticle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String guid = null;
        String link = null;
        String comments = null;
        String date = null;
        String category = null;
        String description = null;
        String body = null;
        
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = Utility.captializeWords(readTitle(parser));
            } else if (name.equals("guid")) {
                link = readGuid(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("comments")) {
                comments = readComments(parser);
            } else if (name.equals("date")) {
                date = readDate(parser);
            } else if (name.equals("category")) {
                category = readCategory(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("content:encoded")) {
                body = readBody(parser);
            } else {
                skip(parser);
            }
        }
        return new edu.grinnell.sandb.data.Article(title, guid, link, comments, description, date, category, body);
    }

	
	/* Listener you should implement for the callback method in the UI thread
	 * and pass to the constructor of GetMenuTask */
	public interface ParseDataListener {
		public void onDataParsed(List<Article> articles);
	}
	
	
    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    
 // Processes link tags in the feed.
    private static String readGuid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "guid");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "guid");
        return link;
    }

    // Processes link tags in the feed.
    private static String readComments(XmlPullParser parser) throws IOException, XmlPullParserException {
    	parser.require(XmlPullParser.START_TAG, ns, "comments");
        String commentsLink = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "comments");
        return commentsLink;
    }
    
    // Processes link tags in the feed.
    private static String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
    	parser.require(XmlPullParser.START_TAG, ns, "comments");
        String date = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "comments");
        return date;
    }
    
    // Processes description tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }
    
    // Processes description tags in the feed.
    private static String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category;
    }
    
    // Processes description tags in the feed.
    private static String readBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content:encoded");
        String body = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "content:encoded");
        return body;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    
}
