package com.NewApp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Peter on 07/08/2015.
 */
public class HxMProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMathcer();
    private HxMDbHelper mOpenHelper;

    static final int HXM = 100;
    static final int HXM_WITH_ID = 101;

    private static final SQLiteQueryBuilder sQueryBuilder;
    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(HxMContract.HxMEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMathcer() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HxMContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, HxMContract.PATH_HXM, HXM);
        matcher.addURI(authority, HxMContract.PATH_HXM + "/*", HXM_WITH_ID);
        return matcher;
    }

    private static final String sByIDSelection =
            HxMContract.HxMEntry.TABLE_NAME +
                    "." + HxMContract.HxMEntry.COLUMN_id + " = ? ";

//    @Override
//    public boolean OnCreate() {
//        mOpenHelper = new HxMDbHelper(getContext());
//        return true;
//    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case HXM:
                return HxMContract.HxMEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HxMDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "hxm"
            case HXM_WITH_ID:
            {
                retCursor = getHxmByArgs(uri, projection, selectionArgs, sortOrder);
            }
            case HXM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HxMContract.HxMEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getHxmByArgs(Uri uri, String[] projection, /*int id*/String[] selectionArgs, String sortOrder) {
        //int id = 1;
        String selection = sByIDSelection;
        //String[] selectionArgs;
        //selectionArgs= new String[]{Integer.toString(id)};

        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
                );
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection) selection = "1";
        switch (match) {
            case HXM:
                rowsDeleted = db.delete(
                    HxMContract.HxMEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HXM:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HxMContract.HxMEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case HXM:
                rowsUpdated = db.update(HxMContract.HxMEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case HXM: {
                long _id = db.insert(HxMContract.HxMEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = HxMContract.HxMEntry.buildHxmUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }
}
