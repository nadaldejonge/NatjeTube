package com.liskovsoft.smartyoutubetv2.tv;

import android.app.Application;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;
import android.view.KeyEvent;
import android.view.ViewGroup;
import androidx.leanback.widget.PageRow;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.app.BrowseSupportFragment;
import com.liskovsoft.smartyoutubetv2.common.app.models.data.BrowseSection;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightModeManager;
import com.liskovsoft.smartyoutubetv2.common.misc.NatjeNightOverlayView;
import com.liskovsoft.smartyoutubetv2.tv.presenter.IconHeaderItemPresenter;
import com.liskovsoft.smartyoutubetv2.tv.ui.browse.SectionHeaderItem;
import com.liskovsoft.smartyoutubetv2.tv.ui.browse.BrowseSectionFragmentFactory;
import com.liskovsoft.smartyoutubetv2.tv.ui.browse.NatjeNightActionFragment;
import com.liskovsoft.smartyoutubetv2.tv.ui.settings.NatjeNightModeSettingsActivity;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.NatjeRangeControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,application=Application.class)
public class NatjeTvUiTest {
    @Before public void reset() throws Exception {
        RuntimeEnvironment.getApplication().getSharedPreferences("natje_night_mode",0).edit().clear().commit();
        Field f=NatjeNightModeManager.class.getDeclaredField("instance");f.setAccessible(true);f.set(null,null);
    }
    @Test public void headerBindingAndFocusDoNotToggle() {
        Application c=RuntimeEnvironment.getApplication();c.setTheme(R.style.App_Theme_DarkGrey_Browse);
        NatjeNightModeManager manager=NatjeNightModeManager.instance(c);
        IconHeaderItemPresenter presenter=new IconHeaderItemPresenter(R.drawable.ic_natje_night_mode,null);
        RowHeaderPresenter.ViewHolder holder=presenter.onCreateViewHolder(new FrameLayout(c));
        presenter.onBindViewHolder(holder,new PageRow(new SectionHeaderItem(new BrowseSection(NatjeNightModeManager.SECTION_ID,
                "NatjeNightMode",BrowseSection.TYPE_ACTION,R.drawable.ic_natje_night_mode,false))));
        holder.view.requestFocus();assertFalse(manager.isActive());
        TextView status=holder.view.findViewById(R.id.night_status);assertEquals(View.VISIBLE,status.getVisibility());
        assertEquals(manager.status(),status.getText().toString());presenter.onUnbindViewHolder(holder);
    }
    @Test public void selectingNightPageThroughLeanbackDoesNotCrashOrToggle() throws Exception {
        BrowseSupportFragment browse = new BrowseSupportFragment();
        BrowseSectionFragmentFactory factory = new BrowseSectionFragmentFactory();
        browse.getMainFragmentRegistry().registerFragment(PageRow.class, factory);
        ArrayObjectAdapter rows = new ArrayObjectAdapter();
        rows.add(new PageRow(new SectionHeaderItem(new BrowseSection(NatjeNightModeManager.SECTION_ID,
                "NatjeNightMode", BrowseSection.TYPE_ACTION, R.drawable.ic_natje_night_mode, false))));
        rows.add(new PageRow(new SectionHeaderItem(new BrowseSection(123,
                "Settings", BrowseSection.TYPE_SETTINGS_GRID, R.drawable.ic_natje_night_mode, false))));
        // Exercise the exact Leanback selection path that rejects a plain Fragment on header focus.
        Method select = BrowseSupportFragment.class.getDeclaredMethod("createMainFragment", ObjectAdapter.class, int.class);
        select.setAccessible(true);
        for (int position : new int[]{0, 1, 0}) assertEquals(true, select.invoke(browse, rows, position));
        assertTrue(browse.getMainFragment() instanceof NatjeNightActionFragment);
        NatjeNightActionFragment night = (NatjeNightActionFragment) browse.getMainFragment();
        assertSame(night, night.getMainFragmentAdapter().getFragment());
        assertNotNull(night.getMainFragmentAdapter().getFragmentHost());
        assertFalse(NatjeNightModeManager.instance(RuntimeEnvironment.getApplication()).isActive());
        factory.cleanup();
    }
    @Test public void warmthRangeIsStrongProgressiveAndNeutralAtZero() {
        int neutral = renderNightPixel(0, 0);
        int medium = renderNightPixel(50, 0);
        int maximum = renderNightPixel(100, 0);
        assertEquals(Color.WHITE, neutral);
        assertEquals(255, Color.red(maximum));
        assertTrue(Color.blue(maximum) <= 160);
        assertTrue(Color.blue(maximum) < Color.blue(medium));
        assertTrue(Color.blue(medium) < Color.blue(neutral));
        assertTrue(Color.green(maximum) > Color.blue(maximum));
    }
    @Test public void warmthDoesNotChangeTheIndependentDimmingRange() {
        int dimOnly = renderNightPixel(0, 40);
        int warmAndDim = renderNightPixel(100, 40);
        assertEquals(153, Color.red(dimOnly), 1);
        assertEquals(Color.red(dimOnly), Color.green(dimOnly));
        assertEquals(Color.red(dimOnly), Color.blue(dimOnly));
        assertEquals(Color.red(dimOnly), Color.red(warmAndDim), 1);
        assertTrue(Color.blue(warmAndDim) < Color.blue(dimOnly));
    }
    private int renderNightPixel(int warmth, int dim) {
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class).setup();
        NatjeNightModeManager manager = NatjeNightModeManager.instance(controller.get());
        manager.data().enabled = true; manager.data().warmth = warmth;
        manager.data().dim = dim; manager.data().transition = 0; manager.changed();
        NatjeNightOverlayView overlay = new NatjeNightOverlayView(controller.get());
        overlay.layout(0, 0, 2, 2); overlay.resume();
        Bitmap bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); canvas.drawColor(Color.WHITE); overlay.draw(canvas);
        int result = bitmap.getPixel(0, 0);
        bitmap.recycle(); overlay.release(); controller.pause().stop().destroy();
        return result;
    }
    @Test public void rangeInitialisationDoesNotPersistButRemoteChangeDoes() {
        Application c=RuntimeEnvironment.getApplication();int[] changes={0};
        NatjeRangeControl control=new NatjeRangeControl(c,"Dim",80,30,v->changes[0]++);
        control.setValue(45);assertEquals(0,changes[0]);
        SeekBar bar=(SeekBar)control.getChildAt(1);assertTrue(bar.isFocusable());assertEquals(80,bar.getMax());
    }
    @Test public void settingsActivityStartsAndRestoresWithoutTouchingAccounts() {
        Application c=RuntimeEnvironment.getApplication();c.getSharedPreferences("account-sentinel",0).edit().putString("token","keep").commit();
        ActivityController<NatjeNightModeSettingsActivity> controller=Robolectric.buildActivity(NatjeNightModeSettingsActivity.class).setup();
        assertNotNull(controller.get().getWindow().getDecorView().findFocus());
        controller.pause().stop().destroy();
        assertEquals("keep",c.getSharedPreferences("account-sentinel",0).getString("token",null));
    }
    @Test public void overlayNeverTakesRemoteFocusAndDetaches() {
        ActivityController<Activity> controller=Robolectric.buildActivity(Activity.class).setup();
        Activity activity=controller.get();NatjeNightOverlayView overlay=new NatjeNightOverlayView(activity);
        overlay.resume();assertFalse(overlay.isFocusable());assertFalse(overlay.isClickable());
        overlay.pause();assertEquals(View.GONE,overlay.getVisibility());overlay.release();assertNull(overlay.getParent());
        controller.pause().stop().destroy();
    }
    @Test public void settingsSwitchAndDpadRangePersistAcrossReopen() throws Exception {
        ActivityController<NatjeNightModeSettingsActivity> controller=Robolectric.buildActivity(NatjeNightModeSettingsActivity.class).setup();
        NatjeNightModeManager manager=NatjeNightModeManager.instance(controller.get());
        View root=controller.get().getWindow().getDecorView();
        Switch toggle=find(root,Switch.class);assertNotNull(toggle);toggle.performClick();assertTrue(manager.isActive());
        SeekBar range=find(root,SeekBar.class);assertNotNull(range);
        range.requestFocus();assertTrue(range.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT)));
        assertEquals(30,manager.data().warmth);
        controller.pause().stop().destroy();
        Field f=NatjeNightModeManager.class.getDeclaredField("instance");f.setAccessible(true);f.set(null,null);
        controller=Robolectric.buildActivity(NatjeNightModeSettingsActivity.class).setup();
        manager=NatjeNightModeManager.instance(controller.get());assertTrue(manager.isActive());assertEquals(30,manager.data().warmth);
        controller.pause().stop().destroy();
    }
    @Test @Config(qualifiers="w960dp-h540dp-land-xhdpi") public void settingsFitSimulated1080pWindow(){assertWindow(1920,1080);}
    @Test @Config(qualifiers="w1920dp-h1080dp-land-xhdpi") public void settingsFitSimulated4kWindow(){assertWindow(3840,2160);}
    private void assertWindow(int width,int height){
        ActivityController<NatjeNightModeSettingsActivity> controller=Robolectric.buildActivity(NatjeNightModeSettingsActivity.class).setup();
        View root=controller.get().getWindow().getDecorView();
        root.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));
        root.layout(0,0,width,height);
        SeekBar range=find(root,SeekBar.class);assertNotNull(range);assertTrue(range.getWidth()>0);assertTrue(range.getWidth()<width);
        assertEquals(width,root.getMeasuredWidth());assertEquals(height,root.getMeasuredHeight());
        controller.pause().stop().destroy();
    }
    private <T extends View> T find(View view,Class<T> type){
        if(type.isInstance(view))return type.cast(view);
        if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++){T match=find(group.getChildAt(i),type);if(match!=null)return match;}}
        return null;
    }
}
