package com.NewApp.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.NewApp.utils.PollingCheck;

import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by Peter on 10/08/2015.
 */
public class TestUtilities extends AndroidTestCase {
    static final String Test_HeartRate = "999";
    static final String Test_InstantSpeed = "99";
    static final String Test_posted = "2015-07-15 07:11:34";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
            "' did not match the expected value '" +
            expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createHxMValues(/*long hxmRowId*/) {
        ContentValues testValues = new ContentValues();
        //testValues.put(HxMContract.HxMEntry.COLUMN_id, hxmRowId);
        testValues.put(HxMContract.HxMEntry.COLUMN_HeartRate, Test_HeartRate);
        testValues.put(HxMContract.HxMEntry.COLUMN_InstantSpeed, Test_InstantSpeed);
        testValues.put(HxMContract.HxMEntry.COLUMN_posted, Test_posted);
        return testValues;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange, null);
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }

        private TestContentObserver(HandlerThread ht) {
            super(new android.os.Handler(ht.getLooper()));
            mHT = ht;
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
