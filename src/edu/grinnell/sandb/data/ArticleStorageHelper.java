package edu.grinnell.sandb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ArticleStorageHelper extends SQLiteOpenHelper {

	public static final String TABLE_ARTICLES = "articles";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_PUBDATE = "pubdate";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_BODY = "body";
	public static final String COLUMN_COMMENTS = "comments";

	private static final String DATABASE_NAME = "articles.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	    + TABLE_ARTICLES + "(" + COLUMN_ID
	    + " integer primary key autoincrement, " 
	    + COLUMN_CATEGORY + " text, " 
	    + COLUMN_TITLE + " text not null, "
	    + COLUMN_LINK + " text, "
	    + COLUMN_PUBDATE + "integer, "
	    + COLUMN_DESCRIPTION + "text, " 
	    + COLUMN_BODY + "text not null, "
	    + COLUMN_COMMENTS + "text, "
	    + " " + ");";

	public ArticleStorageHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	public ArticleStorageHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ArticleStorageHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		    onCreate(db);
	}

}
