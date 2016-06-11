package com.mzusman.bluetooth.model.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mzusman.bluetooth.model.RideDescription;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Asaf on 11/06/2016.
 */
public class SqlManager extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RidesFiles.db";
    public static final String RIDES_TABLE = "rides";
    public static final String RIDE_ID = "rid";
    public static final String RIDE_FILE_NAME = "rides";
    public static final String RIDE_SENT = "sent";


    public SqlManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +
                RIDES_TABLE + " (" +
                RIDE_ID + " TEXT PRIMARY KEY," +
                RIDE_FILE_NAME + " TEXT," +
                RIDE_SENT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void add(SQLiteDatabase db, RideDescription rideDescription) {
        ContentValues values = new ContentValues();
        values.put(RIDE_ID, rideDescription.getId());
        values.put(RIDE_FILE_NAME, rideDescription.getFileName());
        if (rideDescription.isSent()) {
            values.put(RIDE_SENT, "YES");
        } else {
            values.put(RIDE_SENT, "NO");
        }
        db.insert(RIDES_TABLE, RIDE_ID, values);
    }

    public static List<RideDescription> getAllRides(SQLiteDatabase db) {
        Cursor cursor = db.query(RIDES_TABLE, null, null, null, null, null, null);

        List<RideDescription> list = new LinkedList<>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(RIDE_ID);
            int fileIndex = cursor.getColumnIndex(RIDE_FILE_NAME);
            int sentIndex = cursor.getColumnIndex(RIDE_SENT);
            do {
                String id = cursor.getString(idIndex);
                String file = cursor.getString(fileIndex);
                String sent = cursor.getString(sentIndex);
                boolean isSent = sent.equals("YES");
                RideDescription description = new RideDescription(id, isSent, file);
                list.add(description);
            } while (cursor.moveToNext());
        }
        return list;
    }


}
