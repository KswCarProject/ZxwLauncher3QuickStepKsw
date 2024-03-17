package androidx.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.InflateException;
import com.android.launcher3.icons.cache.BaseIconCache;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater {
    private static final HashMap<String, Constructor<?>> CONSTRUCTOR_MAP = new HashMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;

    public PreferenceInflater(Context context, PreferenceManager preferenceManager) {
        this.mContext = context;
        init(preferenceManager);
    }

    private void init(PreferenceManager preferenceManager) {
        this.mPreferenceManager = preferenceManager;
        setDefaultPackages(new String[]{Preference.class.getPackage().getName() + BaseIconCache.EMPTY_CLASS_NAME, SwitchPreference.class.getPackage().getName() + BaseIconCache.EMPTY_CLASS_NAME});
    }

    public void setDefaultPackages(String[] strArr) {
        this.mDefaultPackages = strArr;
    }

    public String[] getDefaultPackages() {
        return this.mDefaultPackages;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Preference inflate(int i, PreferenceGroup preferenceGroup) {
        XmlResourceParser xml = getContext().getResources().getXml(i);
        try {
            return inflate((XmlPullParser) xml, preferenceGroup);
        } finally {
            xml.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x001a A[Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x002d A[SYNTHETIC, Splitter:B:14:0x002d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.preference.Preference inflate(org.xmlpull.v1.XmlPullParser r6, androidx.preference.PreferenceGroup r7) {
        /*
            r5 = this;
            java.lang.Object[] r0 = r5.mConstructorArgs
            monitor-enter(r0)
            android.util.AttributeSet r1 = android.util.Xml.asAttributeSet(r6)     // Catch:{ all -> 0x0083 }
            java.lang.Object[] r2 = r5.mConstructorArgs     // Catch:{ all -> 0x0083 }
            r3 = 0
            android.content.Context r4 = r5.mContext     // Catch:{ all -> 0x0083 }
            r2[r3] = r4     // Catch:{ all -> 0x0083 }
        L_0x000e:
            int r2 = r6.next()     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            r3 = 2
            if (r2 == r3) goto L_0x0018
            r4 = 1
            if (r2 != r4) goto L_0x000e
        L_0x0018:
            if (r2 != r3) goto L_0x002d
            java.lang.String r2 = r6.getName()     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            androidx.preference.Preference r2 = r5.createItemFromTag(r2, r1)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            androidx.preference.PreferenceGroup r2 = (androidx.preference.PreferenceGroup) r2     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            androidx.preference.PreferenceGroup r7 = r5.onMergeRoots(r7, r2)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            r5.rInflate(r6, r7, r1)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            monitor-exit(r0)     // Catch:{ all -> 0x0083 }
            return r7
        L_0x002d:
            android.view.InflateException r7 = new android.view.InflateException     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            r1.<init>()     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            java.lang.String r2 = r6.getPositionDescription()     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            java.lang.String r2 = ": No start tag found!"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            java.lang.String r1 = r1.toString()     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            r7.<init>(r1)     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
            throw r7     // Catch:{ InflateException -> 0x0081, XmlPullParserException -> 0x0073, IOException -> 0x004a }
        L_0x004a:
            r7 = move-exception
            android.view.InflateException r1 = new android.view.InflateException     // Catch:{ all -> 0x0083 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0083 }
            r2.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r6 = r6.getPositionDescription()     // Catch:{ all -> 0x0083 }
            java.lang.StringBuilder r6 = r2.append(r6)     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = ": "
            java.lang.StringBuilder r6 = r6.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = r7.getMessage()     // Catch:{ all -> 0x0083 }
            java.lang.StringBuilder r6 = r6.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0083 }
            r1.<init>(r6)     // Catch:{ all -> 0x0083 }
            r1.initCause(r7)     // Catch:{ all -> 0x0083 }
            throw r1     // Catch:{ all -> 0x0083 }
        L_0x0073:
            r6 = move-exception
            android.view.InflateException r7 = new android.view.InflateException     // Catch:{ all -> 0x0083 }
            java.lang.String r1 = r6.getMessage()     // Catch:{ all -> 0x0083 }
            r7.<init>(r1)     // Catch:{ all -> 0x0083 }
            r7.initCause(r6)     // Catch:{ all -> 0x0083 }
            throw r7     // Catch:{ all -> 0x0083 }
        L_0x0081:
            r6 = move-exception
            throw r6     // Catch:{ all -> 0x0083 }
        L_0x0083:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0083 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.preference.PreferenceInflater.inflate(org.xmlpull.v1.XmlPullParser, androidx.preference.PreferenceGroup):androidx.preference.Preference");
    }

    private PreferenceGroup onMergeRoots(PreferenceGroup preferenceGroup, PreferenceGroup preferenceGroup2) {
        if (preferenceGroup != null) {
            return preferenceGroup;
        }
        preferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
        return preferenceGroup2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007d, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007e, code lost:
        r0 = new android.view.InflateException(r13.getPositionDescription() + ": Error inflating class " + r11);
        r0.initCause(r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009f, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a0, code lost:
        r11 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a1, code lost:
        throw r11;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007d A[ExcHandler: Exception (r12v4 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:2:0x000d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private androidx.preference.Preference createItem(java.lang.String r11, java.lang.String[] r12, android.util.AttributeSet r13) throws java.lang.ClassNotFoundException, android.view.InflateException {
        /*
            r10 = this;
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor<?>> r0 = CONSTRUCTOR_MAP
            java.lang.Object r0 = r0.get(r11)
            java.lang.reflect.Constructor r0 = (java.lang.reflect.Constructor) r0
            java.lang.String r1 = ": Error inflating class "
            r2 = 1
            if (r0 != 0) goto L_0x0072
            android.content.Context r0 = r10.mContext     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.ClassLoader r0 = r0.getClassLoader()     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r3 = 0
            if (r12 == 0) goto L_0x0060
            int r4 = r12.length     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            if (r4 != 0) goto L_0x001a
            goto L_0x0060
        L_0x001a:
            int r4 = r12.length     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r5 = 0
            r6 = r3
            r7 = r5
        L_0x001e:
            if (r6 >= r4) goto L_0x003c
            r8 = r12[r6]     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            r9.<init>()     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            java.lang.StringBuilder r8 = r9.append(r8)     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            java.lang.StringBuilder r8 = r8.append(r11)     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            java.lang.String r8 = r8.toString()     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            java.lang.Class r5 = java.lang.Class.forName(r8, r3, r0)     // Catch:{ ClassNotFoundException -> 0x0038, Exception -> 0x007d }
            goto L_0x003c
        L_0x0038:
            r7 = move-exception
            int r6 = r6 + 1
            goto L_0x001e
        L_0x003c:
            if (r5 != 0) goto L_0x0064
            if (r7 != 0) goto L_0x005f
            android.view.InflateException r12 = new android.view.InflateException     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r0.<init>()     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.String r2 = r13.getPositionDescription()     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.StringBuilder r0 = r0.append(r11)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.String r0 = r0.toString()     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r12.<init>(r0)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            throw r12     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
        L_0x005f:
            throw r7     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
        L_0x0060:
            java.lang.Class r5 = java.lang.Class.forName(r11, r3, r0)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
        L_0x0064:
            java.lang.Class<?>[] r12 = CONSTRUCTOR_SIGNATURE     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.reflect.Constructor r0 = r5.getConstructor(r12)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r0.setAccessible(r2)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor<?>> r12 = CONSTRUCTOR_MAP     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r12.put(r11, r0)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
        L_0x0072:
            java.lang.Object[] r12 = r10.mConstructorArgs     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            r12[r2] = r13     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            java.lang.Object r12 = r0.newInstance(r12)     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            androidx.preference.Preference r12 = (androidx.preference.Preference) r12     // Catch:{ ClassNotFoundException -> 0x00a0, Exception -> 0x007d }
            return r12
        L_0x007d:
            r12 = move-exception
            android.view.InflateException r0 = new android.view.InflateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r13 = r13.getPositionDescription()
            java.lang.StringBuilder r13 = r2.append(r13)
            java.lang.StringBuilder r13 = r13.append(r1)
            java.lang.StringBuilder r11 = r13.append(r11)
            java.lang.String r11 = r11.toString()
            r0.<init>(r11)
            r0.initCause(r12)
            throw r0
        L_0x00a0:
            r11 = move-exception
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.preference.PreferenceInflater.createItem(java.lang.String, java.lang.String[], android.util.AttributeSet):androidx.preference.Preference");
    }

    /* access modifiers changed from: protected */
    public Preference onCreateItem(String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return createItem(str, this.mDefaultPackages, attributeSet);
    }

    private Preference createItemFromTag(String str, AttributeSet attributeSet) {
        try {
            if (-1 == str.indexOf(46)) {
                return onCreateItem(str, attributeSet);
            }
            return createItem(str, (String[]) null, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException inflateException = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class (not found)" + str);
            inflateException.initCause(e2);
            throw inflateException;
        } catch (Exception e3) {
            InflateException inflateException2 = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
            inflateException2.initCause(e3);
            throw inflateException2;
        }
    }

    private void rInflate(XmlPullParser xmlPullParser, Preference preference, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if ("intent".equals(name)) {
                    try {
                        preference.setIntent(Intent.parseIntent(getContext().getResources(), xmlPullParser, attributeSet));
                    } catch (IOException e) {
                        XmlPullParserException xmlPullParserException = new XmlPullParserException("Error parsing preference");
                        xmlPullParserException.initCause(e);
                        throw xmlPullParserException;
                    }
                } else if (EXTRA_TAG_NAME.equals(name)) {
                    getContext().getResources().parseBundleExtra(EXTRA_TAG_NAME, attributeSet, preference.getExtras());
                    try {
                        skipCurrentTag(xmlPullParser);
                    } catch (IOException e2) {
                        XmlPullParserException xmlPullParserException2 = new XmlPullParserException("Error parsing preference");
                        xmlPullParserException2.initCause(e2);
                        throw xmlPullParserException2;
                    }
                } else {
                    Preference createItemFromTag = createItemFromTag(name, attributeSet);
                    ((PreferenceGroup) preference).addItemFromInflater(createItemFromTag);
                    rInflate(xmlPullParser, createItemFromTag, attributeSet);
                }
            }
        }
    }

    private static void skipCurrentTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
        }
    }
}
