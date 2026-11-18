package com.liskovsoft.smartyoutubetv2.tv.ui.browse;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import androidx.leanback.widget.TitleViewAdapter;
/** Search belongs to the sidebar; no floating search/account/clock/branding layer. */
public final class SidebarTitleView extends FrameLayout implements TitleViewAdapter.Provider {
    private final TitleViewAdapter adapter = new TitleViewAdapter() {
        @Override public View getSearchAffordanceView() { return null; }
        @Override public void setOnSearchClickedListener(View.OnClickListener listener) { }
    };
    public SidebarTitleView(Context context) {
        super(context);
        setId(com.liskovsoft.smartyoutubetv2.tv.R.id.browse_title_group);
        setLayoutParams(new FrameLayout.LayoutParams(0, 0)); setFocusable(false);
    }
    @Override public TitleViewAdapter getTitleViewAdapter() { return adapter; }
}
