package com.liskovsoft.smartyoutubetv2.common.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;

/** Device-wide, versioned storage. Deliberately independent of account/profile serializers. */
public final class NatjeNightModeData {
    private final SharedPreferences prefs;
    public boolean enabled;
    public int mode, planning, warmth = 25, dim = 15, transition = 5;
    public int start = 22 * 60 + 30, end = 7 * 60;
    public boolean locationConfigured, softenUi = true;
    public double latitude, longitude;
    public long overrideUntil;
    public boolean overrideActive;
    public String profile = "Standard";
    public final Map<String, int[]> profiles = new LinkedHashMap<>();

    public NatjeNightModeData(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("natje_night_mode", Context.MODE_PRIVATE);
        try {
            JSONObject o = new JSONObject(prefs.getString("configuration", "{}"));
            if (o.optInt("version", 1) != 1) return; // Leave unknown future versions untouched until an explicit edit.
            enabled = o.optBoolean("enabled"); mode = clamp(o.optInt("mode"), 0, 2);
            planning = clamp(o.optInt("planning"), 0, 2);
            warmth = clamp(o.optInt("warmth", 25), 0, 100); dim = clamp(o.optInt("dim", 15), 0, 80);
            transition = validTransition(o.optInt("transition", 5));
            start = clamp(o.optInt("start", 1350), 0, 1439); end = clamp(o.optInt("end", 420), 0, 1439);
            latitude = o.optDouble("latitude", 0); longitude = o.optDouble("longitude", 0);
            locationConfigured = o.optBoolean("locationConfigured") && validLocation(latitude, longitude);
            softenUi = o.optBoolean("softenUi", true);
            overrideUntil = o.optLong("overrideUntil"); overrideActive = o.optBoolean("overrideActive");
            profile = o.optString("profile", "Standard");
            JSONArray a = o.optJSONArray("profiles");
            if (a != null) for (int i = 0; i < Math.min(20, a.length()); i++) {
                JSONObject p = a.optJSONObject(i);
                if (p != null && !p.optString("name").trim().isEmpty()) profiles.put(p.optString("name"),
                        new int[] {clamp(p.optInt("warmth",25),0,100), clamp(p.optInt("dim",15),0,80),
                                validTransition(p.optInt("transition",5)),p.optBoolean("softenUi",true)?1:0});
            }
        } catch (JSONException ignored) { /* Valid defaults, never touch other app data. */ }
    }

    public void save() {
        try {
            JSONObject o = new JSONObject();
            o.put("version",1).put("enabled",enabled).put("mode",mode).put("planning",planning)
                    .put("warmth",clamp(warmth,0,100)).put("dim",clamp(dim,0,80)).put("transition",transition)
                    .put("start",start).put("end",end).put("locationConfigured",locationConfigured)
                    .put("latitude",latitude).put("longitude",longitude).put("softenUi",softenUi)
                    .put("overrideUntil",overrideUntil).put("overrideActive",overrideActive).put("profile",profile);
            JSONArray a = new JSONArray();
            for (Map.Entry<String,int[]> p: profiles.entrySet()) a.put(new JSONObject().put("name",p.getKey())
                    .put("warmth",p.getValue()[0]).put("dim",p.getValue()[1])
                    .put("transition",p.getValue().length>2?p.getValue()[2]:5)
                    .put("softenUi",p.getValue().length<4||p.getValue()[3]!=0));
            o.put("profiles",a);
            prefs.edit().putString("configuration",o.toString()).apply();
        } catch (JSONException e) { throw new IllegalStateException("Invalid night mode configuration", e); }
    }

    public static boolean validLocation(double lat, double lon) {
        return !Double.isNaN(lat) && !Double.isNaN(lon) && lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }
    public static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max,value)); }
    private static int validTransition(int s) { return s==0||s==5||s==15||s==30||s==60?s:5; }
}
