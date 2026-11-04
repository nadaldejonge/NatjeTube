package com.liskovsoft.smartyoutubetv2.common.utils;

import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import static org.junit.Assert.*;

public class NatjeNightScheduleTest {
    private final TimeZone amsterdam=TimeZone.getTimeZone("Europe/Amsterdam");
    private long time(String text,TimeZone zone) throws Exception {
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US);f.setTimeZone(zone);return f.parse(text).getTime();
    }
    private NatjeNightSchedule.Result custom(String text) throws Exception {
        return NatjeNightSchedule.evaluate(time(text,amsterdam),amsterdam,1,1350,420,false,0,0);
    }
    @Test public void overnightBoundaries() throws Exception {
        assertFalse(custom("2026-11-04 22:29").active);
        assertTrue(custom("2026-11-04 22:30").active);
        assertTrue(custom("2026-11-05 00:01").active);
        assertTrue(custom("2026-11-05 06:59").active);
        assertFalse(custom("2026-11-05 07:00").active);
        assertEquals(time("2026-11-05 07:00",amsterdam),custom("2026-11-04 23:00").next);
    }
    @Test public void daytimeIntervalAndEqualTimes() throws Exception {
        long now=time("2026-11-04 12:00",amsterdam);
        assertTrue(NatjeNightSchedule.evaluate(now,amsterdam,1,600,900,false,0,0).active);
        assertFalse(NatjeNightSchedule.evaluate(now,amsterdam,1,600,600,false,0,0).active);
    }
    @Test public void springAndAutumnDstUseLocalClock() throws Exception {
        assertEquals(time("2026-03-29 07:00",amsterdam),custom("2026-03-28 23:00").next);
        assertEquals(time("2026-10-25 07:00",amsterdam),custom("2026-10-24 23:00").next);
        assertTrue(custom("2026-03-29 04:00").active);
    }
    @Test public void changedTimezoneRecomputesBoundary() throws Exception {
        long now=time("2026-11-04 22:00",TimeZone.getTimeZone("UTC"));
        assertTrue(NatjeNightSchedule.evaluate(now,amsterdam,1,1350,420,false,0,0).active);
        assertFalse(NatjeNightSchedule.evaluate(now,TimeZone.getTimeZone("UTC"),1,1350,420,false,0,0).active);
    }
    @Test public void missingLocationDoesNotInventSolarTime() {
        NatjeNightSchedule.Result r=NatjeNightSchedule.evaluate(0,amsterdam,2,0,0,false,0,0);
        assertFalse(r.configured);assertFalse(r.active);assertEquals(Long.MAX_VALUE,r.next);
    }
    @Test public void solarEquinoxAndPolarConditions() throws Exception {
        long noon=time("2026-03-20 12:00",TimeZone.getTimeZone("UTC"));
        long[] sun=NatjeNightSchedule.solar(noon,0,0);
        long sunrise=time("2026-03-20 06:00",TimeZone.getTimeZone("UTC"));
        long sunset=time("2026-03-20 18:00",TimeZone.getTimeZone("UTC"));
        assertTrue(Math.abs(sun[0]-sunrise)<20*60_000);assertTrue(Math.abs(sun[1]-sunset)<20*60_000);
        long[] summer=NatjeNightSchedule.solar(time("2026-06-21 12:00",amsterdam),80,10);
        long[] winter=NatjeNightSchedule.solar(time("2026-12-21 12:00",amsterdam),80,10);
        assertEquals(-1,summer[0]);assertEquals(0,summer[2]);assertEquals(-1,winter[0]);assertEquals(1,winter[2]);
    }
    @Test public void solarScheduleChangesAtRealCalculatedEvents() throws Exception {
        long noon=time("2026-11-04 12:00",amsterdam);
        long[] sun=NatjeNightSchedule.solar(noon,52.37,4.90);
        assertFalse(NatjeNightSchedule.evaluate(sun[1]-1,amsterdam,2,0,0,true,52.37,4.9).active);
        assertTrue(NatjeNightSchedule.evaluate(sun[1]+1,amsterdam,2,0,0,true,52.37,4.9).active);
        assertFalse(NatjeNightSchedule.evaluate(sun[0]+1,amsterdam,2,0,0,true,52.37,4.9).active);
    }
}
