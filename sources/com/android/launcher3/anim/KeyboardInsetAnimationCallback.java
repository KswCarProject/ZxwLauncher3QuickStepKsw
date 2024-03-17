package com.android.launcher3.anim;

import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import com.android.launcher3.Utilities;
import java.util.List;

public class KeyboardInsetAnimationCallback extends WindowInsetsAnimation.Callback {
    private float mInitialTranslation;
    private float mTerminalTranslation;
    private final View mView;

    public interface KeyboardInsetListener {
        void onTranslationEnd();

        void onTranslationStart();
    }

    public KeyboardInsetAnimationCallback(View view) {
        super(0);
        this.mView = view;
    }

    public void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
        this.mInitialTranslation = this.mView.getTranslationY();
    }

    public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
        if (list.size() == 0) {
            this.mView.setTranslationY(this.mInitialTranslation);
            return windowInsets;
        }
        this.mView.setTranslationY(Utilities.mapRange(list.get(0).getInterpolatedFraction(), this.mInitialTranslation, this.mTerminalTranslation));
        return windowInsets;
    }

    public WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
        this.mTerminalTranslation = this.mView.getTranslationY();
        View view = this.mView;
        if (view instanceof KeyboardInsetListener) {
            ((KeyboardInsetListener) view).onTranslationStart();
        }
        return KeyboardInsetAnimationCallback.super.onStart(windowInsetsAnimation, bounds);
    }

    public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
        View view = this.mView;
        if (view instanceof KeyboardInsetListener) {
            ((KeyboardInsetListener) view).onTranslationEnd();
        }
        KeyboardInsetAnimationCallback.super.onEnd(windowInsetsAnimation);
    }
}
