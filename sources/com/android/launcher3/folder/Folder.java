package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.content.res.ResourcesCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Alarm;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.anim.KeyboardInsetAnimationCallback;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemFactory;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.views.ClipPathView;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Folder extends AbstractFloatingView implements ClipPathView, DragSource, View.OnLongClickListener, DropTarget, FolderInfo.FolderListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, DragController.DragListener, ExtendedEditText.OnBackKeyListener {
    private static final boolean DEBUG = false;
    private static final int FOLDER_COLOR_ANIMATION_DURATION = 200;
    private static final CharSequence FOLDER_LABEL_DELIMITER = "~";
    private static final int FOLDER_NAME_ANIMATION_DURATION = 633;
    private static final float ICON_OVERSCROLL_WIDTH_FACTOR = 0.45f;
    public static final Comparator<ItemInfo> ITEM_POS_COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
            int i;
            int i2;
            if (itemInfo.rank != itemInfo2.rank) {
                i = itemInfo.rank;
                i2 = itemInfo2.rank;
            } else if (itemInfo.cellY != itemInfo2.cellY) {
                i = itemInfo.cellY;
                i2 = itemInfo2.cellY;
            } else {
                i = itemInfo.cellX;
                i2 = itemInfo2.cellX;
            }
            return i - i2;
        }
    };
    private static final int MIN_CONTENT_DIMEN = 5;
    private static final int MIN_FOLDERS_FOR_HARDWARE_OPTIMIZATION = 10;
    private static final int ON_EXIT_CLOSE_DELAY = 400;
    private static final int REORDER_DELAY = 250;
    private static final int RESCROLL_EXTRA_DELAY = 150;
    public static final int SCROLL_HINT_DURATION = 500;
    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_NONE = -1;
    public static final int SCROLL_RIGHT = 1;
    public static final int STATE_ANIMATING = 1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_OPEN = 2;
    private static final String TAG = "Launcher.Folder";
    private static final Rect sTempRect = new Rect();
    protected final ActivityContext mActivityContext;
    private GradientDrawable mBackground;
    private Path mClipPath;
    FolderPagedView mContent;
    /* access modifiers changed from: private */
    public AnimatorSet mCurrentAnimator;
    private View mCurrentDragView;
    int mCurrentScrollDir = -1;
    private boolean mDeleteFolderOnDropCompleted = false;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mDestroyed;
    protected DragController mDragController;
    private boolean mDragInProgress = false;
    int mEmptyCellRank;
    FolderIcon mFolderIcon;
    public FolderNameEditText mFolderName;
    protected View mFooter;
    private int mFooterHeight;
    private LauncherAtom.FromState mFromLabelState;
    private CharSequence mFromTitle;
    public FolderInfo mInfo;
    /* access modifiers changed from: private */
    public boolean mIsAnimatingClosed = false;
    private boolean mIsEditingName = false;
    private boolean mIsExternalDrag;
    private boolean mItemAddedBackToSelfViaIcon = false;
    final ArrayList<View> mItemsInReadingOrder = new ArrayList<>();
    boolean mItemsInvalidated = false;
    /* access modifiers changed from: private */
    public KeyboardInsetAnimationCallback mKeyboardInsetAnimationCallback;
    protected final LauncherDelegate mLauncherDelegate;
    private final Alarm mOnExitAlarm = new Alarm();
    OnAlarmListener mOnExitAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.completeDragExit();
        }
    };
    private OnFolderStateChangedListener mOnFolderStateChangedListener;
    private final Alarm mOnScrollHintAlarm = new Alarm();
    /* access modifiers changed from: private */
    public PageIndicatorDots mPageIndicator;
    int mPrevTargetRank;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mRearrangeOnClose = false;
    private final Alarm mReorderAlarm = new Alarm();
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.mContent.realTimeReorder(Folder.this.mEmptyCellRank, Folder.this.mTargetRank);
            Folder folder = Folder.this;
            folder.mEmptyCellRank = folder.mTargetRank;
        }
    };
    private int mScrollAreaOffset;
    int mScrollHintDir = -1;
    final Alarm mScrollPauseAlarm = new Alarm();
    @ViewDebug.ExportedProperty(category = "launcher", mapping = {@ViewDebug.IntToString(from = 0, to = "STATE_CLOSED"), @ViewDebug.IntToString(from = 1, to = "STATE_ANIMATING"), @ViewDebug.IntToString(from = 2, to = "STATE_OPEN")})
    private int mState = 0;
    private StatsLogManager mStatsLogManager;
    private boolean mSuppressFolderDeletion = false;
    int mTargetRank;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FolderState {
    }

    public interface OnFolderStateChangedListener {
        void onFolderStateChanged(int i);
    }

    static /* synthetic */ boolean lambda$getViewForInfo$7(WorkspaceItemInfo workspaceItemInfo, ItemInfo itemInfo, View view) {
        return itemInfo == workspaceItemInfo;
    }

    public boolean canInterceptEventsInSystemGestureRegion() {
        return true;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 1) != 0;
    }

    public Folder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setAlwaysDrawnWithCacheEnabled(false);
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(context);
        this.mActivityContext = activityContext;
        this.mLauncherDelegate = LauncherDelegate.from(activityContext);
        this.mStatsLogManager = StatsLogManager.newInstance(context);
        setFocusableInTouchMode(true);
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        DeviceProfile deviceProfile = this.mActivityContext.getDeviceProfile();
        int i = deviceProfile.folderContentPaddingLeftRight;
        this.mBackground = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.round_rect_folder, getContext().getTheme());
        FolderPagedView folderPagedView = (FolderPagedView) findViewById(R.id.folder_content);
        this.mContent = folderPagedView;
        folderPagedView.setPadding(i, deviceProfile.folderContentPaddingTop, i, 0);
        this.mContent.setFolder(this);
        this.mPageIndicator = (PageIndicatorDots) findViewById(R.id.folder_page_indicator);
        FolderNameEditText folderNameEditText = (FolderNameEditText) findViewById(R.id.folder_name);
        this.mFolderName = folderNameEditText;
        folderNameEditText.setTextSize(0, (float) deviceProfile.folderLabelTextSizePx);
        this.mFolderName.setOnBackKeyListener(this);
        this.mFolderName.setOnFocusChangeListener(this);
        this.mFolderName.setOnEditorActionListener(this);
        this.mFolderName.setSelectAllOnFocus(true);
        FolderNameEditText folderNameEditText2 = this.mFolderName;
        folderNameEditText2.setInputType((folderNameEditText2.getInputType() & -32769) | 524288 | 8192);
        this.mFolderName.forceDisableSuggestions(true);
        this.mFooter = findViewById(R.id.folder_footer);
        this.mFooterHeight = getResources().getDimensionPixelSize(R.dimen.folder_label_height);
        if (Utilities.ATLEAST_R) {
            KeyboardInsetAnimationCallback keyboardInsetAnimationCallback = new KeyboardInsetAnimationCallback(this);
            this.mKeyboardInsetAnimationCallback = keyboardInsetAnimationCallback;
            setWindowInsetsAnimationCallback(keyboardInsetAnimationCallback);
        }
    }

    public boolean onLongClick(View view) {
        if (!this.mLauncherDelegate.isDraggingEnabled()) {
            return true;
        }
        return startDrag(view, new DragOptions());
    }

    public boolean startDrag(View view, DragOptions dragOptions) {
        Object tag = view.getTag();
        if (!(tag instanceof WorkspaceItemInfo)) {
            return true;
        }
        this.mEmptyCellRank = ((WorkspaceItemInfo) tag).rank;
        this.mCurrentDragView = view;
        this.mDragController.addDragListener(this);
        if (dragOptions.isAccessibleDrag) {
            this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this.mContent, $$Lambda$8SF8RcJYqwQZZcMaI1AkwVyLf0.INSTANCE) {
                /* access modifiers changed from: protected */
                public void enableAccessibleDrag(boolean z) {
                    super.enableAccessibleDrag(z);
                    Folder.this.mFooter.setImportantForAccessibility(z ? 4 : 0);
                }
            });
        }
        this.mLauncherDelegate.beginDragShared(view, this, dragOptions);
        return true;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        if (dragObject.dragSource == this) {
            this.mContent.removeItem(this.mCurrentDragView);
            if (dragObject.dragInfo instanceof WorkspaceItemInfo) {
                this.mItemsInvalidated = true;
                SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
                try {
                    this.mInfo.remove((WorkspaceItemInfo) dragObject.dragInfo, true);
                    suppressInfoChanges.close();
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
            return;
        }
        return;
        throw th;
    }

    public void onDragEnd() {
        if (this.mIsExternalDrag && this.mDragInProgress) {
            completeDragExit();
        }
        this.mDragInProgress = false;
        this.mDragController.removeDragListener(this);
    }

    public boolean isEditingName() {
        return this.mIsEditingName;
    }

    public void startEditingFolderName() {
        post(new Runnable() {
            public final void run() {
                Folder.this.lambda$startEditingFolderName$0$Folder();
            }
        });
    }

    public /* synthetic */ void lambda$startEditingFolderName$0$Folder() {
        if (FeatureFlags.FOLDER_NAME_SUGGEST.get()) {
            showLabelSuggestions();
        }
        this.mFolderName.setHint("");
        this.mIsEditingName = true;
    }

    public boolean onBackKey() {
        String obj = this.mFolderName.getText().toString();
        this.mInfo.setTitle(obj, this.mLauncherDelegate.getModelWriter());
        this.mFolderIcon.onTitleChanged(obj);
        if (TextUtils.isEmpty(this.mInfo.title)) {
            this.mFolderName.setHint(R.string.folder_hint_text);
            this.mFolderName.setText("");
        } else {
            this.mFolderName.setHint((CharSequence) null);
        }
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(this, 32, getContext().getString(R.string.folder_renamed, new Object[]{obj}));
        this.mFolderName.clearFocus();
        Selection.setSelection(this.mFolderName.getText(), 0, 0);
        this.mIsEditingName = false;
        return true;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.mFolderName.dispatchBackKey();
        return true;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_R) {
            setTranslationY(0.0f);
            if (windowInsets.isVisible(WindowInsets.Type.ime())) {
                Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
                int heightFromBottom = getHeightFromBottom();
                if (insets.bottom > heightFromBottom) {
                    setTranslationY((float) ((heightFromBottom - insets.bottom) - this.mFolderName.getPaddingBottom()));
                }
            }
        }
        return windowInsets;
    }

    public FolderIcon getFolderIcon() {
        return this.mFolderIcon;
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setFolderIcon(FolderIcon folderIcon) {
        this.mFolderIcon = folderIcon;
        this.mLauncherDelegate.init(this, folderIcon);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        requestFocus();
        super.onAttachedToWindow();
    }

    public View focusSearch(int i) {
        return FocusFinder.getInstance().findNextFocus(this, (View) null, i);
    }

    public FolderInfo getInfo() {
        return this.mInfo;
    }

    /* access modifiers changed from: package-private */
    public void bind(FolderInfo folderInfo) {
        this.mInfo = folderInfo;
        this.mFromTitle = folderInfo.title;
        this.mFromLabelState = folderInfo.getFromLabelState();
        Collections.sort(folderInfo.contents, ITEM_POS_COMPARATOR);
        updateItemLocationsInDatabaseBatch(true);
        if (((BaseDragLayer.LayoutParams) getLayoutParams()) == null) {
            BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(0, 0);
            layoutParams.customPosition = true;
            setLayoutParams(layoutParams);
        }
        this.mItemsInvalidated = true;
        this.mInfo.addListener(this);
        if (!TextUtils.isEmpty(this.mInfo.title)) {
            this.mFolderName.setText(this.mInfo.title);
            this.mFolderName.setHint((CharSequence) null);
        } else {
            this.mFolderName.setText("");
            this.mFolderName.setHint(R.string.folder_hint_text);
        }
        this.mFolderIcon.post(new Runnable() {
            public final void run() {
                Folder.this.lambda$bind$1$Folder();
            }
        });
    }

    public /* synthetic */ void lambda$bind$1$Folder() {
        if (getItemCount() <= 1) {
            replaceFolderWithFinalItem();
        }
    }

    private void showLabelSuggestions() {
        if (this.mInfo.suggestedFolderNames != null && this.mInfo.suggestedFolderNames.hasSuggestions()) {
            if (TextUtils.isEmpty(this.mFolderName.getText()) && this.mInfo.suggestedFolderNames.hasPrimary()) {
                this.mFolderName.setHint("");
                this.mFolderName.setText(this.mInfo.suggestedFolderNames.getLabels()[0]);
                this.mFolderName.selectAll();
            }
            this.mFolderName.showKeyboard();
            this.mFolderName.displayCompletions((List) Stream.of(this.mInfo.suggestedFolderNames.getLabels()).filter($$Lambda$Folder$7h7UCwt0omQ5tvXZAQRgoe6uRo.INSTANCE).map($$Lambda$Folder$yJ3SHxQYa26IoVX5ZxK453cCCJU.INSTANCE).filter($$Lambda$Folder$I6qfg4P67PzkJ5VPeDUsnGuHcIE.INSTANCE).filter(new Predicate() {
                public final boolean test(Object obj) {
                    return Folder.this.lambda$showLabelSuggestions$3$Folder((String) obj);
                }
            }).collect(Collectors.toList()));
        }
    }

    static /* synthetic */ boolean lambda$showLabelSuggestions$2(String str) {
        return !str.isEmpty();
    }

    public /* synthetic */ boolean lambda$showLabelSuggestions$3$Folder(String str) {
        return !str.equalsIgnoreCase(this.mFolderName.getText().toString());
    }

    static <T extends Context & ActivityContext> Folder fromXml(T t) {
        return (Folder) LayoutInflater.from(t).cloneInContext(t).inflate(R.layout.user_folder_icon_normalized, (ViewGroup) null);
    }

    private void startAnimation(final AnimatorSet animatorSet) {
        this.mLauncherDelegate.forEachVisibleWorkspacePage(new Consumer(animatorSet) {
            public final /* synthetic */ AnimatorSet f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                Folder.this.lambda$startAnimation$4$Folder(this.f$1, (View) obj);
            }
        });
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Folder.this.setState(1);
                AnimatorSet unused = Folder.this.mCurrentAnimator = animatorSet;
            }

            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = Folder.this.mCurrentAnimator = null;
            }
        });
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: addAnimatorListenerForPage */
    public void lambda$startAnimation$4$Folder(AnimatorSet animatorSet, final CellLayout cellLayout) {
        final boolean shouldUseHardwareLayerForAnimation = shouldUseHardwareLayerForAnimation(cellLayout);
        final boolean isHardwareLayerEnabled = cellLayout.isHardwareLayerEnabled();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (shouldUseHardwareLayerForAnimation) {
                    cellLayout.enableHardwareLayer(true);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (shouldUseHardwareLayerForAnimation) {
                    cellLayout.enableHardwareLayer(isHardwareLayerEnabled);
                }
            }
        });
    }

    private boolean shouldUseHardwareLayerForAnimation(CellLayout cellLayout) {
        if (FeatureFlags.ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS.get()) {
            return true;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        int i = 0;
        for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = shortcutsAndWidgets.getChildAt(childCount);
            if (childAt instanceof AppWidgetHostView) {
                return false;
            }
            if (childAt instanceof FolderIcon) {
                i++;
            }
        }
        if (i >= 10) {
            return true;
        }
        return false;
    }

    public void beginExternalDrag() {
        this.mIsExternalDrag = true;
        this.mDragInProgress = true;
        this.mDragController.addDragListener(this);
        ArrayList arrayList = new ArrayList(this.mInfo.contents);
        this.mEmptyCellRank = arrayList.size();
        arrayList.add((Object) null);
        animateOpen(arrayList, this.mEmptyCellRank / this.mContent.itemsPerPage());
    }

    public void animateOpen() {
        animateOpen(this.mInfo.contents, 0);
    }

    private void animateOpen(List<WorkspaceItemInfo> list, int i) {
        Folder open = getOpen(this.mActivityContext);
        if (!(open == null || open == this)) {
            open.close(true);
        }
        this.mContent.bindItems(list);
        centerAboutIcon();
        this.mItemsInvalidated = true;
        updateTextViewFocus();
        this.mIsOpen = true;
        BaseDragLayer dragLayer = this.mActivityContext.getDragLayer();
        if (getParent() == null) {
            dragLayer.addView(this);
            this.mDragController.addDropTarget(this);
        }
        this.mContent.completePendingPageChanges();
        this.mContent.setCurrentPage(i);
        this.mDeleteFolderOnDropCompleted = false;
        cancelRunningAnimations();
        AnimatorSet animator = new FolderAnimationManager(this, true).getAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Folder.this.mFolderIcon.setIconVisible(false);
                Folder.this.mFolderIcon.drawLeaveBehindIfExists();
            }

            public void onAnimationEnd(Animator animator) {
                Folder.this.setState(2);
                Folder.this.announceAccessibilityChanges();
                AccessibilityManagerCompat.sendFolderOpenedEventToTest(Folder.this.getContext());
                Folder.this.mContent.setFocusOnFirstChild();
            }
        });
        if (this.mContent.getPageCount() <= 1 || this.mInfo.hasOption(4)) {
            this.mFolderName.setTranslationX(0.0f);
        } else {
            float desiredWidth = (((float) ((this.mContent.getDesiredWidth() - this.mFooter.getPaddingLeft()) - this.mFooter.getPaddingRight())) - this.mFolderName.getPaint().measureText(this.mFolderName.getText().toString())) / 2.0f;
            FolderNameEditText folderNameEditText = this.mFolderName;
            if (this.mContent.mIsRtl) {
                desiredWidth = -desiredWidth;
            }
            folderNameEditText.setTranslationX(desiredWidth);
            this.mPageIndicator.prepareEntryAnimation();
            final boolean z = !this.mDragInProgress;
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    Folder.this.mFolderName.animate().setDuration(633).translationX(0.0f).setInterpolator(AnimationUtils.loadInterpolator(Folder.this.getContext(), AndroidResources.FAST_OUT_SLOW_IN));
                    Folder.this.mPageIndicator.playEntryAnimation();
                    if (z) {
                        Folder.this.mInfo.setOption(4, true, Folder.this.mLauncherDelegate.getModelWriter());
                    }
                }
            });
        }
        this.mPageIndicator.stopAllAnimations();
        startAnimation(animator);
        animator.setCurrentPlayTime(Math.min((long) RefreshRateTracker.getSingleFrameMs(getContext()), animator.getTotalDuration()));
        if (this.mDragController.isDragging()) {
            this.mDragController.forceTouchMove();
        }
        FolderPagedView folderPagedView = this.mContent;
        folderPagedView.verifyVisibleHighResIcons(folderPagedView.getNextPage());
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        AnimatorSet animatorSet;
        this.mIsOpen = false;
        if (!z && (animatorSet = this.mCurrentAnimator) != null && animatorSet.isRunning()) {
            this.mCurrentAnimator.cancel();
        }
        if (isEditingName()) {
            this.mFolderName.dispatchBackKey();
        }
        FolderIcon folderIcon = this.mFolderIcon;
        if (folderIcon != null) {
            folderIcon.clearLeaveBehindIfExists();
        }
        if (z) {
            animateClosed();
        } else {
            closeComplete(false);
            post(new Runnable() {
                public final void run() {
                    Folder.this.announceAccessibilityChanges();
                }
            });
        }
        this.mActivityContext.getDragLayer().sendAccessibilityEvent(32);
    }

    private void cancelRunningAnimations() {
        AnimatorSet animatorSet = this.mCurrentAnimator;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mCurrentAnimator.cancel();
        }
    }

    private void animateClosed() {
        if (!this.mIsAnimatingClosed) {
            this.mContent.completePendingPageChanges();
            FolderPagedView folderPagedView = this.mContent;
            folderPagedView.snapToPageImmediately(folderPagedView.getDestinationPage());
            cancelRunningAnimations();
            AnimatorSet animator = new FolderAnimationManager(this, false).getAnimator();
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    if (Utilities.ATLEAST_R) {
                        Folder.this.setWindowInsetsAnimationCallback((WindowInsetsAnimation.Callback) null);
                    }
                    boolean unused = Folder.this.mIsAnimatingClosed = true;
                }

                public void onAnimationEnd(Animator animator) {
                    if (Utilities.ATLEAST_R && Folder.this.mKeyboardInsetAnimationCallback != null) {
                        Folder folder = Folder.this;
                        folder.setWindowInsetsAnimationCallback(folder.mKeyboardInsetAnimationCallback);
                    }
                    Folder.this.closeComplete(true);
                    Folder.this.announceAccessibilityChanges();
                    boolean unused = Folder.this.mIsAnimatingClosed = false;
                }
            });
            startAnimation(animator);
        }
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        String str;
        FolderPagedView folderPagedView = this.mContent;
        if (this.mIsOpen) {
            str = this.mContent.getAccessibilityDescription();
        } else {
            str = getContext().getString(R.string.folder_closed);
        }
        return Pair.create(folderPagedView, str);
    }

    /* access modifiers changed from: protected */
    public View getAccessibilityInitialFocusView() {
        View firstItem = this.mContent.getFirstItem();
        return firstItem != null ? firstItem : super.getAccessibilityInitialFocusView();
    }

    /* access modifiers changed from: private */
    public void closeComplete(boolean z) {
        BaseDragLayer baseDragLayer = (BaseDragLayer) getParent();
        if (baseDragLayer != null) {
            baseDragLayer.removeView(this);
        }
        this.mDragController.removeDropTarget(this);
        clearFocus();
        FolderIcon folderIcon = this.mFolderIcon;
        if (folderIcon != null) {
            folderIcon.setVisibility(0);
            this.mFolderIcon.setIconVisible(true);
            this.mFolderIcon.mFolderName.setTextVisibility(true);
            if (z) {
                this.mFolderIcon.animateBgShadowAndStroke();
                this.mFolderIcon.onFolderClose(this.mContent.getCurrentPage());
                if (this.mFolderIcon.hasDot()) {
                    this.mFolderIcon.animateDotScale(0.0f, 1.0f);
                }
                this.mFolderIcon.requestFocus();
            }
        }
        if (this.mRearrangeOnClose) {
            rearrangeChildren();
            this.mRearrangeOnClose = false;
        }
        if (getItemCount() <= 1) {
            boolean z2 = this.mDragInProgress;
            if (!z2 && !this.mSuppressFolderDeletion) {
                replaceFolderWithFinalItem();
            } else if (z2) {
                this.mDeleteFolderOnDropCompleted = true;
            }
        } else if (!this.mDragInProgress) {
            this.mContent.unbindItems();
        }
        this.mSuppressFolderDeletion = false;
        clearDragInfo();
        setState(0);
        this.mContent.setCurrentPage(0);
    }

    public boolean acceptDrop(DropTarget.DragObject dragObject) {
        int i = dragObject.dragInfo.itemType;
        return i == 0 || i == 1 || i == 6;
    }

    public void onDragEnter(DropTarget.DragObject dragObject) {
        this.mPrevTargetRank = -1;
        this.mOnExitAlarm.cancelAlarm();
        this.mScrollAreaOffset = (dragObject.dragView.getDragRegionWidth() / 2) - dragObject.xOffset;
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    private int getTargetRank(DropTarget.DragObject dragObject, float[] fArr) {
        float[] visualCenter = dragObject.getVisualCenter(fArr);
        return this.mContent.findNearestArea(((int) visualCenter[0]) - getPaddingLeft(), ((int) visualCenter[1]) - getPaddingTop());
    }

    public void onDragOver(DropTarget.DragObject dragObject) {
        if (!this.mScrollPauseAlarm.alarmPending()) {
            float[] fArr = new float[2];
            int targetRank = getTargetRank(dragObject, fArr);
            this.mTargetRank = targetRank;
            if (targetRank != this.mPrevTargetRank) {
                this.mReorderAlarm.cancelAlarm();
                this.mReorderAlarm.setOnAlarmListener(this.mReorderAlarmListener);
                this.mReorderAlarm.setAlarm(250);
                this.mPrevTargetRank = this.mTargetRank;
                if (dragObject.stateAnnouncer != null) {
                    dragObject.stateAnnouncer.announce(getContext().getString(R.string.move_to_position, new Object[]{Integer.valueOf(this.mTargetRank + 1)}));
                }
            }
            float f = fArr[0];
            int nextPage = this.mContent.getNextPage();
            float cellWidth = ((float) this.mContent.getCurrentCellLayout().getCellWidth()) * ICON_OVERSCROLL_WIDTH_FACTOR;
            boolean z = f < cellWidth;
            boolean z2 = f > ((float) getWidth()) - cellWidth;
            if (nextPage > 0 && (!this.mContent.mIsRtl ? z : z2)) {
                showScrollHint(0, dragObject);
            } else if (nextPage >= this.mContent.getPageCount() - 1 || (!this.mContent.mIsRtl ? !z2 : !z)) {
                this.mOnScrollHintAlarm.cancelAlarm();
                if (this.mScrollHintDir != -1) {
                    this.mContent.clearScrollHint();
                    this.mScrollHintDir = -1;
                }
            } else {
                showScrollHint(1, dragObject);
            }
        }
    }

    private void showScrollHint(int i, DropTarget.DragObject dragObject) {
        if (this.mScrollHintDir != i) {
            this.mContent.showScrollHint(i);
            this.mScrollHintDir = i;
        }
        if (!this.mOnScrollHintAlarm.alarmPending() || this.mCurrentScrollDir != i) {
            this.mCurrentScrollDir = i;
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mOnScrollHintAlarm.setOnAlarmListener(new OnScrollHintListener(dragObject));
            this.mOnScrollHintAlarm.setAlarm(500);
            this.mReorderAlarm.cancelAlarm();
            this.mTargetRank = this.mEmptyCellRank;
        }
    }

    public void completeDragExit() {
        if (this.mIsOpen) {
            close(true);
            this.mRearrangeOnClose = true;
        } else if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
            clearDragInfo();
        }
    }

    private void clearDragInfo() {
        this.mCurrentDragView = null;
        this.mIsExternalDrag = false;
    }

    public void onDragExit(DropTarget.DragObject dragObject) {
        if (!dragObject.dragComplete) {
            this.mOnExitAlarm.setOnAlarmListener(this.mOnExitAlarmListener);
            this.mOnExitAlarm.setAlarm(400);
        }
        this.mReorderAlarm.cancelAlarm();
        this.mOnScrollHintAlarm.cancelAlarm();
        this.mScrollPauseAlarm.cancelAlarm();
        if (this.mScrollHintDir != -1) {
            this.mContent.clearScrollHint();
            this.mScrollHintDir = -1;
        }
    }

    public void prepareAccessibilityDrop() {
        if (this.mReorderAlarm.alarmPending()) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
        }
    }

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        if (!z) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) dragObject.dragInfo;
            View view2 = this.mCurrentDragView;
            View createNewView = (view2 == null || view2.getTag() != workspaceItemInfo) ? this.mContent.createNewView(workspaceItemInfo) : this.mCurrentDragView;
            ArrayList<View> iconsInReadingOrder = getIconsInReadingOrder();
            workspaceItemInfo.rank = Utilities.boundToRange(workspaceItemInfo.rank, 0, iconsInReadingOrder.size());
            iconsInReadingOrder.add(workspaceItemInfo.rank, createNewView);
            this.mContent.arrangeChildren(iconsInReadingOrder);
            this.mItemsInvalidated = true;
            SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
            try {
                this.mFolderIcon.onDrop(dragObject, true);
                suppressInfoChanges.close();
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        } else if (this.mDeleteFolderOnDropCompleted && !this.mItemAddedBackToSelfViaIcon && view != this) {
            replaceFolderWithFinalItem();
        }
        if (view != this && this.mOnExitAlarm.alarmPending()) {
            this.mOnExitAlarm.cancelAlarm();
            if (!z) {
                this.mSuppressFolderDeletion = true;
            }
            this.mScrollPauseAlarm.cancelAlarm();
            completeDragExit();
        }
        this.mDeleteFolderOnDropCompleted = false;
        this.mDragInProgress = false;
        this.mItemAddedBackToSelfViaIcon = false;
        this.mCurrentDragView = null;
        updateItemLocationsInDatabaseBatch(false);
        if (getItemCount() <= this.mContent.itemsPerPage()) {
            this.mInfo.setOption(4, false, this.mLauncherDelegate.getModelWriter());
            return;
        }
        return;
        throw th;
    }

    private void updateItemLocationsInDatabaseBatch(boolean z) {
        FolderGridOrganizer folderInfo = new FolderGridOrganizer(this.mActivityContext.getDeviceProfile().inv).setFolderInfo(this.mInfo);
        ArrayList arrayList = new ArrayList();
        int size = this.mInfo.contents.size();
        for (int i = 0; i < size; i++) {
            WorkspaceItemInfo workspaceItemInfo = this.mInfo.contents.get(i);
            if (folderInfo.updateRankAndPos(workspaceItemInfo, i)) {
                arrayList.add(workspaceItemInfo);
            }
        }
        if (!arrayList.isEmpty()) {
            this.mLauncherDelegate.getModelWriter().moveItemsInDatabase(arrayList, this.mInfo.id, 0);
        }
        if (FeatureFlags.FOLDER_NAME_SUGGEST.get() && !z && size > 1) {
            Executors.MODEL_EXECUTOR.post(new Runnable() {
                public final void run() {
                    Folder.this.lambda$updateItemLocationsInDatabaseBatch$6$Folder();
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateItemLocationsInDatabaseBatch$6$Folder() {
        FolderNameInfos folderNameInfos = new FolderNameInfos();
        FolderNameProvider.newInstance(getContext()).getSuggestedFolderName(getContext(), this.mInfo.contents, folderNameInfos);
        this.mInfo.suggestedFolderNames = folderNameInfos;
    }

    public void notifyDrop() {
        if (this.mDragInProgress) {
            this.mItemAddedBackToSelfViaIcon = true;
        }
    }

    public boolean isDropEnabled() {
        return this.mState != 1;
    }

    private void centerAboutIcon() {
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        BaseDragLayer dragLayer = this.mActivityContext.getDragLayer();
        int folderWidth = getFolderWidth();
        int folderHeight = getFolderHeight();
        FolderIcon folderIcon = this.mFolderIcon;
        Rect rect = sTempRect;
        dragLayer.getDescendantRectRelativeToSelf(folderIcon, rect);
        int i = folderWidth / 2;
        int centerX = rect.centerX() - i;
        int i2 = folderHeight / 2;
        int centerY = rect.centerY() - i2;
        rect.set(this.mActivityContext.getFolderBoundingBox());
        int[] iArr = {Utilities.boundToRange(centerX, rect.left, rect.right - folderWidth), Utilities.boundToRange(centerY, rect.top, rect.bottom - folderHeight)};
        this.mActivityContext.updateOpenFolderPosition(iArr, rect, folderWidth, folderHeight);
        int i3 = iArr[0];
        int i4 = iArr[1];
        setPivotX((float) (i + (centerX - i3)));
        setPivotY((float) (i2 + (centerY - i4)));
        layoutParams.width = folderWidth;
        layoutParams.height = folderHeight;
        layoutParams.x = i3;
        layoutParams.y = i4;
        this.mBackground.setBounds(0, 0, folderWidth, folderHeight);
    }

    /* access modifiers changed from: protected */
    public int getContentAreaHeight() {
        DeviceProfile deviceProfile = this.mActivityContext.getDeviceProfile();
        return Math.max(Math.min((deviceProfile.availableHeightPx - deviceProfile.getTotalWorkspacePadding().y) - this.mFooterHeight, this.mContent.getDesiredHeight()), 5);
    }

    private int getContentAreaWidth() {
        return Math.max(this.mContent.getDesiredWidth(), 5);
    }

    private int getFolderWidth() {
        return getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth();
    }

    private int getFolderHeight() {
        return getFolderHeight(getContentAreaHeight());
    }

    private int getFolderHeight(int i) {
        return getPaddingTop() + getPaddingBottom() + i + this.mFooterHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int contentAreaWidth = getContentAreaWidth();
        int contentAreaHeight = getContentAreaHeight();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(contentAreaWidth, BasicMeasure.EXACTLY);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(contentAreaHeight, BasicMeasure.EXACTLY);
        this.mContent.setFixedSize(contentAreaWidth, contentAreaHeight);
        this.mContent.measure(makeMeasureSpec, makeMeasureSpec2);
        if (this.mContent.getChildCount() > 0) {
            int cellWidth = (this.mContent.getPageAt(0).getCellWidth() - this.mActivityContext.getDeviceProfile().iconSizePx) / 2;
            this.mFooter.setPadding(this.mContent.getPaddingLeft() + cellWidth, this.mFooter.getPaddingTop(), this.mContent.getPaddingRight() + cellWidth, this.mFooter.getPaddingBottom());
        }
        this.mFooter.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mFooterHeight, BasicMeasure.EXACTLY));
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentAreaWidth, getFolderHeight(contentAreaHeight));
    }

    public void rearrangeChildren() {
        if (this.mContent.areViewsBound()) {
            this.mContent.arrangeChildren(getIconsInReadingOrder());
            this.mItemsInvalidated = true;
        }
    }

    public int getItemCount() {
        return this.mInfo.contents.size();
    }

    /* access modifiers changed from: package-private */
    public void replaceFolderWithFinalItem() {
        this.mDestroyed = this.mLauncherDelegate.replaceFolderWithFinalItem(this);
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void updateTextViewFocus() {
        View firstItem = this.mContent.getFirstItem();
        final View lastItem = this.mContent.getLastItem();
        if (firstItem == null || lastItem == null) {
            setOnKeyListener((View.OnKeyListener) null);
            return;
        }
        this.mFolderName.setNextFocusDownId(lastItem.getId());
        this.mFolderName.setNextFocusRightId(lastItem.getId());
        this.mFolderName.setNextFocusLeftId(lastItem.getId());
        this.mFolderName.setNextFocusUpId(lastItem.getId());
        this.mFolderName.setNextFocusForwardId(firstItem.getId());
        setNextFocusDownId(firstItem.getId());
        setNextFocusRightId(firstItem.getId());
        setNextFocusLeftId(firstItem.getId());
        setNextFocusUpId(firstItem.getId());
        setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = true;
                if (i != 61 || !keyEvent.hasModifiers(1)) {
                    z = false;
                }
                if (!z || !Folder.this.isFocused()) {
                    return false;
                }
                return lastItem.requestFocus();
            }
        });
    }

    public void onDrop(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        View view;
        if (!this.mContent.rankOnCurrentPage(this.mEmptyCellRank)) {
            this.mTargetRank = getTargetRank(dragObject, (float[]) null);
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mScrollPauseAlarm.cancelAlarm();
        }
        this.mContent.completePendingPageChanges();
        Launcher launcher = this.mLauncherDelegate.getLauncher();
        if (launcher != null) {
            PendingAddShortcutInfo pendingAddShortcutInfo = dragObject.dragInfo instanceof PendingAddShortcutInfo ? (PendingAddShortcutInfo) dragObject.dragInfo : null;
            WorkspaceItemInfo createWorkspaceItemInfo = pendingAddShortcutInfo != null ? pendingAddShortcutInfo.activityInfo.createWorkspaceItemInfo() : null;
            if (pendingAddShortcutInfo == null || createWorkspaceItemInfo != null) {
                if (createWorkspaceItemInfo == null) {
                    if (dragObject.dragInfo instanceof WorkspaceItemFactory) {
                        createWorkspaceItemInfo = ((WorkspaceItemFactory) dragObject.dragInfo).makeWorkspaceItem(launcher);
                    } else {
                        createWorkspaceItemInfo = (WorkspaceItemInfo) dragObject.dragInfo;
                    }
                }
                if (this.mIsExternalDrag) {
                    view = this.mContent.createAndAddViewForRank(createWorkspaceItemInfo, this.mEmptyCellRank);
                    this.mLauncherDelegate.getModelWriter().addOrMoveItemInDatabase(createWorkspaceItemInfo, this.mInfo.id, 0, createWorkspaceItemInfo.cellX, createWorkspaceItemInfo.cellY);
                    this.mIsExternalDrag = false;
                } else {
                    view = this.mCurrentDragView;
                    this.mContent.addViewForRank(view, createWorkspaceItemInfo, this.mEmptyCellRank);
                }
                if (dragObject.dragView.hasDrawn()) {
                    float scaleX = getScaleX();
                    float scaleY = getScaleY();
                    setScaleX(1.0f);
                    setScaleY(1.0f);
                    launcher.getDragLayer().animateViewIntoPosition(dragObject.dragView, view, (View) null);
                    setScaleX(scaleX);
                    setScaleY(scaleY);
                } else {
                    dragObject.deferDragViewCleanupPostAnimation = false;
                    view.setVisibility(0);
                }
                this.mItemsInvalidated = true;
                rearrangeChildren();
                SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
                try {
                    this.mInfo.add(createWorkspaceItemInfo, this.mEmptyCellRank, false);
                    suppressInfoChanges.close();
                    if (dragObject.dragSource != this) {
                        updateItemLocationsInDatabaseBatch(false);
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            } else {
                pendingAddShortcutInfo.container = this.mInfo.id;
                pendingAddShortcutInfo.rank = this.mEmptyCellRank;
                launcher.addPendingItem(pendingAddShortcutInfo, pendingAddShortcutInfo.container, pendingAddShortcutInfo.screenId, (int[]) null, pendingAddShortcutInfo.spanX, pendingAddShortcutInfo.spanY);
                dragObject.deferDragViewCleanupPostAnimation = false;
                this.mRearrangeOnClose = true;
            }
            this.mDragInProgress = false;
            if (this.mContent.getPageCount() > 1) {
                this.mInfo.setOption(4, true, this.mLauncherDelegate.getModelWriter());
            }
            launcher.getStateManager().goToState(LauncherState.NORMAL, 500);
            if (dragObject.stateAnnouncer != null) {
                dragObject.stateAnnouncer.completeAction(R.string.item_moved);
            }
            this.mStatsLogManager.logger().withItemInfo(dragObject.dragInfo).withInstanceId(dragObject.logInstanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_COMPLETED);
            return;
        }
        return;
        throw th;
    }

    public void hideItem(WorkspaceItemInfo workspaceItemInfo) {
        View viewForInfo = getViewForInfo(workspaceItemInfo);
        if (viewForInfo != null) {
            viewForInfo.setVisibility(4);
        }
    }

    public void showItem(WorkspaceItemInfo workspaceItemInfo) {
        View viewForInfo = getViewForInfo(workspaceItemInfo);
        if (viewForInfo != null) {
            viewForInfo.setVisibility(0);
        }
    }

    public void onAdd(WorkspaceItemInfo workspaceItemInfo, int i) {
        new FolderGridOrganizer(this.mActivityContext.getDeviceProfile().inv).setFolderInfo(this.mInfo).updateRankAndPos(workspaceItemInfo, i);
        this.mLauncherDelegate.getModelWriter().addOrMoveItemInDatabase(workspaceItemInfo, this.mInfo.id, 0, workspaceItemInfo.cellX, workspaceItemInfo.cellY);
        updateItemLocationsInDatabaseBatch(false);
        if (this.mContent.areViewsBound()) {
            this.mContent.createAndAddViewForRank(workspaceItemInfo, i);
        }
        this.mItemsInvalidated = true;
    }

    public void onRemove(List<WorkspaceItemInfo> list) {
        this.mItemsInvalidated = true;
        Stream map = list.stream().map(new Function() {
            public final Object apply(Object obj) {
                return Folder.this.getViewForInfo((WorkspaceItemInfo) obj);
            }
        });
        FolderPagedView folderPagedView = this.mContent;
        Objects.requireNonNull(folderPagedView);
        map.forEach(new Consumer() {
            public final void accept(Object obj) {
                FolderPagedView.this.removeItem((View) obj);
            }
        });
        if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
        }
        if (getItemCount() > 1) {
            return;
        }
        if (this.mIsOpen) {
            close(true);
        } else {
            replaceFolderWithFinalItem();
        }
    }

    /* access modifiers changed from: private */
    public View getViewForInfo(WorkspaceItemInfo workspaceItemInfo) {
        return this.mContent.iterateOverItems(new LauncherBindableItemsContainer.ItemOperator() {
            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return Folder.lambda$getViewForInfo$7(WorkspaceItemInfo.this, itemInfo, view);
            }
        });
    }

    public void onItemsChanged(boolean z) {
        updateTextViewFocus();
    }

    public void iterateOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        this.mContent.iterateOverItems(itemOperator);
    }

    public ArrayList<View> getIconsInReadingOrder() {
        if (this.mItemsInvalidated) {
            this.mItemsInReadingOrder.clear();
            this.mContent.iterateOverItems(new LauncherBindableItemsContainer.ItemOperator() {
                public final boolean evaluate(ItemInfo itemInfo, View view) {
                    return Folder.this.lambda$getIconsInReadingOrder$8$Folder(itemInfo, view);
                }
            });
            this.mItemsInvalidated = false;
        }
        return this.mItemsInReadingOrder;
    }

    public /* synthetic */ boolean lambda$getIconsInReadingOrder$8$Folder(ItemInfo itemInfo, View view) {
        return !this.mItemsInReadingOrder.add(view);
    }

    public List<BubbleTextView> getItemsOnPage(int i) {
        ArrayList<View> iconsInReadingOrder = getIconsInReadingOrder();
        int pageCount = this.mContent.getPageCount() - 1;
        int size = iconsInReadingOrder.size();
        int itemsPerPage = this.mContent.itemsPerPage();
        int i2 = i == pageCount ? size - (itemsPerPage * i) : itemsPerPage;
        int i3 = i * itemsPerPage;
        int min = Math.min(i3 + i2, iconsInReadingOrder.size());
        ArrayList arrayList = new ArrayList(i2);
        while (i3 < min) {
            arrayList.add((BubbleTextView) iconsInReadingOrder.get(i3));
            i3++;
        }
        return arrayList;
    }

    public void onFocusChange(View view, boolean z) {
        LauncherAtom.ToState toState;
        if (view != this.mFolderName) {
            return;
        }
        if (z) {
            this.mFromLabelState = this.mInfo.getFromLabelState();
            this.mFromTitle = this.mInfo.title;
            startEditingFolderName();
            return;
        }
        StatsLogManager.StatsLogger withFromState = this.mStatsLogManager.logger().withItemInfo(this.mInfo).withFromState(this.mFromLabelState);
        StringJoiner stringJoiner = new StringJoiner(FOLDER_LABEL_DELIMITER);
        if (this.mFromLabelState.equals(LauncherAtom.FromState.FROM_SUGGESTED)) {
            stringJoiner.add(this.mFromTitle);
        }
        CharSequence charSequence = this.mFromTitle;
        if (charSequence == null || !charSequence.equals(this.mInfo.title)) {
            toState = this.mInfo.getToLabelState();
            if (toState.toString().startsWith("TO_SUGGESTION")) {
                stringJoiner.add(this.mInfo.title);
            }
        } else {
            toState = LauncherAtom.ToState.UNCHANGED;
        }
        withFromState.withToState(toState);
        if (stringJoiner.length() > 0) {
            withFromState.withEditText(stringJoiner.toString());
        }
        withFromState.log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_LABEL_UPDATED);
        this.mFolderName.dispatchBackKey();
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        getHitRect(rect);
        rect.left -= this.mScrollAreaOffset;
        rect.right += this.mScrollAreaOffset;
    }

    private class OnScrollHintListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollHintListener(DropTarget.DragObject dragObject) {
            this.mDragObject = dragObject;
        }

        public void onAlarm(Alarm alarm) {
            if (Folder.this.mCurrentScrollDir == 0) {
                Folder.this.mContent.scrollLeft();
                Folder.this.mScrollHintDir = -1;
            } else if (Folder.this.mCurrentScrollDir == 1) {
                Folder.this.mContent.scrollRight();
                Folder.this.mScrollHintDir = -1;
            } else {
                return;
            }
            Folder.this.mCurrentScrollDir = -1;
            Folder.this.mScrollPauseAlarm.setOnAlarmListener(new OnScrollFinishedListener(this.mDragObject));
            Folder.this.mScrollPauseAlarm.setAlarm((long) (Folder.this.getResources().getInteger(R.integer.config_pageSnapAnimationDuration) + 150));
        }
    }

    private class OnScrollFinishedListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollFinishedListener(DropTarget.DragObject dragObject) {
            this.mDragObject = dragObject;
        }

        public void onAlarm(Alarm alarm) {
            Folder.this.onDragOver(this.mDragObject);
        }
    }

    private class SuppressInfoChanges implements AutoCloseable {
        SuppressInfoChanges() {
            Folder.this.mInfo.removeListener(Folder.this);
        }

        public void close() {
            Folder.this.mInfo.addListener(Folder.this);
            Folder.this.updateTextViewFocus();
        }
    }

    public static Folder getOpen(ActivityContext activityContext) {
        return (Folder) getOpenView(activityContext, 1);
    }

    public boolean onBackPressed() {
        if (isEditingName()) {
            this.mFolderName.dispatchBackKey();
            return true;
        }
        super.onBackPressed();
        return true;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            BaseDragLayer baseDragLayer = (BaseDragLayer) getParent();
            if (isEditingName()) {
                if (baseDragLayer.isEventOverView(this.mFolderName, motionEvent)) {
                    return false;
                }
                this.mFolderName.dispatchBackKey();
                return true;
            } else if (baseDragLayer.isEventOverView(this, motionEvent) || !this.mLauncherDelegate.interceptOutsideTouch(motionEvent, baseDragLayer, this)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void setClipPath(Path path) {
        this.mClipPath = path;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mClipPath != null) {
            int save = canvas.save();
            canvas.clipPath(this.mClipPath);
            this.mBackground.draw(canvas);
            canvas.restoreToCount(save);
            super.dispatchDraw(canvas);
            return;
        }
        this.mBackground.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public FolderPagedView getContent() {
        return this.mContent;
    }

    private int getHeightFromBottom() {
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        return this.mActivityContext.getDeviceProfile().heightPx - (layoutParams.y + layoutParams.height);
    }

    /* access modifiers changed from: private */
    public void setState(int i) {
        this.mState = i;
        OnFolderStateChangedListener onFolderStateChangedListener = this.mOnFolderStateChangedListener;
        if (onFolderStateChangedListener != null) {
            onFolderStateChangedListener.onFolderStateChanged(i);
        }
    }

    public void setOnFolderStateChangedListener(OnFolderStateChangedListener onFolderStateChangedListener) {
        this.mOnFolderStateChangedListener = onFolderStateChangedListener;
    }
}
