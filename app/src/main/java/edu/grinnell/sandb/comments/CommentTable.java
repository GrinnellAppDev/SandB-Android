package edu.grinnell.sandb.comments;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommentTable {

	private static final String TAG = "CommentTable";

	// Database fields
	private SQLiteDatabase database;
	private CommentStorageHelper dbHelper;
	private String[] allColumns = { CommentStorageHelper.COLUMN_ID,
			CommentStorageHelper.COLUMN_URL, CommentStorageHelper.COLUMN_DATE,
			CommentStorageHelper.COLUMN_BODY,
			CommentStorageHelper.COLUMN_AUTHOR,
			CommentStorageHelper.COLUMN_ARTICLE_URL, };

	public CommentTable(Context context) {
		dbHelper = new CommentStorageHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void deleteComment(Comment comment) {
		long id = comment.getId();
		Log.i(TAG, "Image deleted with id: " + id);
		database.delete(CommentStorageHelper.TABLE_COMMENTS,
				CommentStorageHelper.COLUMN_ID + " = " + id, null);
	}

	public Comment findByUrl(String url) {
		Log.i("ImageTable.findByUrl", "url: " + url);
		Cursor cursor = database.query(CommentStorageHelper.TABLE_COMMENTS,
				allColumns, CommentStorageHelper.COLUMN_URL + " = '" + url
						+ "'", null, null, null, null);

		Comment cmt = null;
		cursor.moveToFirst();
		if (!cursor.isAfterLast())
			cmt = cursorToComment(cursor);

		// Make sure to close the cursor
		cursor.close();
		return cmt;
	}

	public List<Comment> findByArticleURL(String articleURL) {
		Cursor cursor = database.query(CommentStorageHelper.TABLE_COMMENTS,
				allColumns, CommentStorageHelper.COLUMN_ARTICLE_URL + " = "
						+ articleURL, null, null, null, null);

		List<Comment> comments = new ArrayList<Comment>();
		Comment cmt = null;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			cmt = cursorToComment(cursor);
			comments.add(cmt);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	public Comment createComment(String url, String date, String body,
			String author, String article_url) {

		ContentValues values = new ContentValues();
		values.put(CommentStorageHelper.COLUMN_URL, url);
		values.put(CommentStorageHelper.COLUMN_DATE, date);
		values.put(CommentStorageHelper.COLUMN_BODY, body);
		values.put(CommentStorageHelper.COLUMN_AUTHOR, author);
		values.put(CommentStorageHelper.COLUMN_ARTICLE_URL, article_url);

		long insertId = database.insert(CommentStorageHelper.TABLE_COMMENTS,
				null, values);

		Cursor cursor = database.query(CommentStorageHelper.TABLE_COMMENTS,
				allColumns, CommentStorageHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);

		cursor.moveToFirst();
		Comment newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;
	}

	private Comment cursorToComment(Cursor cursor) {

		return new Comment(cursor.getInt(0), cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getString(4),
				cursor.getString(5));
	}

	private String cursorToURL(Cursor cursor) {
		return new String(cursor.getString(1));
	}

	private String cursorToArticleURL(Cursor cursor) {
		return new String(cursor.getString(5));
	}

	public void clearTable() {
		database.execSQL("DROP TABLE IF EXISTS "
				+ CommentStorageHelper.TABLE_COMMENTS);
		database.execSQL(CommentStorageHelper.DATABASE_CREATE);
	}
}
