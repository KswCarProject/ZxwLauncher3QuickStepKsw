package com.android.launcher3.dragndrop;

import android.os.SystemClock;
import android.view.DragEvent;
import android.view.MotionEvent;
import java.util.function.Consumer;

public abstract class DragDriver {
    protected final EventListener mEventListener;
    protected final Consumer<MotionEvent> mSecondaryEventConsumer;

    public interface EventListener {
        void onDriverDragCancel();

        void onDriverDragEnd(float f, float f2);

        void onDriverDragExitWindow();

        void onDriverDragMove(float f, float f2);
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public DragDriver(EventListener eventListener, Consumer<MotionEvent> consumer) {
        this.mEventListener = eventListener;
        this.mSecondaryEventConsumer = consumer;
    }

    public static DragDriver create(DragController dragController, DragOptions dragOptions, Consumer<MotionEvent> consumer) {
        if (dragOptions.simulatedDndStartPoint == null) {
            return new InternalDragDriver(dragController, consumer);
        }
        if (dragOptions.isAccessibleDrag) {
            return null;
        }
        return new SystemDragDriver(dragController, consumer);
    }

    static class SystemDragDriver extends DragDriver {
        private final long mDragStartTime = SystemClock.uptimeMillis();
        float mLastX = 0.0f;
        float mLastY = 0.0f;

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        SystemDragDriver(DragController dragController, Consumer<MotionEvent> consumer) {
            super(dragController, consumer);
        }

        private void simulateSecondaryMotionEvent(DragEvent dragEvent) {
            int i;
            int action = dragEvent.getAction();
            int i2 = 2;
            if (action == 1) {
                i2 = 0;
            } else if (action != 2) {
                if (action == 4) {
                    i = 1;
                    MotionEvent obtain = MotionEvent.obtain(this.mDragStartTime, SystemClock.uptimeMillis(), i, dragEvent.getX(), dragEvent.getY(), 0);
                    this.mSecondaryEventConsumer.accept(obtain);
                    obtain.recycle();
                }
                return;
            }
            i = i2;
            MotionEvent obtain2 = MotionEvent.obtain(this.mDragStartTime, SystemClock.uptimeMillis(), i, dragEvent.getX(), dragEvent.getY(), 0);
            this.mSecondaryEventConsumer.accept(obtain2);
            obtain2.recycle();
        }

        public boolean onDragEvent(DragEvent dragEvent) {
            simulateSecondaryMotionEvent(dragEvent);
            switch (dragEvent.getAction()) {
                case 1:
                    this.mLastX = dragEvent.getX();
                    this.mLastY = dragEvent.getY();
                    return true;
                case 2:
                    this.mLastX = dragEvent.getX();
                    this.mLastY = dragEvent.getY();
                    this.mEventListener.onDriverDragMove(dragEvent.getX(), dragEvent.getY());
                    return true;
                case 3:
                    this.mLastX = dragEvent.getX();
                    this.mLastY = dragEvent.getY();
                    this.mEventListener.onDriverDragMove(dragEvent.getX(), dragEvent.getY());
                    this.mEventListener.onDriverDragEnd(this.mLastX, this.mLastY);
                    return true;
                case 4:
                    this.mEventListener.onDriverDragCancel();
                    return true;
                case 5:
                    break;
                case 6:
                    this.mEventListener.onDriverDragExitWindow();
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    static class InternalDragDriver extends DragDriver {
        private final DragController mDragController;

        InternalDragDriver(DragController dragController, Consumer<MotionEvent> consumer) {
            super(dragController, consumer);
            this.mDragController = dragController;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            this.mSecondaryEventConsumer.accept(motionEvent);
            int action = motionEvent.getAction();
            if (action == 1) {
                this.mEventListener.onDriverDragMove(this.mDragController.getX(motionEvent), this.mDragController.getY(motionEvent));
                this.mEventListener.onDriverDragEnd(this.mDragController.getX(motionEvent), this.mDragController.getY(motionEvent));
            } else if (action == 2) {
                this.mEventListener.onDriverDragMove(this.mDragController.getX(motionEvent), this.mDragController.getY(motionEvent));
            } else if (action == 3) {
                this.mEventListener.onDriverDragCancel();
            }
            return true;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            this.mSecondaryEventConsumer.accept(motionEvent);
            int action = motionEvent.getAction();
            if (action == 1) {
                this.mEventListener.onDriverDragEnd(this.mDragController.getX(motionEvent), this.mDragController.getY(motionEvent));
            } else if (action == 3) {
                this.mEventListener.onDriverDragCancel();
            }
            return true;
        }
    }
}
