package com.liskovsoft.smartyoutubetv2.common.misc;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.content.res.ColorStateList;
import java.util.WeakHashMap;
import com.liskovsoft.smartyoutubetv2.common.R;

/** App-window comfort filter. No input, surface changes, bitmap allocation or video readback. */
public final class NatjeNightOverlayView extends View implements NatjeNightModeManager.Listener,
        ViewTreeObserver.OnGlobalLayoutListener {
    // Keep a neutral zero while allowing a pronounced amber tint at the top of the slider.
    private static final float MAX_WARMTH_OPACITY = 0.60f;
    private final NatjeNightModeManager manager;
    private final ViewGroup root;
    private ValueAnimator animator;
    private float strength, target = -1;
    private boolean bound;
    private final WeakHashMap<TextView,ColorStateList> originalText = new WeakHashMap<>();
    private final WeakHashMap<TextView,Integer> appliedText = new WeakHashMap<>();

    public NatjeNightOverlayView(Activity activity) {
        this(activity,activity.getWindow());
    }
    public NatjeNightOverlayView(android.content.Context context, android.view.Window window) {
        super(context);
        manager=NatjeNightModeManager.instance(context);
        root=(ViewGroup)window.getDecorView();
        setFocusable(false); setFocusableInTouchMode(false); setClickable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        setWillNotDraw(false);
        root.addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }
    public void resume() {
        if (bound) return;
        bound=true; setVisibility(VISIBLE); target=-1;
        // Reopening an already active mode must not flash the unfiltered video.
        strength=manager.isActive()?1:0;
        root.getViewTreeObserver().addOnGlobalLayoutListener(this);
        manager.addListener(this);
    }
    public void pause() {
        if (!bound) return;
        bound=false; manager.removeListener(this);
        root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (animator!=null) animator.cancel();
        setVisibility(GONE); restoreText();
    }
    public void release() { pause(); root.removeView(this); }
    @Override public void onNightModeChanged() {
        float next=manager.isActive()?1:0;
        if (target!=next) {
            target=next;
            if (animator!=null) animator.cancel();
            if (manager.data().transition==0 || strength==target) strength=target;
            else {
                animator=ValueAnimator.ofFloat(strength,target);
                animator.setDuration(manager.data().transition*1000L);
                animator.addUpdateListener(a -> { strength=(float)a.getAnimatedValue(); invalidate(); });
                animator.start();
            }
        }
        softenText(root); invalidate();
    }
    @Override public void onGlobalLayout() {
        if (bound) {
            softenText(root);
            if (root.indexOfChild(this)!=root.getChildCount()-1) bringToFront();
        }
    }
    private void restoreText() {
        for (TextView v:originalText.keySet()) if (v!=null && appliedText.containsKey(v) && v.getCurrentTextColor()==appliedText.get(v))
            v.setTextColor(originalText.get(v));
        originalText.clear(); appliedText.clear();
    }
    private void softenText(View v) {
        if (!manager.isActive() || !manager.data().softenUi) { restoreText(); return; }
        if (v instanceof TextView) {
            TextView t=(TextView)v;
            ColorStateList colors=t.getTextColors(); int c=t.getCurrentTextColor();
            if (appliedText.containsKey(t) && c==appliedText.get(t)) return;
            // Stateful focus colors are deliberately preserved.
            if (!colors.isStateful() && Color.red(c)>200 && Color.green(c)>200 && Color.blue(c)>200) {
                originalText.put(t,colors);
                int toned=Color.argb(Color.alpha(c),Math.min(208,Color.red(c)),Math.min(208,Color.green(c)),Math.min(208,Color.blue(c)));
                appliedText.put(t,toned); t.setTextColor(toned);
            }
        }
        if (v instanceof ViewGroup) {
            ViewGroup group=(ViewGroup)v;
            for (int i=0;i<group.getChildCount();i++) softenText(group.getChildAt(i));
        }
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (strength<=0) return;
        View existing=root.findViewById(R.id.dim_container);
        float base=existing!=null && existing.getVisibility()==VISIBLE && existing.getBackground() instanceof ColorDrawable
                ? Color.alpha(((ColorDrawable)existing.getBackground()).getColor())/255f : 0;
        if (base>=0.999f) return; // Respect the existing full screen-off black.
        float requested=manager.data().dim/100f*strength;
        // Compose to max(existing dim, night dim), rather than multiplying two dimmers.
        float extra=extraDimming(base,requested);
        float warm=manager.data().warmth/100f*MAX_WARMTH_OPACITY*strength*(1-base);
        if (warm>0) canvas.drawColor(Color.argb(Math.round(255*warm),255,171,80));
        if (extra>0) canvas.drawColor(Color.argb(Math.round(255*extra),0,0,0));
    }
    public static float extraDimming(float existing,float requested) {
        return existing>=0.999f?0:Math.max(0,(requested-existing)/(1-existing));
    }
}
