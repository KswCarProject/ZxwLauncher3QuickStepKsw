package com.android.launcher3.util;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Xml;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.pm.UserCache;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.LongFunction;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PersistedItemArray<T extends ItemInfo> {
    private static final String TAG = "PersistedItemArray";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_ROOT = "items";
    private final String mFileName;

    public interface ItemFactory<T extends ItemInfo> {
        T createInfo(int i, UserHandle userHandle, Intent intent);
    }

    public PersistedItemArray(String str) {
        this.mFileName = str + ".xml";
    }

    public void write(Context context, List<T> list) {
        AtomicFile file = getFile(context);
        try {
            FileOutputStream startWrite = file.startWrite();
            UserCache userCache = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
            try {
                XmlSerializer newSerializer = Xml.newSerializer();
                newSerializer.setOutput(startWrite, StandardCharsets.UTF_8.name());
                newSerializer.startDocument((String) null, true);
                newSerializer.startTag((String) null, TAG_ROOT);
                for (T t : list) {
                    Intent intent = t.getIntent();
                    if (intent != null) {
                        newSerializer.startTag((String) null, TAG_ENTRY);
                        newSerializer.attribute((String) null, LauncherSettings.Favorites.ITEM_TYPE, Integer.toString(t.itemType));
                        newSerializer.attribute((String) null, "profileId", Long.toString(userCache.getSerialNumberForUser(t.user)));
                        newSerializer.attribute((String) null, LauncherSettings.Favorites.INTENT, intent.toUri(0));
                        newSerializer.endTag((String) null, TAG_ENTRY);
                    }
                }
                newSerializer.endTag((String) null, TAG_ROOT);
                newSerializer.endDocument();
                file.finishWrite(startWrite);
            } catch (IOException e) {
                file.failWrite(startWrite);
                Log.e(TAG, "Unable to persist items in " + this.mFileName, e);
            }
        } catch (IOException e2) {
            Log.e(TAG, "Unable to persist items in " + this.mFileName, e2);
        }
    }

    public List<T> read(Context context, ItemFactory<T> itemFactory) {
        UserCache userCache = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        Objects.requireNonNull(userCache);
        return read(context, itemFactory, new LongFunction() {
            public final Object apply(long j) {
                return UserCache.this.getUserForSerialNumber(j);
            }
        });
    }

    public List<T> read(Context context, ItemFactory<T> itemFactory, LongFunction<UserHandle> longFunction) {
        FileInputStream openRead;
        T createInfo;
        ArrayList arrayList = new ArrayList();
        try {
            openRead = getFile(context).openRead();
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(new InputStreamReader(openRead, StandardCharsets.UTF_8));
            AutoInstallsLayout.beginDocument(newPullParser, TAG_ROOT);
            int depth = newPullParser.getDepth();
            while (true) {
                int next = newPullParser.next();
                if ((next != 3 || newPullParser.getDepth() > depth) && next != 1) {
                    if (next == 2 && TAG_ENTRY.equals(newPullParser.getName())) {
                        try {
                            int parseInt = Integer.parseInt(newPullParser.getAttributeValue((String) null, LauncherSettings.Favorites.ITEM_TYPE));
                            UserHandle apply = longFunction.apply(Long.parseLong(newPullParser.getAttributeValue((String) null, "profileId")));
                            Intent parseUri = Intent.parseUri(newPullParser.getAttributeValue((String) null, LauncherSettings.Favorites.INTENT), 0);
                            if (!(apply == null || parseUri == null || (createInfo = itemFactory.createInfo(parseInt, apply, parseUri)) == null)) {
                                arrayList.add(createInfo);
                            }
                        } catch (Exception unused) {
                        }
                    }
                }
            }
            if (openRead != null) {
                openRead.close();
            }
        } catch (FileNotFoundException unused2) {
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Unable to read items in " + this.mFileName, e);
            return Collections.emptyList();
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        return arrayList;
        throw th;
    }

    public AtomicFile getFile(Context context) {
        return new AtomicFile(context.getFileStreamPath(this.mFileName));
    }
}
