package com.liskovsoft.smartyoutubetv2.tv.ui.mod.leanback.headers;

import android.view.KeyEvent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLayoutChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.leanback.app.HeadersSupportFragment;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.sharedutils.helpers.KeyHelpers;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.SearchPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.settings.NatjeNightModeSettingsPresenter;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightModeManager;
import com.liskovsoft.smartyoutubetv2.common.utils.NatjeNavigation;
import com.liskovsoft.smartyoutubetv2.tv.R;

public class ExtendedHeadersSupportFragment extends HeadersSupportFragment implements NatjeNightModeManager.Listener {
    private View search, night, settings;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        View list = super.onCreateView(inflater, parent, state);
        LinearLayout root = new LinearLayout(requireContext()); root.setOrientation(LinearLayout.VERTICAL);
        // Leanback's dock is wrap_content. The replacement root must retain the original
        // fixed sidebar width; its default MATCH_PARENT params expand the footer over the videos.
        root.setLayoutParams(new ViewGroup.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.lb_browse_headers_width), ViewGroup.LayoutParams.MATCH_PARENT));
        int space = getResources().getDimensionPixelSize(R.dimen.natje_space);
        root.setPadding(space, space, space, space);
        search = control(getString(R.string.nav_search), R.drawable.lb_ic_in_app_search);
        search.setOnClickListener(v -> SearchPresenter.instance(requireContext()).startSearch(null));
        root.addView(search);
        list.setPadding(0, 0, 0, 0);
        View fade = list.findViewById(R.id.fade_out_edge); if (fade != null) fade.setVisibility(View.GONE);
        root.addView(list, new LinearLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.natje_sidebar_row_height) * 4));
        night = control(getString(R.string.nav_night_mode), R.drawable.ic_natje_night_mode);
        night.setOnClickListener(v -> NatjeNightModeManager.instance(requireContext()).toggle());
        night.setOnLongClickListener(v -> { NatjeNightModeSettingsPresenter.show(requireContext()); return true; });
        settings = control(getString(R.string.header_settings), R.drawable.icon_settings);
        settings.setOnClickListener(v -> NatjeNavigation.showSettings(requireContext()));
        root.addView(night); root.addView(settings);
        night.setNextFocusDownId(settings.getId()); settings.setNextFocusUpId(night.getId());
        return root;
    }
    private View control(String label, int icon) {
        View button = LayoutInflater.from(requireContext()).inflate(R.layout.icon_header_item, null);
        button.setId(View.generateViewId());
        ((TextView) button.findViewById(R.id.header_label)).setText(label);
        ((android.widget.ImageView) button.findViewById(R.id.header_icon)).setImageResource(icon);
        button.setFocusable(true); button.setClickable(true);
        float alpha = getResources().getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
        button.setAlpha(alpha);
        button.setOnFocusChangeListener((v, focused) -> v.setAlpha(focused ? 1f : alpha));
        button.setLayoutParams(new LinearLayout.LayoutParams(-1,
                getResources().getDimensionPixelSize(R.dimen.natje_sidebar_row_height)));
        button.setOnKeyListener((v, code, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (code == KeyEvent.KEYCODE_DPAD_RIGHT) return focusContent();
            if (v == search && code == KeyEvent.KEYCODE_DPAD_DOWN) return focusHeader(0);
            if (v == search && code == KeyEvent.KEYCODE_DPAD_UP) return true;
            if (v == night && code == KeyEvent.KEYCODE_DPAD_DOWN) return settings.requestFocus();
            if (v == settings && code == KeyEvent.KEYCODE_DPAD_UP) return night.requestFocus();
            if (v == night && code == KeyEvent.KEYCODE_DPAD_UP) return focusHeader(getAdapter().size() - 1);
            if (v == night && KeyHelpers.isMenuKey(code)) { NatjeNightModeSettingsPresenter.show(requireContext()); return true; }
            if (v == settings && code == KeyEvent.KEYCODE_DPAD_DOWN) return true;
            return false;
        });
        return button;
    }
    private boolean focusHeader(int position) {
        if (getVerticalGridView() == null || position < 0) return false;
        getVerticalGridView().setSelectedPosition(position);
        return getVerticalGridView().requestFocus();
    }
    private boolean focusContent() {
        if (getParentFragment() instanceof com.liskovsoft.smartyoutubetv2.tv.ui.browse.BrowseFragment) {
            ((com.liskovsoft.smartyoutubetv2.tv.ui.browse.BrowseFragment) getParentFragment()).focusOnContent();
            return true;
        }
        return false;
    }
    @Override public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        if (getVerticalGridView() != null) {
            androidx.leanback.widget.VerticalGridView grid = getVerticalGridView();
            grid.setPadding(0, 0, 0, 0); grid.setVerticalSpacing(0);
            grid.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            grid.setWindowAlignment(androidx.leanback.widget.BaseGridView.WINDOW_ALIGN_BOTH_EDGE);
            grid.setWindowAlignmentPreferKeyLineOverLowEdge(false);
            grid.setWindowAlignmentPreferKeyLineOverHighEdge(false);
            grid.setWindowAlignmentOffset(0); grid.setWindowAlignmentOffsetPercent(0);
            grid.setItemAlignmentOffset(0); grid.setItemAlignmentOffsetPercent(0);
            night.setNextFocusUpId(grid.getId()); search.setNextFocusDownId(grid.getId());
        }
    }
    // Browse reapplies the old title baseline during onStart; this column owns its alignment.
    @Override public void setAlignment(int ignoredTitleOffset) {
        super.setAlignment(0);
        if (getVerticalGridView() != null)
            getVerticalGridView().setWindowAlignment(androidx.leanback.widget.BaseGridView.WINDOW_ALIGN_BOTH_EDGE);
    }
    @Override public void onResume() { super.onResume(); NatjeNightModeManager.instance(requireContext()).addListener(this); }
    @Override public void onPause() { NatjeNightModeManager.instance(requireContext()).removeListener(this); super.onPause(); }
    @Override public void onNightModeChanged() {
        if (night != null) { night.setSelected(NatjeNightModeManager.instance(requireContext()).isActive());

            night.setContentDescription(getString(R.string.night_title) + ": " + NatjeNightModeManager.instance(requireContext()).status()); }
    }
    @Override public void onDestroyView() { search = null; night = null; settings = null; super.onDestroyView(); }
    public interface OnHeaderLongPressedListener {
        /**
         * Called when a header item has been long pressed.
         *
         * @param viewHolder Row ViewHolder object corresponding to the selected Header.
         * @param row Row object corresponding to the selected Header.
         */
        void onHeaderLongPressed(RowHeaderPresenter.ViewHolder viewHolder, Row row);
    }

    private OnHeaderLongPressedListener mOnHeaderLongPressedListener;

    public ExtendedHeadersSupportFragment() {
        androidx.leanback.widget.FocusHighlightHelper.setupHeaderItemFocusHighlight(getBridgeAdapter(), false);
        Helpers.setField(this, "mAdapterListener", mCustomAdapterListener);
    }

    public void setOnHeaderLongPressedListener(OnHeaderLongPressedListener listener) {
        mOnHeaderLongPressedListener = listener;
    }

    private final ItemBridgeAdapter.AdapterListener mCustomAdapterListener =
            new ItemBridgeAdapter.AdapterListener() {
                @Override
                public void onCreate(final ItemBridgeAdapter.ViewHolder viewHolder) {
                    ItemBridgeAdapter.Wrapper wrapper = (ItemBridgeAdapter.Wrapper) Helpers.getField(ExtendedHeadersSupportFragment.this, "mWrapper");
                    OnLayoutChangeListener layoutChangeListener = (OnLayoutChangeListener) Helpers.getField(ExtendedHeadersSupportFragment.this, "sLayoutChangeListener");

                    View headerView = viewHolder.getViewHolder().view;
                    int rowWidth = getResources().getDimensionPixelSize(R.dimen.lb_browse_headers_width)
                            - 2 * getResources().getDimensionPixelSize(R.dimen.natje_space);
                    headerView.getLayoutParams().width = rowWidth;
                    headerView.setOnClickListener(v -> {
                        OnHeaderClickedListener onHeaderClickedListener =
                                (OnHeaderClickedListener) Helpers.getField(ExtendedHeadersSupportFragment.this, "mOnHeaderClickedListener");
                        if (onHeaderClickedListener != null) {
                            onHeaderClickedListener.onHeaderClicked(
                                    (RowHeaderPresenter.ViewHolder) viewHolder.getViewHolder(),
                                    (Row) viewHolder.getItem());
                        }
                    });

                    // NEW CODE
                    headerView.setOnLongClickListener(v -> {
                        if (mOnHeaderLongPressedListener != null) {
                            mOnHeaderLongPressedListener.onHeaderLongPressed(
                                    (RowHeaderPresenter.ViewHolder) viewHolder.getViewHolder(),
                                    (Row) viewHolder.getItem());
                            return true; // don't provoke single click event
                        }

                        return false; // work as usual
                    });

                    // NEW CODE
                    headerView.setOnKeyListener((v, keyCode, event) -> {
                        if (event.getAction() == KeyEvent.ACTION_DOWN && getAdapter() != null) {
                            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) return focusContent();
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && getAdapter().size() > 0
                                    && viewHolder.getItem() == getAdapter().get(getAdapter().size() - 1) && night != null)
                                return night.requestFocus();
                            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && getAdapter().size() > 0
                                    && viewHolder.getItem() == getAdapter().get(0) && search != null)
                                return search.requestFocus();
                        }
                        if (mOnHeaderLongPressedListener != null) {
                            if (KeyHelpers.isMenuKey(keyCode)) {
                                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                    mOnHeaderLongPressedListener.onHeaderLongPressed(
                                            (RowHeaderPresenter.ViewHolder) viewHolder.getViewHolder(),
                                            (Row) viewHolder.getItem());
                                }
                            }
                        }

                        return false; // enable navigation events
                    });

                    if (wrapper != null) {
                        viewHolder.itemView.addOnLayoutChangeListener(layoutChangeListener);
                    } else {
                        headerView.addOnLayoutChangeListener(layoutChangeListener);
                    }
                }

            };
}
