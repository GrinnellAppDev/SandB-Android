package edu.grinnell.sandb.img;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class URLDrawable extends BitmapDrawable {
    // the drawable that you need to set, you could set the initial drawing
    // with the loading image if you need to
    protected Drawable drawable;

    public URLDrawable(Resources r) {
    	super(r);
    }
    
    public URLDrawable(Resources r, Bitmap b) {
    	super(r, b);
    }
    
    @Override
    public void draw(Canvas canvas) {
    	super.draw(canvas);
        // override the draw to facilitate refresh function later
        if(drawable != null) {
            drawable.draw(canvas);
        }
    }
}