package com.liskovsoft.smartyoutubetv2.common.misc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.liskovsoft.smartyoutubetv2.common.R;
import com.liskovsoft.smartyoutubetv2.common.prefs.NatjeNightModeData;
import com.liskovsoft.smartyoutubetv2.common.utils.NatjeNightSchedule;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/** Single device-wide state owner. Observers must attach/detach with the visible window. */
public final class NatjeNightModeManager {
    public static final int SECTION_ID = -2000; // App action, outside MediaGroup and pinned media ID ranges.
    private static NatjeNightModeManager instance;
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Set<Listener> listeners = new HashSet<>();
    private NatjeNightModeData data;
    private boolean active;
    private final Runnable tick = () -> { refresh(); scheduleTick(); };
    private final BroadcastReceiver clock = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            // Wall-clock/time-zone changes reschedule the remaining manual override as well.
            if (data.overrideUntil!=0 && automatic()) { data.overrideUntil=schedule().next; data.save(); }
            refresh(); scheduleTick();
        }
    };
    public interface Listener { void onNightModeChanged(); }
    private NatjeNightModeManager(Context context) { this.context=context.getApplicationContext(); data=new NatjeNightModeData(context); refresh(); }
    public static NatjeNightModeManager instance(Context c) {
        if (instance==null) instance=new NatjeNightModeManager(c);
        return instance;
    }
    public NatjeNightModeData data() { return data; }
    public boolean isActive() { return active; }
    public boolean automatic() { return data.mode==1 && data.planning!=0; }
    public NatjeNightSchedule.Result schedule() {
        return NatjeNightSchedule.evaluate(System.currentTimeMillis(),TimeZone.getDefault(),data.planning,
                data.start,data.end,data.locationConfigured,data.latitude,data.longitude);
    }
    public void addListener(Listener listener) {
        if (!listeners.add(listener)) return;
        if (listeners.size()==1) {
            IntentFilter filter=new IntentFilter(); filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED); filter.addAction(Intent.ACTION_DATE_CHANGED);
            if (Build.VERSION.SDK_INT>=33) context.registerReceiver(clock,filter,Context.RECEIVER_NOT_EXPORTED);
            else context.registerReceiver(clock,filter);
        }
        refresh(); scheduleTick();
    }
    public void removeListener(Listener listener) {
        if (listeners.remove(listener) && listeners.isEmpty()) {
            context.unregisterReceiver(clock); handler.removeCallbacks(tick);
        }
    }
    private void scheduleTick() {
        handler.removeCallbacks(tick);
        if (!listeners.isEmpty()) {
            long now=System.currentTimeMillis(), boundary=automatic()?schedule().next:Long.MAX_VALUE;
            long delay=Math.min(60_000-now%60_000,boundary>now?boundary-now:1000);
            if (data.overrideUntil>now) delay=Math.min(delay,data.overrideUntil-now);
            handler.postDelayed(tick,Math.max(100,delay));
        }
    }
    public void changed() { data.save(); refresh(); scheduleTick(); }
    public void reset() { data=new NatjeNightModeDataDefaults(context).value; changed(); }
    public void resumeAutomatic() { data.overrideUntil=0; data.mode=1; changed(); }
    public void toggle() {
        if (automatic()) { data.overrideActive=!active; data.overrideUntil=schedule().next; }
        else data.enabled=!active;
        changed();
    }
    private void refresh() {
        long now=System.currentTimeMillis();
        NatjeNightSchedule.Result planned=automatic()?schedule():null;
        // A polar period can initially have no nearby event; capture the real next event when it appears.
        if (planned!=null && data.overrideUntil>now && planned.next<data.overrideUntil) {
            data.overrideUntil=planned.next; data.save();
        }
        if (data.overrideUntil!=0 && now>=data.overrideUntil) { data.overrideUntil=0; data.save(); }
        active=planned!=null ? (data.overrideUntil>now?data.overrideActive:planned.active) : data.enabled;
        for (Listener l:new HashSet<>(listeners)) l.onNightModeChanged();
    }
    public String status() {
        if (automatic()) {
            if (data.overrideUntil>System.currentTimeMillis()) return context.getString(active?R.string.night_override_on:R.string.night_override_off);
            if (!schedule().configured) return context.getString(R.string.night_not_configured);
            return context.getString(active?R.string.night_auto_on:R.string.night_auto_off);
        }
        return context.getString(active?R.string.night_on:R.string.night_off);
    }
    // Reset only our dedicated preference file; never AppPrefs/account storage.
    private static final class NatjeNightModeDataDefaults {
        final NatjeNightModeData value;
        NatjeNightModeDataDefaults(Context c) {
            c.getSharedPreferences("natje_night_mode",Context.MODE_PRIVATE).edit().clear().apply();
            value=new NatjeNightModeData(c);
        }
    }
}
