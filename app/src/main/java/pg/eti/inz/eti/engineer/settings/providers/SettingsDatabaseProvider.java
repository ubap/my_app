package pg.eti.inz.eti.engineer.settings.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pg.eti.inz.eti.engineer.settings.Option;
import pg.eti.inz.eti.engineer.settings.utils.OptionType;
import pg.eti.inz.eti.engineer.settings.utils.SettingsConstants;
import pg.eti.inz.eti.engineer.utils.Constants;


/**
 * Database provider for application settings
 */
public class SettingsDatabaseProvider extends SQLiteOpenHelper {

    private static final Logger LOGGER = Logger.getLogger(SettingsDatabaseProvider.class.getName());

    public SettingsDatabaseProvider(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists  " + SettingsConstants.OPTIONS_DATABASE_TABLE +
                "(id integer primary key autoincrement, " + SettingsConstants.OPTIONS_DB_NAME_COLUMN + " text unique, " +
                SettingsConstants.OPTIONS_DB_VALUE_COLUMN + " text, " + SettingsConstants.OPTIONS_DB_TYPE_COLUMN +" text);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists " + SettingsConstants.OPTIONS_DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Map<String, Option> getOptions() {
        Map<String, Option> options = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String name;
        String value;
        String type;

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + SettingsConstants.OPTIONS_DATABASE_TABLE, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            name = cursor.getString(cursor.getColumnIndex(SettingsConstants.OPTIONS_DB_NAME_COLUMN));
            value = cursor.getString(cursor.getColumnIndex(SettingsConstants.OPTIONS_DB_VALUE_COLUMN));
            type = cursor.getString(cursor.getColumnIndex(SettingsConstants.OPTIONS_DB_TYPE_COLUMN));

            options.put(name, new Option(name, value, type));
            cursor.moveToNext();
        }

        if (options.isEmpty()) {
            setDefaultValues();
            return getOptions();
        } else {
            return options;
        }
    }

    public void updateOptions(List<Option> options) {
        LOGGER.log(Level.INFO, "Updating options to database table " + SettingsConstants.OPTIONS_DATABASE_TABLE);

        int updatedRows = 0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Option option : options) {
            contentValues.put(SettingsConstants.OPTIONS_DB_NAME_COLUMN, option.getName());
            contentValues.put(SettingsConstants.OPTIONS_DB_VALUE_COLUMN, option.getValue());


            updatedRows += sqLiteDatabase.update(SettingsConstants.OPTIONS_DATABASE_TABLE, contentValues,
                    SettingsConstants.OPTIONS_DB_NAME_COLUMN + "= ?", new String[] {option.getName()});

            contentValues.clear();
        }

        if (updatedRows == 0) {
            insertOptions(options);
        }
    }

    private void insertOptions(List<Option> options) {
        LOGGER.log(Level.INFO, "Inserting options to database table " + SettingsConstants.OPTIONS_DATABASE_TABLE);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Option option : options) {
            contentValues.put(SettingsConstants.OPTIONS_DB_NAME_COLUMN, option.getName());
            contentValues.put(SettingsConstants.OPTIONS_DB_VALUE_COLUMN, option.getValue());
            contentValues.put(SettingsConstants.OPTIONS_DB_TYPE_COLUMN, option.getType());

            sqLiteDatabase.insert(SettingsConstants.OPTIONS_DATABASE_TABLE, null, contentValues);

            contentValues.clear();
        }
    }

    private void setDefaultValues() {
        List<Option> defaultOptions = new ArrayList<>();

        defaultOptions.add(new Option(SettingsConstants.OPTIONS_DB_LOCALE, SettingsConstants.ENGLISH_LOCALE,
                OptionType.STRING));
        defaultOptions.add(new Option(SettingsConstants.OPTIONS_DB_SERVER_ADDRESS, "123.456.789.69",
                OptionType.STRING));

        insertOptions(defaultOptions);
    }
}
