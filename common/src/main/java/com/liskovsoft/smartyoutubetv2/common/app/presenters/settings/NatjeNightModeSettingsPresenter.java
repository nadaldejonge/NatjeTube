package com.liskovsoft.smartyoutubetv2.common.app.presenters.settings;

import android.content.Context;
import android.content.Intent;

/** Entry point shared by settings and the sidebar; the TV module owns its concrete controls. */
public final class NatjeNightModeSettingsPresenter {
    private NatjeNightModeSettingsPresenter() { }
    public static void show(Context context) {
        Intent i=new Intent().setClassName(context,"com.liskovsoft.smartyoutubetv2.tv.ui.settings.NatjeNightModeSettingsActivity");
        if (!(context instanceof android.app.Activity)) i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
