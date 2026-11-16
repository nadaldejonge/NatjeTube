package com.liskovsoft.smartyoutubetv2.common.utils;

import android.content.Context;
import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.liskovsoft.smartyoutubetv2.common.R;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.Video;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.SettingsItem;
import com.liskovsoft.smartyoutubetv2.common.app.models.playback.ui.UiOptionItem;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.AppDialogPresenter;
import com.liskovsoft.smartyoutubetv2.common.misc.AppDataSourceManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Presentation hierarchy only. Original pins and account-specific preferences stay in storage. */
public final class NatjeNavigation {
    public static final int LIBRARY = -2100, DISCOVER = -2101, WATCH_LATER = -2102, LIKED = -2103;
    private NatjeNavigation() {}
    public static int[] children(int root) {
        if (root == MediaGroup.TYPE_SUBSCRIPTIONS) return new int[]{MediaGroup.TYPE_SUBSCRIPTIONS, MediaGroup.TYPE_CHANNEL_UPLOADS};
        if (root == LIBRARY) return new int[]{MediaGroup.TYPE_HISTORY, WATCH_LATER, LIKED, MediaGroup.TYPE_USER_PLAYLISTS,
                MediaGroup.TYPE_MY_VIDEOS, MediaGroup.TYPE_PLAYBACK_QUEUE, MediaGroup.TYPE_NOTIFICATIONS, MediaGroup.TYPE_BLOCKED_CHANNELS};
        if (root == DISCOVER) return new int[]{MediaGroup.TYPE_MUSIC, MediaGroup.TYPE_GAMING, MediaGroup.TYPE_LIVE,
                MediaGroup.TYPE_SPORTS, MediaGroup.TYPE_NEWS, MediaGroup.TYPE_SHORTS, MediaGroup.TYPE_KIDS_HOME, MediaGroup.TYPE_TRENDING};
        return new int[0];
    }
    public static int parent(int id) {
        for (int root : new int[]{MediaGroup.TYPE_SUBSCRIPTIONS, LIBRARY, DISCOVER})
            for (int child : children(root)) if (child == id) return root;
        return id;
    }
    public static boolean isPrimary(int id) {
        return id == MediaGroup.TYPE_HOME || id == MediaGroup.TYPE_SUBSCRIPTIONS || id == LIBRARY || id == DISCOVER;
    }
    public static List<Video> sidebar(Collection<Video> saved) {
        List<Video> result = new ArrayList<>();
        for (int id : new int[]{MediaGroup.TYPE_HOME, MediaGroup.TYPE_SUBSCRIPTIONS, LIBRARY, DISCOVER}) {
            Video item = new Video(); item.sectionId = id; result.add(item);
        }
        if (saved != null) for (Video item : saved)
            if (item != null && item.sectionId == -1) result.add(item);
        return result;
    }
    public static int label(int id) {
        switch (id) {
            case LIBRARY: return R.string.nav_library;
            case DISCOVER: return R.string.nav_discover;
            case WATCH_LATER: return R.string.nav_watch_later;
            case LIKED: return R.string.nav_liked;
            case MediaGroup.TYPE_SUBSCRIPTIONS: return R.string.nav_new_uploads;
            case MediaGroup.TYPE_CHANNEL_UPLOADS: return R.string.nav_my_channels;
            case MediaGroup.TYPE_HISTORY: return R.string.header_history;
            case MediaGroup.TYPE_USER_PLAYLISTS: return R.string.header_playlists;
            case MediaGroup.TYPE_MY_VIDEOS: return R.string.my_videos;
            case MediaGroup.TYPE_PLAYBACK_QUEUE: return R.string.playback_queue_category_title;
            case MediaGroup.TYPE_NOTIFICATIONS: return R.string.header_notifications;
            case MediaGroup.TYPE_BLOCKED_CHANNELS: return R.string.header_blocked_channels;
            case MediaGroup.TYPE_MUSIC: return R.string.header_music;
            case MediaGroup.TYPE_GAMING: return R.string.header_gaming;
            case MediaGroup.TYPE_LIVE: return R.string.badge_live;
            case MediaGroup.TYPE_SPORTS: return R.string.header_sports;
            case MediaGroup.TYPE_NEWS: return R.string.header_news;
            case MediaGroup.TYPE_SHORTS: return R.string.header_shorts;
            case MediaGroup.TYPE_KIDS_HOME: return R.string.header_kids_home;
            default: return R.string.header_trending;
        }
    }
    public static void showSettings(Context context) {
        AppDialogPresenter dialog = AppDialogPresenter.instance(context);
        for (SettingsItem item : AppDataSourceManager.instance().getSettingItems(context))
            dialog.appendSingleButton(UiOptionItem.from(item.title, option -> item.onClick.run()));
        dialog.showDialog(context.getString(R.string.header_settings));
    }
}
