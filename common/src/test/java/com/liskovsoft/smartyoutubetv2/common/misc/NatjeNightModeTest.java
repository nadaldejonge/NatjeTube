package com.liskovsoft.smartyoutubetv2.common.misc;

import android.content.Context;
import com.liskovsoft.smartyoutubetv2.common.prefs.NatjeNightModeData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.lang.reflect.Field;
import java.util.Calendar;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28, manifest=Config.NONE)
public class NatjeNightModeTest {
    private Context context;
    @Before public void setup() throws Exception {
        context=RuntimeEnvironment.getApplication();
        context.getSharedPreferences("natje_night_mode",0).edit().clear().commit();
        Field f=NatjeNightModeManager.class.getDeclaredField("instance");f.setAccessible(true);f.set(null,null);
    }
    @Test public void defaultsAreNeutralAndDeviceWide() {
        NatjeNightModeManager m=NatjeNightModeManager.instance(context);
        assertFalse(m.isActive());assertEquals(25,m.data().warmth);assertEquals(15,m.data().dim);
        m.toggle();assertTrue(m.isActive());
        assertTrue(new NatjeNightModeData(context).enabled);
    }
    @Test public void persistenceProfilesAndResetKeepOtherPreferences() {
        context.getSharedPreferences("app_prefs",0).edit().putString("account","keep").commit();
        NatjeNightModeManager m=NatjeNightModeManager.instance(context);
        m.data().warmth=75;m.data().dim=40;m.data().profiles.put("My evening",new int[]{60,35});m.changed();
        NatjeNightModeData restored=new NatjeNightModeData(context);
        assertEquals(75,restored.warmth);assertArrayEquals(new int[]{60,35,5,1},restored.profiles.get("My evening"));
        m.reset();assertFalse(m.isActive());assertTrue(m.data().profiles.isEmpty());
        assertEquals("keep",context.getSharedPreferences("app_prefs",0).getString("account",null));
    }
    @Test public void overrideEndsAtNextScheduleBoundaryAndCanBeCleared() {
        NatjeNightModeManager m=NatjeNightModeManager.instance(context);
        m.data().mode=1;m.data().planning=1;m.changed();
        boolean scheduled=m.isActive();m.toggle();
        assertEquals(!scheduled,m.isActive());assertEquals(m.schedule().next,m.data().overrideUntil);
        m.resumeAutomatic();assertEquals(scheduled,m.isActive());assertEquals(0,m.data().overrideUntil);
    }
    @Test public void expiredOverrideRestoresScheduleAfterProcessRestart() throws Exception {
        NatjeNightModeData d=new NatjeNightModeData(context);d.mode=1;d.planning=1;
        d.overrideUntil=System.currentTimeMillis()-1000;d.overrideActive=true;d.save();
        NatjeNightModeManager m=NatjeNightModeManager.instance(context);
        assertEquals(m.schedule().active,m.isActive());assertEquals(0,m.data().overrideUntil);
    }
    @Test public void malformedSavedValuesAreClamped() {
        context.getSharedPreferences("natje_night_mode",0).edit().putString("configuration","{\"version\":1,\"warmth\":900,\"dim\":-2,\"mode\":99,\"transition\":999,\"locationConfigured\":true,\"latitude\":200}").commit();
        NatjeNightModeData d=new NatjeNightModeData(context);
        assertEquals(100,d.warmth);assertEquals(0,d.dim);assertEquals(2,d.mode);assertEquals(5,d.transition);assertFalse(d.locationConfigured);
    }
    @Test public void observerDetachesAndDoesNotRunBackgroundTimer() {
        NatjeNightModeManager m=NatjeNightModeManager.instance(context);int[] calls={0};
        NatjeNightModeManager.Listener listener=()->calls[0]++;
        m.addListener(listener);assertEquals(1,calls[0]);m.removeListener(listener);m.toggle();assertEquals(1,calls[0]);
    }
    @Test public void existingDimmerAndScreenOffAreNotMultiplied() {
        assertEquals(0,NatjeNightOverlayView.extraDimming(0.5f,0.3f),0.0001f);
        float extra=NatjeNightOverlayView.extraDimming(0.1f,0.8f);
        assertEquals(0.8f,1-(1-0.1f)*(1-extra),0.0001f);
        assertEquals(0,NatjeNightOverlayView.extraDimming(1,0.8f),0.0001f);
    }
}
