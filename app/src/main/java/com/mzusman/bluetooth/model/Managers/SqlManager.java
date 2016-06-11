package com.mzusman.bluetooth.model.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mzusman.bluetooth.model.RideDescription;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Asaf on 11/06/2016.
 */
public class SqlManager {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RidesFiles.db";
    public static final String RIDES_TABLE = "rides";
    public static final String RIDE_FILE_NAME = "rides";
    public static final String RIDE_DRIVER_ID = "did";
    public static final String RIDE_SENT = "sent";


    public static void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                RIDES_TABLE + " (" +
                RIDE_FILE_NAME + " TEXT PRIMARY KEY," +
                RIDE_DRIVER_ID + " TEXT," +
                RIDE_SENT + " TEXT);");
    }

    public static void add(SQLiteDatabase db, RideDescription rideDescription) {
        ContentValues values = getValues(rideDescription);
        db.insert(RIDES_TABLE, null, values);
    }

    private static ContentValues getValues(RideDescription rideDescription) {
        ContentValues values = new ContentValues();
        values.put(RIDE_FILE_NAME, rideDescription.getFileName());
        values.put(RIDE_DRIVER_ID, rideDescription.getDriverID());
        if (rideDescription.isSent()) {
            values.put(RIDE_SENT, "YES");
        } else {
            values.put(RIDE_SENT, "NO");
        }
        return values;
    }

    public static boolean update(SQLiteDatabase db, RideDescription rideDescription) {
        return db.replace(RIDES_TABLE, null, getValues(rideDescription)) > 0;
    }

    public static List<RideDescription> getAllRides(SQLiteDatabase db) {
        Cursor cursor = db.query(RIDES_TABLE, null, null, null, null, null, null);

        List<RideDescription> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int fileIndex = cursor.getColumnIndex(RIDE_FILE_NAME);
            int didIndex = cursor.getColumnIndex(RIDE_DRIVER_ID);
            int sentIndex = cursor.getColumnIndex(RIDE_SENT);
            do {
                String file = cursor.getString(fileIndex);
                String sent = cursor.getString(sentIndex);
                String did = cursor.getString(didIndex);
                boolean isSent = sent.equals("YES");
                RideDescription description = new RideDescription(isSent, file, did);
                list.add(description);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + RIDES_TABLE);
    }

    public static boolean checkIfExist(SQLiteDatabase database, String fileName) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + RIDES_TABLE + "WHERE "
                + RIDE_FILE_NAME + " = " + fileName, null);
        return cursor.moveToNext();
    }
}
