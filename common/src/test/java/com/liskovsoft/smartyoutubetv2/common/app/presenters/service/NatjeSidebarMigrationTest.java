package com.liskovsoft.smartyoutubetv2.common.app.presenters.service;

import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightModeManager;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class NatjeSidebarMigrationTest {
    @Test public void oldPinnedOrderSurvivesAndMigrationIsIdempotent() {
        List<Video> items=new ArrayList<>();Video home=new Video();home.sectionId=0;
        Video pinned=new Video();pinned.channelId="my-channel";Video history=new Video();history.sectionId=3;
        items.add(history);items.add(pinned);items.add(home);
        assertTrue(SidebarService.ensureNightAction(items));assertEquals(4,items.size());
        assertSame(history,items.get(0));assertSame(pinned,items.get(1));assertSame(home,items.get(2));
        assertEquals(NatjeNightModeManager.SECTION_ID,items.get(3).sectionId);
        assertFalse(SidebarService.ensureNightAction(items));assertEquals(4,items.size());
    }
    @Test public void everyProfileGetsItsOwnActionWithoutMutatingOtherProfiles() {
        List<Video> first=new ArrayList<>(), second=new ArrayList<>();
        SidebarService.ensureNightAction(first);assertTrue(second.isEmpty());
        SidebarService.ensureNightAction(second);assertEquals(1,first.size());assertEquals(1,second.size());
        assertNotSame(first.get(0),second.get(0));
    }
}
