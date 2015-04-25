package edu.grinnell.sandb.data;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ArticleTable {

	private static final String TAG = "ArticleTable";

	// Database fields
	private SQLiteDatabase database;
	private ArticleStorageHelper dbHelper;
	private String[] allColumns = { 
			ArticleStorageHelper.COLUMN_ID,
			ArticleStorageHelper.COLUMN_GUID,
			ArticleStorageHelper.COLUMN_TITLE,
			ArticleStorageHelper.COLUMN_LINK,
			ArticleStorageHelper.COLUMN_PUBDATE,
			ArticleStorageHelper.COLUMN_CATEGORY,
			ArticleStorageHelper.COLUMN_DESCRIPTION,
			ArticleStorageHelper.COLUMN_BODY,
			ArticleStorageHelper.COLUMN_COMMENTS,
			ArticleStorageHelper.COLUMN_AUTHOR,
			};

	public ArticleTable(Context context) {
		Assert.assertNotNull(context);
		dbHelper = new ArticleStorageHelper(context);
	}

	public void open() throws SQLException {
		Assert.assertNotNull(dbHelper);
		try {
			database = dbHelper.getWritableDatabase();
			Log.i(TAG, "Article DB opened: '" + database.toString() + "'");
		} catch (Exception e) {
			Log.e(TAG, "method: open", e);
		}
	}

	public void close() {
		dbHelper.close();
	}

	public void deleteArticle(Article article) {
		long id = article.getId();
		Log.i(TAG, "Article deleted with id: " + id);
		database.delete(ArticleStorageHelper.TABLE_ARTICLES,
				ArticleStorageHelper.COLUMN_ID + " = " + id, null);
	}

	public Article findById(int id) {
		String where = ArticleStorageHelper.COLUMN_ID + " = " + id;
		Log.i(TAG, "Looking for article where " + where);
		Cursor cursor = database.query(ArticleStorageHelper.TABLE_ARTICLES,
				allColumns, where, null, null, null, null);

		Article a = null;
		if (cursor != null) {
			cursor.moveToFirst();
			if (!cursor.isAfterLast())
				a = cursorToArticle(cursor);
		}

		// Make sure to close the cursor
		cursor.close();
		return a;
	}

	public List<Article> findByCategory(String category) {
		List<Article> articles = new ArrayList<Article>();

		String where = (category == null) ? null
				: ArticleStorageHelper.COLUMN_CATEGORY + " = \"" + category
						+ "\"";

		Cursor cursor = database.query(ArticleStorageHelper.TABLE_ARTICLES,
				allColumns, where, null, null, null, null);

		if (cursor == null)
			return articles;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Article article = cursorToArticle(cursor);
			articles.add(article);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return articles;
	}

	public List<Article> getAllArticles() {
		return findByCategory(null);
	}

	public Article createArticle(String guid, String articleTitle,
			String articleLink, String publicationDate, String category,
			String description, String articleBody, String commentsLink,
			String author) {

//		Date pubDate = null;
//		try {
//			if (publicationDate != null) {
//				pubDate = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ZZZZ",
//						Locale.ENGLISH).parse(publicationDate);
//			} else {
//				pubDate = new Date();
//			}
//		} catch (ParseException pe) {
//			Log.d(TAG, "new Article", pe);
//		}

		ContentValues values = new ContentValues();
		values.put(ArticleStorageHelper.COLUMN_GUID, guid);
		values.put(ArticleStorageHelper.COLUMN_TITLE, articleTitle);
		values.put(ArticleStorageHelper.COLUMN_LINK, articleLink);
		values.put(ArticleStorageHelper.COLUMN_PUBDATE, publicationDate);
		values.put(ArticleStorageHelper.COLUMN_CATEGORY, category);
		values.put(ArticleStorageHelper.COLUMN_DESCRIPTION, description);
		values.put(ArticleStorageHelper.COLUMN_BODY, articleBody);
		values.put(ArticleStorageHelper.COLUMN_COMMENTS, commentsLink);
		values.put(ArticleStorageHelper.COLUMN_AUTHOR, author);

		long insertId = database.insert(ArticleStorageHelper.TABLE_ARTICLES,
				null, values);

		Cursor cursor = database.query(ArticleStorageHelper.TABLE_ARTICLES,
				allColumns, ArticleStorageHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		Article newArticle = cursorToArticle(cursor);
		cursor.close();
		return newArticle;
	}

	private Article cursorToArticle(Cursor cursor) {

		Article a = null;
		try {
			a = new Article(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7),
					cursor.getString(8), cursor.getString(9));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return a;
	}

	public void clearTable() {
		database.execSQL("DROP TABLE IF EXISTS "
				+ ArticleStorageHelper.TABLE_ARTICLES);
		database.execSQL(ArticleStorageHelper.DATABASE_CREATE);
	}
}
