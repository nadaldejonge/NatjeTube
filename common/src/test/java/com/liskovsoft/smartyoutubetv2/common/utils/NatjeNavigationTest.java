package com.liskovsoft.smartyoutubetv2.common.utils;

import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class NatjeNavigationTest {
    private Video section(int id) { Video v = new Video(); v.sectionId = id; return v; }
    @Test public void compactOrderDoesNotDependOnOldThemeOrder() {
        List<Video> saved = Arrays.asList(section(MediaGroup.TYPE_MUSIC), section(MediaGroup.TYPE_HISTORY), section(MediaGroup.TYPE_HOME));
        List<Video> result = NatjeNavigation.sidebar(saved);
        assertEquals(4, result.size());
        assertEquals(MediaGroup.TYPE_HOME, result.get(0).sectionId);
        assertEquals(MediaGroup.TYPE_SUBSCRIPTIONS, result.get(1).sectionId);
        assertEquals(NatjeNavigation.LIBRARY, result.get(2).sectionId);
        assertEquals(NatjeNavigation.DISCOVER, result.get(3).sectionId);
        assertEquals(MediaGroup.TYPE_MUSIC, saved.get(0).sectionId);
        assertEquals(3, saved.size());
    }
    @Test public void pinsKeepIdentityAndOrderWithoutChangingStoredData() {
        Video first = new Video(); first.channelId = "first";
        Video second = new Video(); second.playlistId = "second";
        List<Video> saved = new ArrayList<>(Arrays.asList(first, section(3), second));
        List<Video> result = NatjeNavigation.sidebar(saved);
        assertEquals(6, result.size()); assertSame(first, result.get(4)); assertSame(second, result.get(5));
        assertEquals(3, saved.size()); assertSame(first, saved.get(0));
        assertEquals(6, NatjeNavigation.sidebar(saved).size());
    }
    @Test public void savedChildStartPagesRouteToTheRightParent() {
        assertEquals(NatjeNavigation.LIBRARY, NatjeNavigation.parent(MediaGroup.TYPE_HISTORY));
        assertEquals(NatjeNavigation.LIBRARY, NatjeNavigation.parent(NatjeNavigation.WATCH_LATER));
        assertEquals(NatjeNavigation.DISCOVER, NatjeNavigation.parent(MediaGroup.TYPE_MUSIC));
        assertEquals(MediaGroup.TYPE_SUBSCRIPTIONS, NatjeNavigation.parent(MediaGroup.TYPE_CHANNEL_UPLOADS));
        assertEquals(9876, NatjeNavigation.parent(9876));
    }
    @Test public void everyChildHasOneParentAndSettingsStayOutOfContentTabs() {
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (int parent : new int[]{MediaGroup.TYPE_SUBSCRIPTIONS, NatjeNavigation.LIBRARY, NatjeNavigation.DISCOVER}) {
            for (int child : NatjeNavigation.children(parent)) {
                assertTrue(seen.add(child)); assertEquals(parent, NatjeNavigation.parent(child));
                assertNotEquals(MediaGroup.TYPE_SETTINGS, child);
            }
        }
        assertArrayEquals(new int[]{MediaGroup.TYPE_SUBSCRIPTIONS, MediaGroup.TYPE_CHANNEL_UPLOADS}, NatjeNavigation.children(MediaGroup.TYPE_SUBSCRIPTIONS));
    }
}
