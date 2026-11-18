package com.liskovsoft.smartyoutubetv2.tv.ui.browse.video;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv2.tv.R;

/** Uses the existing NatjeTube typography, palette and focus drawable. */
public final class NatjeChips {
    private NatjeChips() {}
    public static View wrap(Context context, View grid, LinearLayout chips, CharSequence heading) {
        LinearLayout root = new LinearLayout(context); root.setOrientation(LinearLayout.VERTICAL);
        int space = context.getResources().getDimensionPixelSize(R.dimen.natje_space);
        root.setPadding(space, space, space, 0);
        TextView title = new TextView(context); title.setTextAppearance(context, R.style.Natje_Text_Metadata);
        title.setText(heading); title.setPadding(space, space / 2, space, space / 2); root.addView(title);
        HorizontalScrollView scroll = new HorizontalScrollView(context); scroll.setHorizontalScrollBarEnabled(false);
        scroll.setFillViewport(false); scroll.setFocusable(false); scroll.addView(chips);
        scroll.setPadding(0, space / 2, 0, space);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -2));
        root.addView(grid, new LinearLayout.LayoutParams(-1, 0, 1));
        return root;
    }
    public static boolean focusSidebar(View view) {
        if (view == null) return false;
        android.content.Context context = view.getContext();
        while (context instanceof android.content.ContextWrapper && !(context instanceof androidx.fragment.app.FragmentActivity))
            context = ((android.content.ContextWrapper) context).getBaseContext();
        if (!(context instanceof androidx.fragment.app.FragmentActivity)) return false;
        for (androidx.fragment.app.Fragment fragment : ((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager().getFragments()) {
            if (fragment instanceof com.liskovsoft.smartyoutubetv2.tv.ui.browse.BrowseFragment) {
                ((com.liskovsoft.smartyoutubetv2.tv.ui.browse.BrowseFragment) fragment).focusSidebar(); return true;
            }
        }
        return false;
    }
    public static TextView add(LinearLayout chips, String label, boolean selected, Runnable click, Runnable down) {
        Context c = chips.getContext(); int space = c.getResources().getDimensionPixelSize(R.dimen.natje_space);
        TextView button = new TextView(c); button.setTextAppearance(c, R.style.Natje_Text_Body);
        button.setText(label); button.setSelected(selected); button.setContentDescription(label);
        button.setSingleLine(true);
        button.setFocusable(true); button.setClickable(true); button.setGravity(Gravity.CENTER);
        button.setPadding(space, space / 2, space, space / 2); button.setBackgroundResource(R.drawable.natje_focus);
        button.setOnClickListener(v -> click.run());
        button.setOnKeyListener((v, code, event) -> {
            if (code == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) { down.run(); return true; }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (code == KeyEvent.KEYCODE_DPAD_UP) return true;
                if (code == KeyEvent.KEYCODE_DPAD_LEFT && chips.indexOfChild(v) == 0) return focusSidebar(v);
            }
            return false;
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, c.getResources().getDimensionPixelSize(R.dimen.natje_control_height));
        params.setMargins(space / 4, 0, space / 4, space / 2); chips.addView(button, params);
        return button;
    }
}
