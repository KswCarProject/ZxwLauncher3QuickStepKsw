package com.android.wm.shell.util;

import android.graphics.Point;
import android.util.RotationUtils;
import android.view.SurfaceControl;

public class CounterRotator {
    private SurfaceControl mSurface = null;

    public SurfaceControl getSurface() {
        return this.mSurface;
    }

    public void setup(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, int i, float f, float f2) {
        if (i != 0) {
            SurfaceControl build = new SurfaceControl.Builder().setName("Transition Unrotate").setContainerLayer().setParent(surfaceControl).build();
            this.mSurface = build;
            RotationUtils.rotateSurface(transaction, build, i);
            Point point = new Point(0, 0);
            if (i % 2 != 0) {
                float f3 = f2;
                f2 = f;
                f = f3;
            }
            RotationUtils.rotatePoint(point, i, (int) f, (int) f2);
            transaction.setPosition(this.mSurface, (float) point.x, (float) point.y);
            transaction.show(this.mSurface);
        }
    }

    public void addChild(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        SurfaceControl surfaceControl2 = this.mSurface;
        if (surfaceControl2 != null) {
            transaction.reparent(surfaceControl, surfaceControl2);
        }
    }

    public void cleanUp(SurfaceControl.Transaction transaction) {
        SurfaceControl surfaceControl = this.mSurface;
        if (surfaceControl != null) {
            transaction.remove(surfaceControl);
        }
    }
}
