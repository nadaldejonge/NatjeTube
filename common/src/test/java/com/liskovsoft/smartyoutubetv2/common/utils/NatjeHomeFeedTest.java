package com.liskovsoft.smartyoutubetv2.common.utils;

import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.VideoGroup;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class NatjeHomeFeedTest {
    private Video video(String id) { Video v = new Video(); v.videoId = id; return v; }
    private VideoGroup group(int id, String title, Video... videos) {
        VideoGroup group = VideoGroup.from(Arrays.asList(videos)); group.setId(id); group.setTitle(title); return group;
    }
    @Test public void mainFeedKeepsServerRankRatherThanSortingByNameOrTopic() {
        NatjeHomeFeed feed = new NatjeHomeFeed(); Video z = video("z"), a = video("a");
        feed.update(group(1, "Recommended", z, a)); feed.update(group(2, "Music", video("music")));
        assertEquals(Arrays.asList(z, a), feed.videos());
    }
    @Test public void lazyChipsDoNotReplaceOrMixIntoTheMainRecommendations() {
        NatjeHomeFeed feed = new NatjeHomeFeed(); Video first = video("ranked-first");
        feed.update(group(1, "Home", first)); feed.update(group(2, "Gaming"));
        // The server repeats the already-selected main filter; it must not become a dead duplicate chip.
        feed.update(group(3, "Home"));
        assertEquals(Collections.singletonList(first), feed.videos()); assertEquals(1, feed.topics().size());
        feed.select(2); assertTrue(feed.current().isEmpty());
        feed.update(group(2, "Gaming", video("game"))); assertEquals("game", feed.videos().get(0).videoId);
        feed.select(null); assertSame(first, feed.videos().get(0));
    }
    @Test public void pagingDeduplicatesInFirstSeenOrderAndKeepsContinuationOwnership() {
        NatjeHomeFeed feed = new NatjeHomeFeed(); Video first = video("one"), second = video("two");
        VideoGroup original = group(1, "Home", first, second); feed.update(original);
        feed.update(group(1, "Home", first, second, video("one"), video("three")));
        assertEquals(3, feed.videos().size()); assertSame(first, feed.videos().get(0));
        assertEquals("three", feed.videos().get(2).videoId);
        VideoGroup.from(feed.videos()); assertSame(original, first.getGroup());
    }
    @Test public void emptyEarlierTopicsDoNotBecomeTheDefaultFeed() {
        NatjeHomeFeed feed = new NatjeHomeFeed(); feed.update(group(2, "Music")); feed.update(group(1, "Home", video("personal")));
        assertEquals("personal", feed.videos().get(0).videoId); assertEquals(1, feed.topics().size());
    }
    @Test public void refreshAndAccountChangesCanDiscardAllCachedFeedReferences() {
        NatjeHomeFeed feed = new NatjeHomeFeed(); feed.update(group(1, "Home", video("private")));
        feed.update(group(2, "Topic", video("topic"))); feed.select(2); feed.clear();
        assertTrue(feed.videos().isEmpty()); assertTrue(feed.topics().isEmpty()); assertNull(feed.selection()); assertNull(feed.current());
        feed.update(group(3, "New account", video("other"))); assertEquals("other", feed.videos().get(0).videoId);
    }
}
