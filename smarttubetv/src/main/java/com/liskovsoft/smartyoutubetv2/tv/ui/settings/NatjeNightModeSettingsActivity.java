package com.liskovsoft.smartyoutubetv2.tv.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import com.liskovsoft.smartyoutubetv2.common.misc.MotherActivity;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightModeManager;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightOverlayView;
import com.liskovsoft.smartyoutubetv2.common.prefs.NatjeNightModeData;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.NatjeRangeControl;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.NatjeTimePicker;
import java.util.ArrayList;
import java.util.Locale;

/** A focused extension, leaving the existing preference/dialog architecture unchanged. */
public final class NatjeNightModeSettingsActivity extends MotherActivity implements NatjeNightModeManager.Listener {
    private NatjeNightModeManager manager;
    private LinearLayout form;
    private TextView status;
    private Switch master, ui;
    private Button mode,planning,start,end,location,transition,profile;
    private NatjeRangeControl warmth,dim;
    private View preview;
    private boolean updating;
    private AlertDialog modal;
    private int gap;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); manager=NatjeNightModeManager.instance(this);
        gap=getResources().getDimensionPixelSize(R.dimen.natje_space);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        scroll.setBackgroundColor(getResources().getColor(R.color.natje_background));
        form=new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL);
        int margin=getResources().getDimensionPixelSize(R.dimen.natje_page_margin);
        form.setPadding(margin,gap,margin,gap); scroll.addView(form); setContentView(scroll);
        text("NatjeTube · "+getString(R.string.night_title),R.style.Natje_Text_Title);
        status=text("",R.style.Natje_Text_Section);
        master=toggle(R.string.night_enable,()->manager.toggle());
        mode=button(R.string.night_mode,()->choose(R.string.night_mode,new String[]{getString(R.string.night_manual),getString(R.string.night_automatic),getString(R.string.night_own_mode)},d().mode,i->{
            d().enabled=manager.isActive(); d().mode=i; d().overrideUntil=0; if(i==1&&d().planning==0)d().planning=1; changed();
        }));
        warmth=new NatjeRangeControl(this,getString(R.string.night_warmth),100,d().warmth,v->{d().warmth=v;d().profile="Custom";changed();}); add(warmth);
        dim=new NatjeRangeControl(this,getString(R.string.night_dim),80,d().dim,v->{d().dim=v;d().profile="Custom";changed();}); add(dim);
        ui=toggle(R.string.night_ui,()->{d().softenUi=ui.isChecked();changed();});
        transition=button(R.string.night_transition,()->choose(R.string.night_transition,transitionNames(),transitionIndex(),i->{d().transition=new int[]{0,5,15,30,60}[i];changed();}));
        text(getString(R.string.night_preview),R.style.Natje_Text_Metadata);
        preview=new Preview(this); form.addView(preview,new LinearLayout.LayoutParams(-1,gap*5));
        planning=button(R.string.night_planning,()->choose(R.string.night_planning,planningNames(),d().planning,i->{
            d().enabled=manager.isActive();d().planning=i;d().overrideUntil=0;d().mode=i==0?0:1;changed();
        }));
        start=button(R.string.night_start,()->openTime(true)); end=button(R.string.night_end,()->openTime(false));
        location=button(R.string.night_location,this::configureLocation);
        button(R.string.night_resume_auto,()->{if(d().planning==0)d().planning=1;manager.resumeAutomatic();});
        profile=button(R.string.night_profiles,this::profiles);
        button(R.string.night_save_profile,this::saveProfile);
        button(R.string.night_profile_remove,this::removeProfile);
        button(R.string.night_reset,()->showModal(new AlertDialog.Builder(this).setTitle(R.string.night_reset).setMessage(R.string.night_reset_confirm)
                .setPositiveButton(R.string.night_reset,(x,w)->manager.reset()).setNegativeButton(R.string.night_cancel,null).create()));
        text(getString(R.string.night_help),R.style.Natje_Text_Metadata);
        text(getString(R.string.night_schedule_help),R.style.Natje_Text_Metadata);
        text(getString(R.string.night_pip_note),R.style.Natje_Text_Metadata);
        button(R.string.night_close,this::finish);
        onNightModeChanged(); master.requestFocus();
    }
    private NatjeNightModeData d() { return manager.data(); }
    private void changed() { manager.changed(); preview.invalidate(); }
    private void add(View v) {
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.bottomMargin=gap/2; form.addView(v,p);
    }
    private TextView text(String value,int style) {
        TextView t=new TextView(this); t.setTextAppearance(this,style); t.setText(value); t.setPadding(0,gap/2,0,gap/2); add(t); return t;
    }
    private Button button(int label,Runnable action) {
        Button b=new Button(this); b.setText(label); b.setAllCaps(false); b.setTextAppearance(this,R.style.Natje_Text_Settings);
        b.setGravity(android.view.Gravity.START|android.view.Gravity.CENTER_VERTICAL); b.setBackgroundResource(R.drawable.natje_focus);
        b.setMinHeight(getResources().getDimensionPixelSize(R.dimen.natje_control_height)); b.setOnClickListener(v->action.run()); add(b);return b;
    }
    private Switch toggle(int label,Runnable action) {
        Switch s=new Switch(this); s.setText(label); s.setTextAppearance(this,R.style.Natje_Text_Settings); s.setPadding(gap,gap/2,gap,gap/2);
        s.setBackgroundResource(R.drawable.natje_focus); s.setOnCheckedChangeListener((b,checked)->{if(!updating)action.run();}); add(s);return s;
    }
    @Override protected void onResume() { super.onResume(); if(manager!=null)manager.addListener(this); }
    @Override protected void onPause() { if(manager!=null)manager.removeListener(this); super.onPause(); }
    @Override protected void onDestroy() { if(modal!=null)modal.dismiss(); super.onDestroy(); }
    @Override public void onNightModeChanged() {
        if(status==null)return;
        updating=true;
        String details=manager.status();
        if(manager.automatic()) {
            long next=manager.schedule().next;
            details+="\n"+(next==Long.MAX_VALUE?getString(R.string.night_no_boundary):getString(R.string.night_next,android.text.format.DateFormat.format("EEE HH:mm",next)));
        }
        status.setText(details); master.setChecked(manager.isActive()); ui.setChecked(d().softenUi);
        warmth.setValue(d().warmth);dim.setValue(d().dim);
        mode.setText(getString(R.string.night_mode)+" · "+getString(new int[]{R.string.night_manual,R.string.night_automatic,R.string.night_own_mode}[d().mode]));
        planning.setText(getString(R.string.night_planning)+" · "+planningNames()[d().planning]);
        transition.setText(getString(R.string.night_transition)+" · "+transitionNames()[transitionIndex()]);
        start.setText(getString(R.string.night_start)+" · "+time(d().start));end.setText(getString(R.string.night_end)+" · "+time(d().end));
        location.setText(getString(R.string.night_location)+" · "+(d().locationConfigured?String.format(Locale.getDefault(),"%.3f, %.3f",d().latitude,d().longitude):getString(R.string.night_location_missing)));
        profile.setText(getString(R.string.night_profiles)+" · "+profileLabel(d().profile));
        preview.invalidate(); updating=false;
    }
    private String time(int min) { return String.format(Locale.getDefault(),"%02d:%02d",min/60,min%60); }
    private String[] planningNames(){return new String[]{getString(R.string.night_off),getString(R.string.night_custom_time),getString(R.string.night_solar)};}
    private String[] transitionNames(){return new String[]{getString(R.string.night_immediate),"5 s","15 s","30 s","60 s"};}
    private int transitionIndex(){int[] a={0,5,15,30,60};for(int i=0;i<a.length;i++)if(d().transition==a[i])return i;return 0;}
    private interface Selection { void selected(int i); }
    private void choose(int title,String[] labels,int selected,Selection action) {
        showModal(new AlertDialog.Builder(this).setTitle(title).setSingleChoiceItems(labels,selected,(dialog,i)->{action.selected(i);dialog.dismiss();})
                .setNegativeButton(R.string.night_cancel,null).create());
    }
    private void openTime(boolean isStart) {
        modal=NatjeTimePicker.show(this,getString(isStart?R.string.night_start:R.string.night_end),isStart?d().start:d().end,v->{
            if(isStart)d().start=v;else d().end=v;d().overrideUntil=0;changed();
        }); filterModal(modal);
    }
    private void showModal(AlertDialog dialog) { if(modal!=null)modal.dismiss();modal=dialog;dialog.show();filterModal(dialog); }
    private void filterModal(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.natje_panel);
        NatjeNightOverlayView overlay=new NatjeNightOverlayView(this,dialog.getWindow()); overlay.resume();
        dialog.setOnDismissListener(x->overlay.release());
    }
    private void configureLocation() {
        LinearLayout fields=new LinearLayout(this);fields.setOrientation(LinearLayout.VERTICAL);fields.setPadding(gap,gap,gap,gap);
        EditText lat=entry(fields,R.string.night_latitude),lon=entry(fields,R.string.night_longitude);
        lat.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        lon.setInputType(lat.getInputType());
        if(d().locationConfigured){lat.setText(Double.toString(d().latitude));lon.setText(Double.toString(d().longitude));}
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(R.string.night_location).setView(fields)
                .setPositiveButton(R.string.night_save,null).setNegativeButton(R.string.night_cancel,null).create();showModal(dialog);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            try {
                double a=Double.parseDouble(lat.getText().toString().trim().replace(',','.')), b=Double.parseDouble(lon.getText().toString().trim().replace(',','.'));
                if(!NatjeNightModeData.validLocation(a,b))throw new NumberFormatException();
                d().latitude=a;d().longitude=b;d().locationConfigured=true;d().overrideUntil=0;changed();dialog.dismiss();
            }catch(NumberFormatException e){lat.setError(getString(R.string.night_invalid_location));}
        });
    }
    private EditText entry(LinearLayout group,int hint){EditText e=new EditText(this);e.setHint(hint);e.setSingleLine(true);group.addView(e);return e;}
    private String profileLabel(String name) {
        String[] keys={"Standard","Comfort","Extra warm","Cinema","Custom"};
        int[] labels={R.string.night_standard,R.string.night_comfort,R.string.night_extra,R.string.night_cinema,R.string.night_custom};
        for(int i=0;i<keys.length;i++)if(keys[i].equals(name))return getString(labels[i]);return name;
    }
    private void profiles() {
        String[] keys={"Standard","Comfort","Extra warm","Cinema","Custom"}; int[][] presets={{25,15},{45,30},{75,40},{15,45}};
        ArrayList<String> names=new ArrayList<>();for(String k:keys)names.add(k);names.addAll(d().profiles.keySet());
        String[] labels=new String[names.size()];for(int i=0;i<labels.length;i++)labels[i]=profileLabel(names.get(i));
        choose(R.string.night_profiles,labels,names.indexOf(d().profile),i->{
            String name=names.get(i);int[] values=i<4?presets[i]:d().profiles.get(name);
            if(values!=null){
                d().warmth=values[0];d().dim=values[1];
                if(values.length>2)d().transition=values[2];
                if(values.length>3)d().softenUi=values[3]!=0;
            }d().profile=name;changed();
        });
    }
    private void saveProfile() {
        LinearLayout fields=new LinearLayout(this);fields.setPadding(gap,gap,gap,gap);EditText name=entry(fields,R.string.night_profile_name);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(R.string.night_save_profile).setView(fields)
                .setPositiveButton(R.string.night_save,null).setNegativeButton(R.string.night_cancel,null).create();showModal(dialog);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            String n=name.getText().toString().trim();
            if(n.isEmpty()||n.length()>32||d().profiles.size()>=20||d().profiles.containsKey(n)||n.equals("Standard")||n.equals("Comfort")||n.equals("Extra warm")||n.equals("Cinema")||n.equals("Custom")){
                name.setError(getString(R.string.night_invalid_name));return;
            }
            d().profiles.put(n,new int[]{d().warmth,d().dim,d().transition,d().softenUi?1:0});d().profile=n;changed();dialog.dismiss();
        });
    }
    private void removeProfile(){
        if(d().profiles.isEmpty())return;
        String[] names=d().profiles.keySet().toArray(new String[0]);
        choose(R.string.night_profile_remove,names,-1,i->{d().profiles.remove(names[i]);if(d().profile.equals(names[i]))d().profile="Custom";changed();});
    }
    private final class Preview extends View {
        private final Paint paint=new Paint();
        private final int[] colours={Color.WHITE,0xFFAAAAAA,Color.BLACK,0xFF3366DD,0xFF55BB77,0xFFDD3344};
        Preview(Context c){super(c);setContentDescription(getString(R.string.night_preview));}
        @Override protected void onDraw(Canvas canvas){
            float w=getWidth()/(float)colours.length;
            for(int i=0;i<colours.length;i++){paint.setColor(colours[i]);canvas.drawRect(i*w,0,(i+1)*w,getHeight(),paint);}
            if(!manager.isActive()) { // Example stays available while the real filter is disabled.
                canvas.drawColor(Color.argb(Math.round(d().warmth/100f*0.06f*255),255,171,80));
                canvas.drawColor(Color.argb(Math.round(d().dim/100f*255),0,0,0));
            }
        }
    }
}
