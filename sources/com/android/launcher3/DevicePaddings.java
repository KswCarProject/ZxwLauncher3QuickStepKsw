package com.android.launcher3;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;

public class DevicePaddings {
    private static final boolean DEBUG = false;
    private static final String DEVICE_PADDING = "device-padding";
    private static final String DEVICE_PADDINGS = "device-paddings";
    private static final String HOTSEAT_BOTTOM_PADDING = "hotseatBottomPadding";
    private static final String TAG = "DevicePaddings";
    private static final String WORKSPACE_BOTTOM_PADDING = "workspaceBottomPadding";
    private static final String WORKSPACE_TOP_PADDING = "workspaceTopPadding";
    ArrayList<DevicePadding> mDevicePaddings = new ArrayList<>();

    public DevicePaddings(Context context, int i) {
        XmlResourceParser xml;
        try {
            xml = context.getResources().getXml(i);
            int depth = xml.getDepth();
            while (true) {
                int next = xml.next();
                if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                    if (next == 2 && DEVICE_PADDINGS.equals(xml.getName())) {
                        int depth2 = xml.getDepth();
                        while (true) {
                            int next2 = xml.next();
                            if ((next2 == 3 && xml.getDepth() <= depth2) || next2 == 1) {
                                break;
                            } else if (next2 == 2 && DEVICE_PADDING.equals(xml.getName())) {
                                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xml), R.styleable.DevicePadding);
                                int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, 0);
                                obtainStyledAttributes.recycle();
                                int depth3 = xml.getDepth();
                                PaddingFormula paddingFormula = null;
                                PaddingFormula paddingFormula2 = null;
                                PaddingFormula paddingFormula3 = null;
                                while (true) {
                                    int next3 = xml.next();
                                    if ((next3 != 3 || xml.getDepth() > depth3) && next3 != 1) {
                                        AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
                                        if (next3 == 2) {
                                            if (WORKSPACE_TOP_PADDING.equals(xml.getName())) {
                                                paddingFormula = new PaddingFormula(context, asAttributeSet);
                                            } else if (WORKSPACE_BOTTOM_PADDING.equals(xml.getName())) {
                                                paddingFormula2 = new PaddingFormula(context, asAttributeSet);
                                            } else if (HOTSEAT_BOTTOM_PADDING.equals(xml.getName())) {
                                                paddingFormula3 = new PaddingFormula(context, asAttributeSet);
                                            }
                                        }
                                    }
                                }
                                if ((paddingFormula == null || paddingFormula2 == null || paddingFormula3 == null) && Utilities.IS_DEBUG_DEVICE) {
                                    throw new RuntimeException("DevicePadding missing padding.");
                                }
                                DevicePadding devicePadding = new DevicePadding(dimensionPixelSize, paddingFormula, paddingFormula2, paddingFormula3);
                                if (devicePadding.isValid()) {
                                    this.mDevicePaddings.add(devicePadding);
                                } else {
                                    Log.e(TAG, "Invalid device padding found.");
                                    if (Utilities.IS_DEBUG_DEVICE) {
                                        throw new RuntimeException("DevicePadding is invalid");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (xml != null) {
                xml.close();
            }
            this.mDevicePaddings.sort($$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo.INSTANCE);
            return;
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Failure parsing device padding layout.", e);
            throw new RuntimeException(e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public DevicePadding getDevicePadding(int i) {
        Iterator<DevicePadding> it = this.mDevicePaddings.iterator();
        while (it.hasNext()) {
            DevicePadding next = it.next();
            if (i <= next.maxEmptySpacePx) {
                return next;
            }
        }
        ArrayList<DevicePadding> arrayList = this.mDevicePaddings;
        return arrayList.get(arrayList.size() - 1);
    }

    public static final class DevicePadding {
        private static final int ROUNDING_THRESHOLD_PX = 3;
        private final PaddingFormula hotseatBottomPadding;
        /* access modifiers changed from: private */
        public final int maxEmptySpacePx;
        private final PaddingFormula workspaceBottomPadding;
        private final PaddingFormula workspaceTopPadding;

        public DevicePadding(int i, PaddingFormula paddingFormula, PaddingFormula paddingFormula2, PaddingFormula paddingFormula3) {
            this.maxEmptySpacePx = i;
            this.workspaceTopPadding = paddingFormula;
            this.workspaceBottomPadding = paddingFormula2;
            this.hotseatBottomPadding = paddingFormula3;
        }

        public int getMaxEmptySpacePx() {
            return this.maxEmptySpacePx;
        }

        public int getWorkspaceTopPadding(int i) {
            return this.workspaceTopPadding.calculate(i);
        }

        public int getWorkspaceBottomPadding(int i) {
            return this.workspaceBottomPadding.calculate(i);
        }

        public int getHotseatBottomPadding(int i) {
            return this.hotseatBottomPadding.calculate(i);
        }

        public boolean isValid() {
            return Math.abs(((getWorkspaceTopPadding(this.maxEmptySpacePx) + getWorkspaceBottomPadding(this.maxEmptySpacePx)) + getHotseatBottomPadding(this.maxEmptySpacePx)) - this.maxEmptySpacePx) <= 3;
        }
    }

    private static final class PaddingFormula {
        private final float a;
        private final float b;
        private final float c;

        public PaddingFormula(Context context, AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DevicePaddingFormula);
            this.a = getValue(obtainStyledAttributes, 0);
            this.b = getValue(obtainStyledAttributes, 1);
            this.c = getValue(obtainStyledAttributes, 2);
            obtainStyledAttributes.recycle();
        }

        public int calculate(int i) {
            return Math.round((this.a * (((float) i) - this.c)) + this.b);
        }

        private static float getValue(TypedArray typedArray, int i) {
            if (typedArray.getType(i) == 5) {
                return (float) typedArray.getDimensionPixelSize(i, 0);
            }
            if (typedArray.getType(i) == 4) {
                return typedArray.getFloat(i, 0.0f);
            }
            return 0.0f;
        }

        public String toString() {
            return "a=" + this.a + ", b=" + this.b + ", c=" + this.c;
        }
    }
}
