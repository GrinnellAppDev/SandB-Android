package edu.grinnell.sandb;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.graphics.Bitmap;
import android.util.Log;

public class Utility {
	
	public static String captializeWords(String s) {
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < words.length; i++) {
                sb.append(words[i].substring(0, 1).toUpperCase())
                  .append(words[i].substring(1).toLowerCase());

                if (i != words.length - 1)
                        sb.append(" ");
        }
        return sb.toString();
	}
	
	/*
	public static void showToast(Context c, int message) {
		Toast t;
		switch(message) {
		case Result.NO_ROUTE:
			t = Toast.makeText(c, R.string.noRoute, Toast.LENGTH_SHORT);
			t.setGravity(Gravity.TOP, 0, 70);
			t.show();
			return;
		case Result.HTTP_ERROR:
			t = Toast.makeText(c, R.string.httpError, Toast.LENGTH_SHORT);
			t.setGravity(Gravity.TOP, 0, 70);
			t.show();
			return;
		case Result.NO_MEAL_DATA:
			t = Toast.makeText(c, R.string.noMealContent, Toast.LENGTH_LONG);
			t.setGravity(Gravity.TOP, 0, 70);
			t.show();
			return;
		default:
			return;		
		}
	}
	*/
	
	public static String dateString(GregorianCalendar c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
		sb.append(" ");
		sb.append(c.get(Calendar.DAY_OF_MONTH));
		sb.append(", ");
		sb.append(c.get(Calendar.YEAR));
		sb.append(" | ");
		sb.append(c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
		return sb.toString();
	}
	
	public static Bitmap resizeBitmap(Bitmap bm, int maxWidth) {
		int w = bm.getWidth();
        int h = bm.getHeight();
        float s = ((float)maxWidth)/w; 
        float sh = h*s + 0.5f;

        try {
       	 return Bitmap.createScaledBitmap(bm, maxWidth, (int) sh, true);
        } catch (IllegalArgumentException iae) {
       	 Log.d("generate thumb", "width: " + w + ", height: " + h + ", scale: " + s + ", sh" + sh);
       	 return null;
        }
	}
	
	public static Bitmap generateThumb(Bitmap bm) {
        final int TW = 300;
		return resizeBitmap(bm, TW);
	}
}