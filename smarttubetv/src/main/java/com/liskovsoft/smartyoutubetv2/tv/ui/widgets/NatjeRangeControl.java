package com.liskovsoft.smartyoutubetv2.tv.ui.widgets;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv2.tv.R;

/** Reusable remote-friendly range: one focus target, native left/right and a numeric value. */
public final class NatjeRangeControl extends LinearLayout {
    public interface Change { void changed(int value); }
    private final TextView label;
    private final SeekBar bar;
    private final String title;
    public NatjeRangeControl(Context c,String title,int max,int value,Change change) {
        super(c); this.title=title; setOrientation(VERTICAL);
        int pad=getResources().getDimensionPixelSize(R.dimen.natje_space);
        setPadding(pad,pad,pad,pad); setBackgroundResource(R.drawable.natje_panel);
        label=new TextView(c); label.setTextAppearance(c,R.style.Natje_Text_Body); addView(label);
        bar=new SeekBar(c); bar.setMax(max); bar.setKeyProgressIncrement(5); bar.setFocusable(true);
        bar.setBackgroundResource(R.drawable.natje_focus); bar.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.natje_control_height));
        addView(bar,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        setValue(value);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar b,int v,boolean user) {
                updateLabel(v); if (user) change.changed(v);
            }
            @Override public void onStartTrackingTouch(SeekBar b) { }
            @Override public void onStopTrackingTouch(SeekBar b) { }
        });
    }
    private void updateLabel(int v) { label.setText(title+" · "+v+"%"); bar.setContentDescription(label.getText()); }
    public void setValue(int value) { bar.setProgress(value); updateLabel(value); }
}
