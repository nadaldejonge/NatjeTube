package com.liskovsoft.smartyoutubetv2.tv.ui.browse.video;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.VideoGroup;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.BrowsePresenter;
import com.liskovsoft.smartyoutubetv2.common.utils.NatjeHomeFeed;
import com.liskovsoft.smartyoutubetv2.tv.R;

/** A ranked main grid and server-provided topic chips. No local channel boost or date sort. */
public final class NatjeHomeGridFragment extends VideoGridFragment {
    private final NatjeHomeFeed feed = new NatjeHomeFeed();
    private LinearLayout chips;
    private View selectedChip;
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        View grid = super.onCreateView(inflater, parent, state);
        chips = new LinearLayout(requireContext()); chips.setOrientation(LinearLayout.HORIZONTAL);
        View root = NatjeChips.wrap(requireContext(), grid, chips, getString(
                BrowsePresenter.instance(requireContext()).isSignedIn() ? R.string.nav_home_hint : R.string.nav_home_sign_in));
        rebuildChips(); return root;
    }
    @Override public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        getBrowseGrid().setPadding(getBrowseGrid().getPaddingLeft(), 0, getBrowseGrid().getPaddingRight(), 0);
        getBrowseGrid().setOnKeyInterceptListener(event -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                    && getPosition() % getGridPresenter().getNumberOfColumns() == 0)
                return NatjeChips.focusSidebar(getView());
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN
                    && getPosition() < getGridPresenter().getNumberOfColumns() && selectedChip != null) return selectedChip.requestFocus();
            return false;
        });
        render(false);
    }
    @Override public void update(VideoGroup group) {
        if (group == null) return;
        if (group.getAction() == VideoGroup.ACTION_REPLACE) { feed.clear(); super.clear(); rebuildChips(); return; }
        if (group.getAction() == VideoGroup.ACTION_REMOVE || group.getAction() == VideoGroup.ACTION_REMOVE_AUTHOR
                || group.getAction() == VideoGroup.ACTION_SYNC) { super.update(group); return; }
        feed.update(group); rebuildChips(); render(true);
    }
    private void rebuildChips() {
        if (chips == null) return;
        boolean hadFocus = chips.hasFocus(); chips.removeAllViews();
        selectedChip = NatjeChips.add(chips, getString(R.string.nav_all), feed.selection() == null,
                () -> choose(null), this::focusGrid);
        for (VideoGroup topic : feed.topics()) {
            boolean selected = feed.selection() != null && feed.selection() == topic.getId();
            TextView button = NatjeChips.add(chips, topic.getTitle(), selected,
                    () -> choose(topic.getId()), this::focusGrid);
            if (selected) selectedChip = button;
        }
        NatjeChips.add(chips, getString(R.string.nav_refresh), false,
                () -> BrowsePresenter.instance(requireContext()).refresh(false), this::focusGrid);
        if (hadFocus) selectedChip.requestFocus();
    }
    private void choose(Integer id) {
        feed.select(id); rebuildChips(); render(false);
        VideoGroup current = feed.current();
        if (current != null && current.isEmpty()) BrowsePresenter.instance(requireContext()).loadHomeTopic(current);
    }
    private void render(boolean preservePosition) {
        if (getBrowseGrid() == null) return;
        if (getBrowseGrid().isComputingLayout()) { getBrowseGrid().post(() -> { if (getView() != null) render(preservePosition); }); return; }
        int position = preservePosition ? getPosition() : 0;
        super.clear(); super.update(VideoGroup.from(feed.videos()));
        if (position >= 0 && !feed.videos().isEmpty()) super.setPosition(Math.min(position, feed.videos().size() - 1));
    }
    private void focusGrid() { if (getBrowseGrid() != null) getBrowseGrid().requestFocus(); }
    @Override public void clear() { feed.clear(); super.clear(); rebuildChips(); }
    @Override public boolean isEmpty() { return feed.videos().isEmpty(); }
    public void focusFilters() { if (selectedChip != null) selectedChip.requestFocus(); }
    @Override public void onDestroyView() { chips = null; selectedChip = null; super.onDestroyView(); }
}
