package com.NewApp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Peter on 07/08/2015.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_HXM_ID = 1;

    private static final Uri TEST_HXM_DIR = HxMContract.HxMEntry.CONTENT_URI;
    //private static final Uri TEST_HXM_WITH_HXM_DIR = HxMContract.HxMEntry.buildHxmUri(TEST_HXM_ID);


    public void testUriMatcher() {
        UriMatcher testMather = HxMProvider.buildUriMathcer();

        assertEquals("Error: The URI was matched incorrectly.",
                testMather.match(TEST_HXM_DIR), HxMProvider.HXM);

    }
}
