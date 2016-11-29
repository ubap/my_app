package pg.eti.inz.engineer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import pg.eti.inz.engineer.utils.Log;

/**
 * Created by ubap on 2016-11-07.
 */

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "BikeDashboard.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final class TripContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private TripContract() {}

        /* Inner class that defines the table contents */
        public static class Trip implements BaseColumns {
            public static final String TABLE_NAME = "trip";
            public static final String COLUMN_NAME_START_TIME = "start_time";
            public static final String COLUMN_NAME_FINISH_TIME = "finish_time";
            public static final String COLUMN_NAME_DISTANCE = "distance";
            public static final String COLUMN_NAME_AVG_SPEED = "avg_speed";
            public static final String COLUMN_NAME_TRIP_DATA = "trip_data";
        }

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TripContract.Trip.TABLE_NAME + " (" +
                    TripContract.Trip._ID + " INTEGER PRIMARY KEY," +
                    TripContract.Trip.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
                    TripContract.Trip.COLUMN_NAME_FINISH_TIME + TEXT_TYPE + COMMA_SEP +
                    TripContract.Trip.COLUMN_NAME_TRIP_DATA + BLOB_TYPE + COMMA_SEP +
                    TripContract.Trip.COLUMN_NAME_AVG_SPEED + TEXT_TYPE + COMMA_SEP +
                    TripContract.Trip.COLUMN_NAME_DISTANCE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TripContract.Trip.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d();
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d();
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d();
        onUpgrade(db, oldVersion, newVersion);
    }
}
