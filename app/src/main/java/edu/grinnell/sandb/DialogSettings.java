package edu.grinnell.sandb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import edu.grinnell.sandb.Preferences.MainPrefs;

/**
 * Created by prabir on 3/7/16, AppDev Grinnell.
 */
public class DialogSettings {

    Context context;
    public DialogSettings (Context context) {
        this.context = context;
    }

    public void show(){
        // show the settings dialog for text size
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.action_settings));
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null);
        builder.setView(view);

        final MainPrefs prefs = new MainPrefs(context);
        final int fontSize = prefs.getArticleFontSize();
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setMax(3);
        seekBar.setProgress(fontSize);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.setArticleFontSize(seekBar.getProgress());
                onSettingsSaved();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void onSettingsSaved(){}
}
