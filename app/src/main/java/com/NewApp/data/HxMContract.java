package com.NewApp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Peter on 04/08/2015.
 */
public class HxMContract {

    public static final String CONTENT_AUTHORITY = "com.NewApp";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_HXM = "hxm";


    public static final class HxMEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HXM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HXM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HXM;

        public static final String TABLE_NAME = "hxm";

        public static final String COLUMN_id = "id";
        public static final String COLUMN_HeartRate = "HeartRate";
        public static final String COLUMN_InstantSpeed = "InstantSpeed";
        public static final String COLUMN_posted = "posted";

        public static Uri buildHxmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
