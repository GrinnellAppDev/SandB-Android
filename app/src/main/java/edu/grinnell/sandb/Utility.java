package edu.grinnell.sandb;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.graphics.Bitmap;
import android.util.Log;

public class Utility {
	
	/* Capitalize the first letter of each word in a string */
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
	
	/* Form a nicely formatted date string */
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
	
	/* Resize an image and return it's thumbnail version for displaying in the article list */
	public static Bitmap resizeBitmap(Bitmap bm, int maxWidth, int maxHeight) {
		
		if (bm == null)
			return null;
		
		int w = bm.getWidth();
        int h = bm.getHeight();
        
        float s, sw, sh;
        if (w > h && w > 0) {
        	s = ((float)maxWidth)/w; 
        	sw = maxWidth;
        	sh = h*s + 0.5f;
        } else if (h > 0){
        	s = ((float)maxWidth)/w;
        	sw = w*s + 0.5f;
        	sh = maxHeight;
        } else {
        	s = 1;
        	sw = w;
        	sh = h;
        }

        try {
       	 return Bitmap.createScaledBitmap(bm, (int) sw, (int) sh, true);
        } catch (IllegalArgumentException iae) {
       	 Log.i("generate thumb", "width: " + w + ", height: " + h + ", scale: " + s + ", sh" + sh);
       	 return null;
        }
	}
}