package edu.grinnell.sandb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Preferences.MainPrefs;

/**
 * Created by prabir on 3/7/16, AppDev Grinnell.
 */
public class DialogSettings {

    Context context;

    public DialogSettings(Context context) {
        this.context = context;
    }

    public void show() {
        // show the settings dialog for text size
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.action_settings));
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null);
        builder.setView(view);

        setFontRadioButtonFontSize(view);
        final MainPrefs prefs = new MainPrefs(context);
        final int fontSize = prefs.getArticleFontSize();
        final RadioGroup fontRadioGroup = (RadioGroup) view.findViewById(R.id.fontRadioGroup);
        fontRadioGroup.check(radioSizeToId(fontSize));

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.setArticleFontSize(radioIdToSize(fontRadioGroup.getCheckedRadioButtonId()));
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


    /**
     * conversion between id of radio button to font size
     */
    private static List<Pair<Integer, Integer>> fontSizeIdPairs = new ArrayList<>(4);

    static {
        fontSizeIdPairs.add(new Pair<>(0, R.id.fontRadio0));
        fontSizeIdPairs.add(new Pair<>(1, R.id.fontRadio1));
        fontSizeIdPairs.add(new Pair<>(2, R.id.fontRadio2));
        fontSizeIdPairs.add(new Pair<>(3, R.id.fontRadio3));
    }

    private int radioIdToSize(int id) {
        for (Pair<Integer, Integer> pair : fontSizeIdPairs)
            if (pair.second == id) return pair.first;
        return 2;
    }

    private int radioSizeToId(int size) {
        for (Pair<Integer, Integer> pair : fontSizeIdPairs)
            if (pair.first == size) return pair.second;
        return 0;
    }

    private void setFontRadioButtonFontSize(View view) {
        for (Pair<Integer, Integer> pair : fontSizeIdPairs)
            ((RadioButton) view.findViewById(pair.second))
                    .setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            Constants.FONT_SIZE_TO_SP[pair.first]);
    }
}
