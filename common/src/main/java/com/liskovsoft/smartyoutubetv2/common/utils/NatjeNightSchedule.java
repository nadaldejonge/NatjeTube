package com.liskovsoft.smartyoutubetv2.common.utils;

import java.util.Calendar;
import java.util.TimeZone;

/** Pure Java wall-clock planner: no services, alarms, location permissions or frame processing. */
public final class NatjeNightSchedule {
    public static final class Result {
        public final boolean active, configured;
        public final long next;
        public Result(boolean active, boolean configured, long next) {
            this.active = active; this.configured = configured; this.next = next;
        }
    }
    private NatjeNightSchedule() { }

    public static Result evaluate(long now, TimeZone zone, int planning, int start, int end,
                                  boolean location, double latitude, double longitude) {
        if (planning == 0) return new Result(false, false, Long.MAX_VALUE);
        if (planning == 2 && (!location || !com.liskovsoft.smartyoutubetv2.common.prefs.NatjeNightModeData.validLocation(latitude,longitude)))
            return new Result(false, false, Long.MAX_VALUE);
        if (planning == 1 && start == end) return new Result(false, true, Long.MAX_VALUE);
        long previous = Long.MIN_VALUE, next = Long.MAX_VALUE;
        boolean active = false;
        Calendar day = Calendar.getInstance(zone); day.setTimeInMillis(now);
        // Local noon anchors solar calculations; Calendar handles DST changes in custom schedules.
        day.set(Calendar.HOUR_OF_DAY,12); day.set(Calendar.MINUTE,0); day.set(Calendar.SECOND,0); day.set(Calendar.MILLISECOND,0);
        long noon = day.getTimeInMillis();
        if (planning == 2) active = solar(noon, latitude, longitude)[2] == 1;
        for (int offset = -2; offset <= 3; offset++) {
            Calendar date = (Calendar) day.clone(); date.add(Calendar.DATE,offset);
            long on, off;
            if (planning == 1) {
                on = localTime(date,start); off = localTime(date,end);
            } else {
                long[] events = solar(date.getTimeInMillis(),latitude,longitude);
                off = events[0]; on = events[1];
            }
            long[] times = {off,on};
            for (int i=0;i<times.length;i++) {
                long t=times[i];
                if (t < 0) continue; // Polar day/night: no fabricated sunrise/sunset.
                if (t <= now && t > previous) { previous=t; active=i==1; }
                if (t > now && t < next) next=t;
            }
        }
        return new Result(active,true,next);
    }
    private static long localTime(Calendar date,int minutes) {
        Calendar c=(Calendar)date.clone(); c.set(Calendar.HOUR_OF_DAY,minutes/60); c.set(Calendar.MINUTE,minutes%60);
        return c.getTimeInMillis();
    }

    /* Solar transit equations adapted from AOSP/AndroidX TwilightCalculator (Copyright 2015 AOSP,
       Apache 2.0). Standard sunrise altitude -0.833 degrees replaces civil twilight -6 degrees.
       https://github.com/androidx/androidx/blob/androidx-main/appcompat/appcompat/src/main/java/androidx/appcompat/app/TwilightCalculator.java
       See bundled NatjeNightMode-Apache-2.0.txt. Approximate astronomical events, not measured light. */
    public static long[] solar(long noon,double latitude,double longitude) {
        double d=(noon-946728000000L)/86400000.0;
        double m=6.240059968+d*0.01720197;
        double l=m+0.0334196*Math.sin(m)+0.000349066*Math.sin(2*m)+0.000005236*Math.sin(3*m)+1.796593063+Math.PI;
        double longitudeDay=-longitude/360;
        double transit=Math.round(d-0.0009-longitudeDay)+0.0009+longitudeDay+0.0053*Math.sin(m)-0.0069*Math.sin(2*l);
        double dec=Math.asin(Math.sin(l)*Math.sin(0.40927971)), lat=Math.toRadians(latitude);
        double cosine=(Math.sin(Math.toRadians(-0.833))-Math.sin(lat)*Math.sin(dec))/(Math.cos(lat)*Math.cos(dec));
        if (cosine>=1) return new long[]{-1,-1,1};
        if (cosine<=-1) return new long[]{-1,-1,0};
        double h=Math.acos(cosine)/(2*Math.PI);
        return new long[]{Math.round((transit-h)*86400000)+946728000000L,
                Math.round((transit+h)*86400000)+946728000000L,0};
    }
}
