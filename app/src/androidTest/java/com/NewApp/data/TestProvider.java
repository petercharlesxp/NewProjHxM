package com.NewApp.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Test;

/**
 * Created by Peter on 07/08/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createHxMValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(HxMContract.HxMEntry.CONTENT_URI, true, tco);
        Uri hxmUri = mContext.getContentResolver().insert(HxMContract.HxMEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long hxmRowId = ContentUris.parseId(hxmUri);
        assertTrue(hxmRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                HxMContract.HxMEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Log.v(LOG_TAG, "cusor.getCount: " + Integer.toString(cursor.getCount()));

        TestUtilities.validateCursor("testInsertReadProvider. Error validating HxMEntry.",
                cursor, testValues);
    }

    public void testBulkInsert() throws Exception {
//        ContentValues testValues = TestUtilities.createHxMValues();
//        Uri hxmUri = mContext.getContentResolver().insert(HxMContract.HxMEntry.CONTENT_URI, testValues);
//        long hxmRowId = ContentUris.parseId(hxmUri);
//
//        assertTrue(hxmRowId != -1);
//
//        Cursor cursor = mContext.getContentResolver().query(
//                HxMContract.HxMEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//        TestUtilities.validateCursor("testBulkInsert. Error vaildating HxMEngry.",
//                cursor, testValues);

        ContentValues[] bulkInsertContentValues = createBulkInsertHxMValues(/*hxmRowId*/);

        TestUtilities.TestContentObserver hxmObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(HxMContract.HxMEntry.CONTENT_URI, true, hxmObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(HxMContract.HxMEntry.CONTENT_URI, bulkInsertContentValues);

        hxmObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(hxmObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                HxMContract.HxMEntry.CONTENT_URI,
                null,
                null,
                null,
                HxMContract.HxMEntry.COLUMN_id + " DESC"
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertHxMValues(/*long hxmRowId*/) {
        ContentValues[] returnContnetValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues hxmValues = new ContentValues();
            hxmValues.put(HxMContract.HxMEntry.COLUMN_HeartRate, Integer.toString(100+i));
            hxmValues.put(HxMContract.HxMEntry.COLUMN_InstantSpeed, Integer.toString(i));
            hxmValues.put(HxMContract.HxMEntry.COLUMN_posted, "2015-08-17 19:55:54");
            returnContnetValues[i] = hxmValues;
        }
        return returnContnetValues;
    }

    public void testUpdateHxm() {
        ContentValues values = TestUtilities.createHxMValues();

        Uri hxmUri = mContext.getContentResolver().insert(HxMContract.HxMEntry.CONTENT_URI, values);
        long hxmRowId = ContentUris.parseId(hxmUri);

        assertTrue(hxmRowId != -1);
        Log.d(LOG_TAG, "testUpdateHxm New row id: " + hxmRowId);

        ContentValues updateValues = new ContentValues(values);
        updateValues.put(HxMContract.HxMEntry.COLUMN_HeartRate, "888");
        updateValues.put(HxMContract.HxMEntry.COLUMN_InstantSpeed, "88");
        updateValues.put(HxMContract.HxMEntry.COLUMN_posted, "2015-07-31 19:55:54");

        Cursor hxmCursor = mContext.getContentResolver().query(HxMContract.HxMEntry.CONTENT_URI, null,null, null, null );

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        hxmCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                HxMContract.HxMEntry.CONTENT_URI, updateValues, HxMContract.HxMEntry.COLUMN_id + "= ?",
                new String[] {Long.toString(hxmRowId)});
        assertEquals(count, 1);
        Log.d(LOG_TAG, hxmUri.toString()+ ", testUpdateHxm Update row id: " + hxmRowId);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver hxmObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(HxMContract.HxMEntry.CONTENT_URI, true, hxmObserver);

        deleteAllRecordsFromProvider();

        hxmObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(hxmObserver);
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                HxMContract.HxMEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                HxMContract.HxMEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Eroor: Records not deleted from table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB(){
        HxMDbHelper dbHelper = new HxMDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(HxMContract.HxMEntry.TABLE_NAME, null, null);
        db.delete(HxMContract.HxMEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testBasicHxmQuery() {
        HxMDbHelper dbHelper = new HxMDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues hxMValues = TestUtilities.createHxMValues();
        long hxmRowId = db.insert(HxMContract.HxMEntry.TABLE_NAME, null, hxMValues);
        assertTrue("Unable to Insert HxMEntry into the Database", hxmRowId != -1);

        db.close();

        Cursor hxmCursor = mContext.getContentResolver().query(
                HxMContract.HxMEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicHxMQuery", hxmCursor, hxMValues);
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(HxMContract.HxMEntry.CONTENT_URI);
        assertEquals("Error: the HxMEntry CONTENT_URI should return HxMEntry.CONTENT_TYPE",
                HxMContract.HxMEntry.CONTENT_TYPE, type);
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                HxMProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: HxMProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + HxMContract.CONTENT_AUTHORITY,
                    providerInfo.authority, HxMContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: HxMProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
}
