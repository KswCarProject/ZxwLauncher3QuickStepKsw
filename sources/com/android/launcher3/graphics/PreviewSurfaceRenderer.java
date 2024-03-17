package com.android.launcher3.graphics;

import android.app.WallpaperColors;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.LauncherPreviewRenderer;
import com.android.launcher3.graphics.PreviewSurfaceRenderer;
import com.android.launcher3.model.AllAppsList;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.GridSizeMigrationTaskV2;
import com.android.launcher3.model.LoaderResults;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.Themes;
import com.android.launcher3.widget.LocalColorExtractor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PreviewSurfaceRenderer {
    private static final int FADE_IN_ANIMATION_DURATION = 200;
    private static final String KEY_COLORS = "wallpaper_colors";
    private static final String KEY_DISPLAY_ID = "display_id";
    private static final String KEY_HOST_TOKEN = "host_token";
    private static final String KEY_VIEW_HEIGHT = "height";
    private static final String KEY_VIEW_WIDTH = "width";
    private static final String TAG = "PreviewSurfaceRenderer";
    private final Context mContext;
    private boolean mDestroyed = false;
    private final Display mDisplay;
    private final int mHeight;
    private final IBinder mHostToken;
    /* access modifiers changed from: private */
    public final InvariantDeviceProfile mIdp;
    /* access modifiers changed from: private */
    public final RunnableList mOnDestroyCallbacks;
    private final SurfaceControlViewHost mSurfaceControlViewHost;
    private final WallpaperColors mWallpaperColors;
    private final int mWidth;

    public PreviewSurfaceRenderer(Context context, Bundle bundle) throws Exception {
        RunnableList runnableList = new RunnableList();
        this.mOnDestroyCallbacks = runnableList;
        this.mContext = context;
        String string = bundle.getString("name");
        bundle.remove("name");
        string = string == null ? InvariantDeviceProfile.getCurrentGridName(context) : string;
        this.mWallpaperColors = (WallpaperColors) bundle.getParcelable(KEY_COLORS);
        this.mIdp = new InvariantDeviceProfile(context, string);
        this.mHostToken = bundle.getBinder(KEY_HOST_TOKEN);
        this.mWidth = bundle.getInt(KEY_VIEW_WIDTH);
        this.mHeight = bundle.getInt(KEY_VIEW_HEIGHT);
        this.mDisplay = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(bundle.getInt(KEY_DISPLAY_ID));
        SurfaceControlViewHost surfaceControlViewHost = (SurfaceControlViewHost) Executors.MAIN_EXECUTOR.submit(new Callable() {
            public final Object call() {
                return PreviewSurfaceRenderer.this.lambda$new$0$PreviewSurfaceRenderer();
            }
        }).get(5, TimeUnit.SECONDS);
        this.mSurfaceControlViewHost = surfaceControlViewHost;
        Objects.requireNonNull(surfaceControlViewHost);
        runnableList.add(new Runnable(surfaceControlViewHost) {
            public final /* synthetic */ SurfaceControlViewHost f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.release();
            }
        });
    }

    public /* synthetic */ SurfaceControlViewHost lambda$new$0$PreviewSurfaceRenderer() throws Exception {
        return new SurfaceControlViewHost(this.mContext, this.mDisplay, this.mHostToken);
    }

    public IBinder getHostToken() {
        return this.mHostToken;
    }

    public SurfaceControlViewHost.SurfacePackage getSurfacePackage() {
        return this.mSurfaceControlViewHost.getSurfacePackage();
    }

    public void destroy() {
        this.mDestroyed = true;
        this.mOnDestroyCallbacks.executeAllAndDestroy();
    }

    public void loadAsync() {
        Executors.MODEL_EXECUTOR.execute(new Runnable() {
            public final void run() {
                PreviewSurfaceRenderer.this.loadModelData();
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadModelData() {
        ContextThemeWrapper contextThemeWrapper;
        boolean doGridMigrationIfNecessary = doGridMigrationIfNecessary();
        if (this.mWallpaperColors != null) {
            Context createDisplayContext = this.mContext.createDisplayContext(this.mDisplay);
            if (Utilities.ATLEAST_R) {
                createDisplayContext = createDisplayContext.createWindowContext(2038, (Bundle) null);
            }
            LocalColorExtractor.newInstance(this.mContext).applyColorsOverride(createDisplayContext, this.mWallpaperColors);
            contextThemeWrapper = new ContextThemeWrapper(createDisplayContext, Themes.getActivityThemeRes(createDisplayContext, this.mWallpaperColors.getColorHints()));
        } else {
            Context context = this.mContext;
            contextThemeWrapper = new ContextThemeWrapper(context, Themes.getActivityThemeRes(context));
        }
        if (doGridMigrationIfNecessary) {
            final LauncherPreviewRenderer.PreviewContext previewContext = new LauncherPreviewRenderer.PreviewContext(contextThemeWrapper, this.mIdp);
            new LoaderTask(LauncherAppState.getInstance(previewContext), (AllAppsList) null, new BgDataModel(), LauncherAppState.getInstance(previewContext).getModel().getModelDelegate(), (LoaderResults) null) {
                public void run() {
                    String str = "screen = 0 or container = -101";
                    if (PreviewSurfaceRenderer.this.mIdp.getDeviceProfile(previewContext).isTwoPanels) {
                        str = str + " or screen = 1";
                    }
                    loadWorkspace(new ArrayList(), LauncherSettings.Favorites.PREVIEW_CONTENT_URI, str);
                    Executors.MAIN_EXECUTOR.execute(new Runnable(previewContext) {
                        public final /* synthetic */ LauncherPreviewRenderer.PreviewContext f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            PreviewSurfaceRenderer.AnonymousClass1.this.lambda$run$0$PreviewSurfaceRenderer$1(this.f$1);
                        }
                    });
                }

                public /* synthetic */ void lambda$run$0$PreviewSurfaceRenderer$1(LauncherPreviewRenderer.PreviewContext previewContext) {
                    PreviewSurfaceRenderer.this.renderView(previewContext, this.mBgDataModel, this.mWidgetProvidersMap);
                    RunnableList access$200 = PreviewSurfaceRenderer.this.mOnDestroyCallbacks;
                    Objects.requireNonNull(previewContext);
                    access$200.add(new Runnable() {
                        public final void run() {
                            LauncherPreviewRenderer.PreviewContext.this.onDestroy();
                        }
                    });
                }
            }.run();
            return;
        }
        LauncherAppState.getInstance(contextThemeWrapper).getModel().loadAsync(new Consumer(contextThemeWrapper) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                PreviewSurfaceRenderer.this.lambda$loadModelData$2$PreviewSurfaceRenderer(this.f$1, (BgDataModel) obj);
            }
        });
    }

    public /* synthetic */ void lambda$loadModelData$1$PreviewSurfaceRenderer(Context context, BgDataModel bgDataModel) {
        renderView(context, bgDataModel, (Map<ComponentKey, AppWidgetProviderInfo>) null);
    }

    public /* synthetic */ void lambda$loadModelData$2$PreviewSurfaceRenderer(Context context, BgDataModel bgDataModel) {
        if (bgDataModel != null) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(context, bgDataModel) {
                public final /* synthetic */ Context f$1;
                public final /* synthetic */ BgDataModel f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PreviewSurfaceRenderer.this.lambda$loadModelData$1$PreviewSurfaceRenderer(this.f$1, this.f$2);
                }
            });
        } else {
            Log.e(TAG, "Model loading failed");
        }
    }

    private boolean doGridMigrationIfNecessary() {
        if (!GridSizeMigrationTaskV2.needsToMigrate(this.mContext, this.mIdp)) {
            return false;
        }
        return GridSizeMigrationTaskV2.migrateGridIfNeeded(this.mContext, this.mIdp);
    }

    /* access modifiers changed from: private */
    public void renderView(Context context, BgDataModel bgDataModel, Map<ComponentKey, AppWidgetProviderInfo> map) {
        if (!this.mDestroyed) {
            View renderedView = new LauncherPreviewRenderer(context, this.mIdp, this.mWallpaperColors).getRenderedView(bgDataModel, map);
            float min = Math.min(((float) this.mWidth) / ((float) renderedView.getMeasuredWidth()), ((float) this.mHeight) / ((float) renderedView.getMeasuredHeight()));
            renderedView.setScaleX(min);
            renderedView.setScaleY(min);
            renderedView.setPivotX(0.0f);
            renderedView.setPivotY(0.0f);
            renderedView.setTranslationX((((float) this.mWidth) - (((float) renderedView.getWidth()) * min)) / 2.0f);
            renderedView.setTranslationY((((float) this.mHeight) - (min * ((float) renderedView.getHeight()))) / 2.0f);
            renderedView.setAlpha(0.0f);
            renderedView.animate().alpha(1.0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).start();
            this.mSurfaceControlViewHost.setView(renderedView, renderedView.getMeasuredWidth(), renderedView.getMeasuredHeight());
        }
    }
}
