package com.liskovsoft.smartyoutubetv2.tv.ui.browse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightModeManager;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.settings.NatjeNightModeSettingsPresenter;
import com.liskovsoft.smartyoutubetv2.tv.R;

/** Action landing panel. Merely focusing the header never toggles or fetches media. */
public final class NatjeNightActionFragment extends Fragment implements NatjeNightModeManager.Listener {
    private TextView status;
    @Override public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle state) {
        LinearLayout panel=new LinearLayout(requireContext());panel.setOrientation(LinearLayout.VERTICAL);
        int margin=getResources().getDimensionPixelSize(R.dimen.natje_page_margin);panel.setPadding(margin,margin,margin,margin);
        TextView title=new TextView(requireContext());title.setTextAppearance(requireContext(),R.style.Natje_Text_Title);title.setText(R.string.night_title);panel.addView(title);
        status=new TextView(requireContext());status.setTextAppearance(requireContext(),R.style.Natje_Text_Section);panel.addView(status);
        TextView hint=new TextView(requireContext());hint.setTextAppearance(requireContext(),R.style.Natje_Text_Body);hint.setText(R.string.night_hint);panel.addView(hint);
        Button settings=new Button(requireContext());settings.setText(R.string.night_title);settings.setAllCaps(false);
        settings.setBackgroundResource(R.drawable.natje_focus);settings.setOnClickListener(v->NatjeNightModeSettingsPresenter.show(requireContext()));panel.addView(settings);
        return panel;
    }
    @Override public void onResume(){super.onResume();NatjeNightModeManager.instance(requireContext()).addListener(this);}
    @Override public void onPause(){NatjeNightModeManager.instance(requireContext()).removeListener(this);super.onPause();}
    @Override public void onDestroyView(){status=null;super.onDestroyView();}
    @Override public void onNightModeChanged(){if(status!=null)status.setText(NatjeNightModeManager.instance(requireContext()).status());}
}
