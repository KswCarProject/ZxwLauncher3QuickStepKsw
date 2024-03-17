package com.android.quickstep;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.window.CachedDisplayInfo;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class OrientationTouchTransformer {
    private static final boolean DEBUG = false;
    private static final int QUICKSTEP_ROTATION_UNINITIALIZED = -1;
    private static final String TAG = "OrientationTouchTransformer";
    private int mActiveTouchRotation;
    private final RectF mAssistantLeftRegion = new RectF();
    private final RectF mAssistantRightRegion = new RectF();
    private CachedDisplayInfo mCachedDisplayInfo = new CachedDisplayInfo();
    private QuickStepContractInfo mContractInfo;
    private boolean mEnableMultipleRegions;
    private OrientationRectF mLastRectTouched;
    private DisplayController.NavigationMode mMode;
    private int mNavBarGesturalHeight;
    private final int mNavBarLargerGesturalHeight;
    private final RectF mOneHandedModeRegion = new RectF();
    private int mQuickStepStartingRotation = -1;
    private Resources mResources;
    private final Map<CachedDisplayInfo, OrientationRectF> mSwipeTouchRegions = new HashMap();

    interface QuickStepContractInfo {
        float getWindowCornerRadius();
    }

    OrientationTouchTransformer(Resources resources, DisplayController.NavigationMode navigationMode, QuickStepContractInfo quickStepContractInfo) {
        this.mResources = resources;
        this.mMode = navigationMode;
        this.mContractInfo = quickStepContractInfo;
        int navbarSize = getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
        this.mNavBarGesturalHeight = navbarSize;
        this.mNavBarLargerGesturalHeight = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_LARGER_SIZE, resources, navbarSize);
    }

    private void refreshTouchRegion(DisplayController.Info info, Resources resources) {
        this.mResources = resources;
        this.mSwipeTouchRegions.clear();
        resetSwipeRegions(info);
    }

    /* access modifiers changed from: package-private */
    public void setNavigationMode(DisplayController.NavigationMode navigationMode, DisplayController.Info info, Resources resources) {
        if (this.mMode != navigationMode) {
            this.mMode = navigationMode;
            refreshTouchRegion(info, resources);
        }
    }

    /* access modifiers changed from: package-private */
    public void setGesturalHeight(int i, DisplayController.Info info, Resources resources) {
        if (this.mNavBarGesturalHeight != i) {
            this.mNavBarGesturalHeight = i;
            refreshTouchRegion(info, resources);
        }
    }

    /* access modifiers changed from: package-private */
    public void createOrAddTouchRegion(DisplayController.Info info) {
        CachedDisplayInfo cachedDisplayInfo = new CachedDisplayInfo(info.currentSize, info.rotation);
        this.mCachedDisplayInfo = cachedDisplayInfo;
        if (this.mQuickStepStartingRotation > -1 && cachedDisplayInfo.rotation == this.mQuickStepStartingRotation) {
            resetSwipeRegions(info);
        } else if (this.mSwipeTouchRegions.get(this.mCachedDisplayInfo) == null) {
            if (this.mEnableMultipleRegions) {
                this.mSwipeTouchRegions.put(this.mCachedDisplayInfo, createRegionForDisplay(info));
            } else {
                resetSwipeRegions(info);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void enableMultipleRegions(boolean z, DisplayController.Info info) {
        boolean z2 = z && this.mMode != DisplayController.NavigationMode.TWO_BUTTONS;
        this.mEnableMultipleRegions = z2;
        if (z2) {
            this.mQuickStepStartingRotation = info.rotation;
        } else {
            this.mActiveTouchRotation = 0;
            this.mQuickStepStartingRotation = -1;
        }
        resetSwipeRegions(info);
    }

    /* access modifiers changed from: package-private */
    public void setSingleActiveRegion(DisplayController.Info info) {
        this.mActiveTouchRotation = info.rotation;
        resetSwipeRegions(info);
    }

    private void resetSwipeRegions(DisplayController.Info info) {
        CachedDisplayInfo cachedDisplayInfo = new CachedDisplayInfo(info.currentSize, info.rotation);
        this.mCachedDisplayInfo = cachedDisplayInfo;
        OrientationRectF orientationRectF = this.mSwipeTouchRegions.get(cachedDisplayInfo);
        if (orientationRectF == null) {
            orientationRectF = createRegionForDisplay(info);
        }
        this.mSwipeTouchRegions.clear();
        this.mSwipeTouchRegions.put(this.mCachedDisplayInfo, orientationRectF);
        updateAssistantRegions(orientationRectF);
    }

    private void resetSwipeRegions() {
        OrientationRectF orientationRectF = this.mSwipeTouchRegions.get(this.mCachedDisplayInfo);
        this.mSwipeTouchRegions.clear();
        if (orientationRectF != null) {
            this.mSwipeTouchRegions.put(this.mCachedDisplayInfo, orientationRectF);
            updateAssistantRegions(orientationRectF);
        }
    }

    private OrientationRectF createRegionForDisplay(DisplayController.Info info) {
        Point point = info.currentSize;
        int i = info.rotation;
        int i2 = this.mNavBarGesturalHeight;
        OrientationRectF orientationRectF = new OrientationRectF(0.0f, 0.0f, (float) point.x, (float) point.y, i);
        if (this.mMode == DisplayController.NavigationMode.NO_BUTTON) {
            orientationRectF.top = orientationRectF.bottom - ((float) i2);
            updateAssistantRegions(orientationRectF);
        } else {
            this.mAssistantLeftRegion.setEmpty();
            this.mAssistantRightRegion.setEmpty();
            int navbarSize = getNavbarSize(ResourceUtils.NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE);
            if (i == 1) {
                orientationRectF.left = orientationRectF.right - ((float) navbarSize);
            } else if (i != 3) {
                orientationRectF.top = orientationRectF.bottom - ((float) i2);
            } else {
                orientationRectF.right = orientationRectF.left + ((float) navbarSize);
            }
        }
        this.mOneHandedModeRegion.set(0.0f, orientationRectF.bottom - ((float) this.mNavBarLargerGesturalHeight), (float) point.x, (float) point.y);
        return orientationRectF;
    }

    private void updateAssistantRegions(OrientationRectF orientationRectF) {
        int navbarSize = getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
        int dimensionPixelSize = this.mResources.getDimensionPixelSize(R.dimen.gestures_assistant_width);
        float max = Math.max((float) navbarSize, this.mContractInfo.getWindowCornerRadius());
        RectF rectF = this.mAssistantLeftRegion;
        RectF rectF2 = this.mAssistantRightRegion;
        float f = orientationRectF.bottom;
        rectF2.bottom = f;
        rectF.bottom = f;
        RectF rectF3 = this.mAssistantLeftRegion;
        RectF rectF4 = this.mAssistantRightRegion;
        float f2 = orientationRectF.bottom - max;
        rectF4.top = f2;
        rectF3.top = f2;
        this.mAssistantLeftRegion.left = 0.0f;
        float f3 = (float) dimensionPixelSize;
        this.mAssistantLeftRegion.right = f3;
        this.mAssistantRightRegion.right = orientationRectF.right;
        this.mAssistantRightRegion.left = orientationRectF.right - f3;
    }

    /* access modifiers changed from: package-private */
    public boolean touchInAssistantRegion(MotionEvent motionEvent) {
        return this.mAssistantLeftRegion.contains(motionEvent.getX(), motionEvent.getY()) || this.mAssistantRightRegion.contains(motionEvent.getX(), motionEvent.getY());
    }

    /* access modifiers changed from: package-private */
    public boolean touchInOneHandedModeRegion(MotionEvent motionEvent) {
        return this.mOneHandedModeRegion.contains(motionEvent.getX(), motionEvent.getY());
    }

    private int getNavbarSize(String str) {
        return ResourceUtils.getNavbarSize(str, this.mResources);
    }

    /* access modifiers changed from: package-private */
    public boolean touchInValidSwipeRegions(float f, float f2) {
        OrientationRectF orientationRectF = this.mLastRectTouched;
        if (orientationRectF != null) {
            return orientationRectF.contains(f, f2);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentActiveRotation() {
        return this.mActiveTouchRotation;
    }

    /* access modifiers changed from: package-private */
    public int getQuickStepStartingRotation() {
        return this.mQuickStepStartingRotation;
    }

    public void transform(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            return;
                        }
                    }
                } else if (this.mLastRectTouched != null) {
                    if (!TaskAnimationManager.SHELL_TRANSITIONS_ROTATION) {
                        this.mLastRectTouched.applyTransformFromRotation(motionEvent, this.mCachedDisplayInfo.rotation, true);
                        return;
                    } else if (motionEvent.getSurfaceRotation() != this.mActiveTouchRotation) {
                        this.mLastRectTouched.applyTransform(motionEvent, RotationHelper.deltaRotation(motionEvent.getSurfaceRotation(), this.mActiveTouchRotation), true);
                        return;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            if (this.mLastRectTouched != null) {
                if (!TaskAnimationManager.SHELL_TRANSITIONS_ROTATION) {
                    this.mLastRectTouched.applyTransformFromRotation(motionEvent, this.mCachedDisplayInfo.rotation, true);
                } else if (motionEvent.getSurfaceRotation() != this.mActiveTouchRotation) {
                    this.mLastRectTouched.applyTransform(motionEvent, RotationHelper.deltaRotation(motionEvent.getSurfaceRotation(), this.mActiveTouchRotation), true);
                }
                this.mLastRectTouched = null;
                return;
            }
            return;
        }
        if (this.mLastRectTouched == null) {
            for (OrientationRectF next : this.mSwipeTouchRegions.values()) {
                if (next != null && next.applyTransformFromRotation(motionEvent, this.mCachedDisplayInfo.rotation, false)) {
                    this.mLastRectTouched = next;
                    this.mActiveTouchRotation = next.getRotation();
                    if (this.mEnableMultipleRegions && this.mCachedDisplayInfo.rotation == this.mActiveTouchRotation) {
                        this.mQuickStepStartingRotation = this.mLastRectTouched.getRotation();
                        resetSwipeRegions();
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("OrientationTouchTransformerState: ");
        printWriter.println("  currentActiveRotation=" + getCurrentActiveRotation());
        printWriter.println("  lastTouchedRegion=" + this.mLastRectTouched);
        printWriter.println("  multipleRegionsEnabled=" + this.mEnableMultipleRegions);
        StringBuilder sb = new StringBuilder("  currentTouchableRotations=");
        for (CachedDisplayInfo cachedDisplayInfo : this.mSwipeTouchRegions.keySet()) {
            sb.append(this.mSwipeTouchRegions.get(cachedDisplayInfo)).append(" ");
        }
        printWriter.println(sb.toString());
        printWriter.println("  mNavBarGesturalHeight=" + this.mNavBarGesturalHeight);
        printWriter.println("  mNavBarLargerGesturalHeight=" + this.mNavBarLargerGesturalHeight);
        printWriter.println("  mOneHandedModeRegion=" + this.mOneHandedModeRegion);
    }
}
