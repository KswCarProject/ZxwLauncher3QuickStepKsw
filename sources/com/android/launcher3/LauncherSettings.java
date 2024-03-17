package com.android.launcher3;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;

public class LauncherSettings {

    public static final class Favorites implements BaseColumns {
        public static final String APPWIDGET_ID = "appWidgetId";
        public static final String APPWIDGET_PROVIDER = "appWidgetProvider";
        public static final String APPWIDGET_SOURCE = "appWidgetSource";
        public static final Uri BACKUP_CONTENT_URI = Uri.parse("content://com.android.launcher3.settings/favorites_bakup");
        public static final String BACKUP_TABLE_NAME = "favorites_bakup";
        public static final String CELLX = "cellX";
        public static final String CELLY = "cellY";
        public static final String CONTAINER = "container";
        public static final int CONTAINER_ALL_APPS = -104;
        public static final int CONTAINER_BOTTOM_WIDGETS_TRAY = -112;
        public static final int CONTAINER_DESKTOP = -100;
        public static final int CONTAINER_HOTSEAT = -101;
        public static final int CONTAINER_HOTSEAT_PREDICTION = -103;
        public static final int CONTAINER_PIN_WIDGETS = -113;
        public static final int CONTAINER_PREDICTION = -102;
        public static final int CONTAINER_QSB = -110;
        public static final int CONTAINER_SEARCH_RESULTS = -106;
        public static final int CONTAINER_SETTINGS = -108;
        public static final int CONTAINER_SHORTCUTS = -107;
        public static final int CONTAINER_TASKSWITCHER = -109;
        public static final int CONTAINER_UNKNOWN = -1;
        public static final int CONTAINER_WALLPAPERS = -114;
        public static final int CONTAINER_WIDGETS_PREDICTION = -111;
        public static final int CONTAINER_WIDGETS_TRAY = -105;
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.launcher3.settings/favorites");
        public static final int EXTENDED_CONTAINERS = -200;
        public static final String HYBRID_HOTSEAT_BACKUP_TABLE = "hotseat_restore_backup";
        public static final String ICON = "icon";
        public static final String ICON_PACKAGE = "iconPackage";
        public static final String ICON_RESOURCE = "iconResource";
        public static final String INTENT = "intent";
        public static final String ITEM_TYPE = "itemType";
        public static final int ITEM_TYPE_APPLICATION = 0;
        public static final int ITEM_TYPE_APPWIDGET = 4;
        public static final int ITEM_TYPE_CUSTOM_APPWIDGET = 5;
        public static final int ITEM_TYPE_DEEP_SHORTCUT = 6;
        public static final int ITEM_TYPE_FOLDER = 2;
        public static final int ITEM_TYPE_NON_ACTIONABLE = -1;
        public static final int ITEM_TYPE_QSB = 8;
        public static final int ITEM_TYPE_SEARCH_ACTION = 7;
        public static final int ITEM_TYPE_SHORTCUT = 1;
        public static final int ITEM_TYPE_TASK = 7;
        public static final String MODIFIED = "modified";
        public static final String OPTIONS = "options";
        public static final Uri PREVIEW_CONTENT_URI = Uri.parse("content://com.android.launcher3.settings/favorites_preview");
        public static final String PREVIEW_TABLE_NAME = "favorites_preview";
        public static final String PROFILE_ID = "profileId";
        public static final String RANK = "rank";
        public static final String RESTORED = "restored";
        public static final String SCREEN = "screen";
        public static final String SPANX = "spanX";
        public static final String SPANY = "spanY";
        public static final String TABLE_NAME = "favorites";
        public static final String TITLE = "title";
        public static final Uri TMP_CONTENT_URI = Uri.parse("content://com.android.launcher3.settings/favorites_tmp");
        public static final String TMP_TABLE = "favorites_tmp";

        public static Uri getContentUri(int i) {
            return Uri.parse("content://com.android.launcher3.settings/favorites/" + i);
        }

        public static final String containerToString(int i) {
            switch (i) {
                case CONTAINER_SHORTCUTS /*-107*/:
                    return "shortcuts";
                case CONTAINER_SEARCH_RESULTS /*-106*/:
                    return "search_result";
                case CONTAINER_WIDGETS_TRAY /*-105*/:
                    return "widgets_tray";
                case CONTAINER_ALL_APPS /*-104*/:
                    return "all_apps";
                case CONTAINER_PREDICTION /*-102*/:
                    return "prediction";
                case CONTAINER_HOTSEAT /*-101*/:
                    return "hotseat";
                case -100:
                    return "desktop";
                default:
                    return String.valueOf(i);
            }
        }

        public static final String itemTypeToString(int i) {
            switch (i) {
                case 0:
                    return "APP";
                case 1:
                    return "SHORTCUT";
                case 2:
                    return "FOLDER";
                case 4:
                    return "WIDGET";
                case 5:
                    return "CUSTOMWIDGET";
                case 6:
                    return "DEEPSHORTCUT";
                case 7:
                    return "TASK";
                case 8:
                    return "QSB";
                default:
                    return String.valueOf(i);
            }
        }

        public static void addTableToDb(SQLiteDatabase sQLiteDatabase, long j, boolean z) {
            addTableToDb(sQLiteDatabase, j, z, TABLE_NAME);
        }

        public static void addTableToDb(SQLiteDatabase sQLiteDatabase, long j, boolean z, String str) {
            sQLiteDatabase.execSQL("CREATE TABLE " + (z ? " IF NOT EXISTS " : "") + str + " (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,screen INTEGER,cellX INTEGER,cellY INTEGER,spanX INTEGER,spanY INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,iconPackage TEXT,iconResource TEXT,icon BLOB,appWidgetProvider TEXT,modified INTEGER NOT NULL DEFAULT 0,restored INTEGER NOT NULL DEFAULT 0,profileId INTEGER DEFAULT " + j + ",rank INTEGER NOT NULL DEFAULT 0,options INTEGER NOT NULL DEFAULT 0," + APPWIDGET_SOURCE + " INTEGER NOT NULL DEFAULT " + -1 + ");");
        }
    }

    public static final class Settings {
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.launcher3.settings/settings");
        public static final String EXTRA_DB_NAME = "db_name";
        public static final String EXTRA_VALUE = "value";
        public static final String METHOD_CLEAR_EMPTY_DB_FLAG = "clear_empty_db_flag";
        public static final String METHOD_CLEAR_USE_TEST_WORKSPACE_LAYOUT_FLAG = "clear_use_test_workspace_layout_flag";
        public static final String METHOD_CREATE_EMPTY_DB = "create_empty_db";
        public static final String METHOD_DELETE_EMPTY_FOLDERS = "delete_empty_folders";
        public static final String METHOD_LOAD_DEFAULT_FAVORITES = "load_default_favorites";
        public static final String METHOD_NEW_ITEM_ID = "generate_new_item_id";
        public static final String METHOD_NEW_SCREEN_ID = "generate_new_screen_id";
        public static final String METHOD_NEW_TRANSACTION = "new_db_transaction";
        public static final String METHOD_PREP_FOR_PREVIEW = "prep_for_preview";
        public static final String METHOD_REFRESH_BACKUP_TABLE = "refresh_backup_table";
        public static final String METHOD_REFRESH_HOTSEAT_RESTORE_TABLE = "restore_hotseat_table";
        public static final String METHOD_REMOVE_GHOST_WIDGETS = "remove_ghost_widgets";
        public static final String METHOD_RESTORE_BACKUP_TABLE = "restore_backup_table";
        public static final String METHOD_SET_USE_TEST_WORKSPACE_LAYOUT_FLAG = "set_use_test_workspace_layout_flag";
        public static final String METHOD_SWITCH_DATABASE = "switch_database";
        public static final String METHOD_UPDATE_CURRENT_OPEN_HELPER = "update_current_open_helper";
        public static final String METHOD_WAS_EMPTY_DB_CREATED = "get_empty_db_flag";

        public static Bundle call(ContentResolver contentResolver, String str) {
            return call(contentResolver, str, (String) null);
        }

        public static Bundle call(ContentResolver contentResolver, String str, String str2) {
            return call(contentResolver, str, str2, (Bundle) null);
        }

        public static Bundle call(ContentResolver contentResolver, String str, String str2, Bundle bundle) {
            return contentResolver.call(CONTENT_URI, str, str2, bundle);
        }
    }
}
