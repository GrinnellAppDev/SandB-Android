package edu.grinnell.sandb.comments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.grinnell.sandb.img.ImageStorageHelper;

public class CommentStorageHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_BODY = "text";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_ARTICLE_URL = "url";

	private static final String DATABASE_NAME = "comments.db";
	private static final int DATABASE_VERSION = 11;

	
	// Database creation sql statement
	public static final String DATABASE_CREATE = "create table "
	    + TABLE_COMMENTS + "(" + COLUMN_ID
	    + " integer primary key, " 
	    + COLUMN_URL + " text, "
	    + COLUMN_DATE + " date, "
	    + COLUMN_BODY + " text, " 
	    + COLUMN_AUTHOR + " text "
	    + COLUMN_ARTICLE_URL + " text "
	    + ");";

	public CommentStorageHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ImageStorageHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		    onCreate(db);
	}

}
