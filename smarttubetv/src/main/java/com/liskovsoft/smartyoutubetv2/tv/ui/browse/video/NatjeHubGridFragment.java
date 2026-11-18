package com.liskovsoft.smartyoutubetv2.tv.ui.browse.video;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.BrowsePresenter;
import com.liskovsoft.smartyoutubetv2.common.utils.NatjeNavigation;

/** Reuses the existing video cards, playback, paging and channel/playlist actions. */
public final class NatjeHubGridFragment extends VideoGridFragment {
    private LinearLayout chips;
    private View selectedChip;
    public static NatjeHubGridFragment create(int root) {
        NatjeHubGridFragment fragment = new NatjeHubGridFragment(); Bundle args = new Bundle();
        args.putInt("root", root); fragment.setArguments(args); return fragment;
    }
    public int root() { return requireArguments().getInt("root"); }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        View grid = super.onCreateView(inflater, parent, state);
        chips = new LinearLayout(requireContext()); chips.setOrientation(LinearLayout.HORIZONTAL);
        View root = NatjeChips.wrap(requireContext(), grid, chips, getString(root() == com.liskovsoft.mediaserviceinterfaces.data.MediaGroup.TYPE_SUBSCRIPTIONS
                ? com.liskovsoft.smartyoutubetv2.tv.R.string.header_subscriptions : NatjeNavigation.label(root())));
        rebuildChips(); return root;
    }
    private void rebuildChips() {
        if (chips == null) return;
        boolean hadFocus = chips.hasFocus(); chips.removeAllViews();
        int selected = BrowsePresenter.instance(requireContext()).getNavigationTab(root());
        for (int id : NatjeNavigation.children(root())) {
            View button = NatjeChips.add(chips, getString(NatjeNavigation.label(id)), id == selected, () -> {
                BrowsePresenter.instance(requireContext()).selectNavigationTab(root(), id); rebuildChips();
            }, () -> { if (getBrowseGrid() != null) getBrowseGrid().requestFocus(); });
            if (id == selected) selectedChip = button;
        }
        if (hadFocus && selectedChip != null) selectedChip.requestFocus();
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
    }
    @Override public void onDestroyView() { chips = null; selectedChip = null; super.onDestroyView(); }
}
