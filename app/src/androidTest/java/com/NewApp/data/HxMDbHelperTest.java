package com.NewApp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Peter on 05/08/2015.
 */
public class HxMDbHelperTest extends TestUtilities {

    public static final String LOG_TAG = HxMDbHelperTest.class.getSimpleName();



    public void deleteTheDatabase() {
        mContext.deleteDatabase(HxMDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Exception {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(HxMContract.HxMEntry.TABLE_NAME);

        mContext.deleteDatabase(HxMDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new HxMDbHelper(this.mContext)
                .getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while ( c.moveToNext() );
        assertTrue("Error: Your database was created without HxM entry", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + HxMContract.HxMEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> hxmColumnHashSet = new HashSet<>();
        hxmColumnHashSet.add(HxMContract.HxMEntry.COLUMN_id);
        hxmColumnHashSet.add(HxMContract.HxMEntry.COLUMN_HeartRate);
        hxmColumnHashSet.add(HxMContract.HxMEntry.COLUMN_InstantSpeed);
        hxmColumnHashSet.add(HxMContract.HxMEntry.COLUMN_posted);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            hxmColumnHashSet.remove(columnName);
        } while (c.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required HxM entry columns",
                hxmColumnHashSet.isEmpty());
        db.close();
    }

    public void testHxMTable() {
        insertHxM();
    }

    private long insertHxM() {
        HxMDbHelper dbHelper = new HxMDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createHxMValues();

        long hxmRowId;
        hxmRowId = db.insert(HxMContract.HxMEntry.TABLE_NAME, null, testValues);

        assertTrue(hxmRowId != -1);
        Log.v(LOG_TAG, "test insertHxM Pass - ID: " + hxmRowId);

        Cursor cursor = db.query(
                HxMContract.HxMEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from hxm query", cursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);

            String error = "Error: HxM Query Validation Failed";
            assertFalse(
                    "Column '" + columnName + "' not found. " + error,
                    idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(
                    "Value '" + entry.getValue().toString() +
                            "' did not match the expected value '" +
                            expectedValue + "'. " + error,
                    expectedValue,
                    cursor.getString(idx)
            );
        }

        assertFalse("Error: More than one record returned from hxm query",
                cursor.moveToNext());

        cursor.close();
        db.close();

        return hxmRowId;
    }

}