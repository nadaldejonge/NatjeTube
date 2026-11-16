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
    private TextView search, night, settings;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        View list = super.onCreateView(inflater, parent, state);
        LinearLayout root = new LinearLayout(requireContext()); root.setOrientation(LinearLayout.VERTICAL);
        int space = getResources().getDimensionPixelSize(R.dimen.natje_space);
        root.setPadding(0, space, 0, space);
        search = control(getString(R.string.nav_search));
        search.setOnClickListener(v -> SearchPresenter.instance(requireContext()).startSearch(null));
        root.addView(search); root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        night = control(getString(R.string.night_title));
        night.setOnClickListener(v -> NatjeNightModeManager.instance(requireContext()).toggle());
        night.setOnLongClickListener(v -> { NatjeNightModeSettingsPresenter.show(requireContext()); return true; });
        night.setOnKeyListener((v, code, event) -> {
            if (KeyHelpers.isMenuKey(code)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) NatjeNightModeSettingsPresenter.show(requireContext());
                return true;
            }
            return false;
        });
        settings = control(getString(R.string.header_settings));
        settings.setOnClickListener(v -> NatjeNavigation.showSettings(requireContext()));
        root.addView(night); root.addView(settings);
        night.setNextFocusDownId(settings.getId()); settings.setNextFocusUpId(night.getId());
        return root;
    }
    private TextView control(String label) {
        TextView button = new TextView(requireContext()); button.setId(View.generateViewId());
        button.setTextAppearance(requireContext(), R.style.Natje_Text_Menu); button.setText(label);
        button.setGravity(Gravity.CENTER_VERTICAL); button.setFocusable(true); button.setClickable(true);
        int space = getResources().getDimensionPixelSize(R.dimen.natje_space);
        button.setPadding(space, 0, space, 0); button.setBackgroundResource(R.drawable.natje_header_focus);
        button.setLayoutParams(new LinearLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.natje_control_height)));
        return button;
    }
    @Override public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        if (getVerticalGridView() != null) {
            View grid = getVerticalGridView(); grid.setPadding(grid.getPaddingLeft(), 0, grid.getPaddingRight(), 0);
            night.setNextFocusUpId(grid.getId()); search.setNextFocusDownId(grid.getId());
        }
    }
    @Override public void onResume() { super.onResume(); NatjeNightModeManager.instance(requireContext()).addListener(this); }
    @Override public void onPause() { NatjeNightModeManager.instance(requireContext()).removeListener(this); super.onPause(); }
    @Override public void onNightModeChanged() {
        if (night != null) { night.setSelected(NatjeNightModeManager.instance(requireContext()).isActive());
            night.setText(getString(R.string.night_title) + " · " + getString(
                    NatjeNightModeManager.instance(requireContext()).isActive() ? R.string.night_on : R.string.night_off));
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
