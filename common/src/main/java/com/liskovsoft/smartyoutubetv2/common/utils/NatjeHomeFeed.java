package com.liskovsoft.smartyoutubetv2.common.utils;

import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.VideoGroup;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keeps the service's ranked main feed separate from topic feeds, including lazy topic chips. */
public final class NatjeHomeFeed {
    private final Map<Integer, VideoGroup> groups = new LinkedHashMap<>();
    private Integer primary, selected;
    public void clear() { groups.clear(); primary = null; selected = null; }
    public void update(VideoGroup group) {
        if (group == null) return;
        groups.put(group.getId(), group);
        if (primary == null && !group.isEmpty()) primary = group.getId();
    }
    public List<VideoGroup> topics() {
        List<VideoGroup> result = new ArrayList<>();
        for (VideoGroup group : groups.values())
            if ((primary == null || group.getId() != primary) && group.getTitle() != null && !group.getTitle().trim().isEmpty()
                    && (primary == null || !group.getTitle().equals(groups.get(primary).getTitle()))) result.add(group);
        return result;
    }
    public void select(Integer id) { selected = id != null && groups.containsKey(id) ? id : null; }
    public Integer selection() { return selected; }
    public VideoGroup current() { return groups.get(selected != null ? selected : primary); }
    public List<Video> videos() {
        VideoGroup group = current();
        Map<String, Video> unique = new LinkedHashMap<>();
        if (group != null && group.getVideos() != null) for (Video item : group.getVideos()) {
            String key = item.videoId != null ? "v:" + item.videoId : item.playlistId != null ? "p:" + item.playlistId : "o:" + System.identityHashCode(item);
            if (!unique.containsKey(key)) unique.put(key, item);
        }
        return new ArrayList<>(unique.values());
    }
}
