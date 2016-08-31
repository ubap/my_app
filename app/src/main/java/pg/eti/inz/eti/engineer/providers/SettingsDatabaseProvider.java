package pg.eti.inz.eti.engineer.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pg.eti.inz.eti.engineer.settings.Option;
import pg.eti.inz.eti.engineer.utils.Constants;


/**
 * Database provider for application settings
 */
public class SettingsDatabaseProvider extends SQLiteOpenHelper {

    private static final Logger LOGGER = Logger.getLogger(SettingsDatabaseProvider.class.getName());
    private SQLiteDatabase database;

    public SettingsDatabaseProvider(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
    }

    private void insertOptions(List<Option> options) {
        LOGGER.log(Level.INFO, "Inserting options to database table " + Constants.OPTIONS_DATABASE_TABLE);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Option option : options) {
            contentValues.put(option.getName(), option.getValue());
            sqLiteDatabase.insert(Constants.OPTIONS_DATABASE_TABLE, null, contentValues);

            contentValues.clear();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists" + Constants.OPTIONS_DATABASE_TABLE +
                "(id integer primary key, " + Constants.OPTIONS_DB_NAME_COLUMN + " text unique, " +
                Constants.OPTIONS_DB_VALUE_COLUMN + " text);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + Constants.OPTIONS_DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public List<Option> getOptions() {
        List<Option> options = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Constants.OPTIONS_DATABASE_TABLE, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            options.add(new Option(cursor.getString(cursor.getColumnIndex(Constants.OPTIONS_DB_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(Constants.OPTIONS_DB_VALUE_COLUMN))));
            cursor.moveToNext();
        }

        return options;
    }

    public void updateOptions(List<Option> options) {
        LOGGER.log(Level.INFO, "Updating options to database table " + Constants.OPTIONS_DATABASE_TABLE);

        int updatedRows = 0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Option option : options) {
            contentValues.put(option.getName(), option.getValue());
            updatedRows += sqLiteDatabase.update(Constants.OPTIONS_DATABASE_TABLE, contentValues,
                    Constants.OPTIONS_DB_NAME_COLUMN + "= ?", new String[] {option.getName()});

            contentValues.clear();
        }

        if (updatedRows == 0) {
            insertOptions(options);
        }
    }
}
