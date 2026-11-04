package com.liskovsoft.smartyoutubetv2.tv.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv2.tv.R;

/** Two bounded number pickers; D-pad up/down changes values, left/right moves columns. */
public final class NatjeTimePicker {
    public interface Result { void selected(int minutes); }
    private NatjeTimePicker() { }
    public static AlertDialog show(Context c,String title,int minutes,Result result) {
        LinearLayout row=new LinearLayout(c); row.setGravity(android.view.Gravity.CENTER);
        NumberPicker hours=column(c,row,R.string.night_hours,23,minutes/60);
        NumberPicker mins=column(c,row,R.string.night_minutes,59,minutes%60);
        AlertDialog dialog=new AlertDialog.Builder(c).setTitle(title).setView(row)
                .setPositiveButton(R.string.night_save,(d,w)->result.selected(hours.getValue()*60+mins.getValue()))
                .setNegativeButton(R.string.night_cancel,null).create();
        dialog.show(); hours.requestFocus(); return dialog;
    }
    private static NumberPicker column(Context c,LinearLayout row,int label,int max,int value) {
        LinearLayout group=new LinearLayout(c); group.setOrientation(LinearLayout.VERTICAL);
        int padding=c.getResources().getDimensionPixelSize(R.dimen.natje_space); group.setPadding(padding,padding,padding,padding);
        TextView text=new TextView(c); text.setText(label); group.addView(text);
        NumberPicker picker=new NumberPicker(c); picker.setMinValue(0); picker.setMaxValue(max); picker.setValue(value);
        picker.setWrapSelectorWheel(true); picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setFormatter(v->String.format(java.util.Locale.getDefault(),"%02d",v));
        picker.setContentDescription(c.getString(label)); group.addView(picker); row.addView(group);
        return picker;
    }
}
