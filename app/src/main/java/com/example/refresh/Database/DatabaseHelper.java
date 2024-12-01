package com.example.refresh.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.refresh.DBParent;
import com.example.refresh.Database.Tables.ColumnEnum;
import com.example.refresh.User.UserInfo;

import java.util.ArrayList;

/**
 * Helper class to interact with the SQLite database for storing and retrieving user data.
 */
public abstract class DatabaseHelper<T extends DBParent<T>> extends SQLiteOpenHelper {

    // Database and table names
    public static final String DB_FILE = "refresh_database.db";
    public static final TableCols.Tables[] TABLES = TableCols.Tables.values();
    public TableCols.Tables Table;

    /**
     * Constructor to initialize the HelperDB class.
     * @param context The context to use for database creation.
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_FILE, null, 1);
    }

    /**
     * Creates the database tables upon first creation.
     * @param db The database to be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        db.execSQL(buildTable());
    }

    /**
     * Handles database upgrade logic (currently not implemented).
     * @param db The database to upgrade.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement database upgrade logic here if necessary
    }

    /**
     * Builds the SQL query to create the Users table.
     * @return The SQL query string.
     */
    public abstract String buildTable();

    public abstract boolean insert(T record, SQLiteDatabase db);

    public abstract ArrayList<T> getAllRecords(SQLiteDatabase db);

    public T getRecord(int index, SQLiteDatabase db) {
        ArrayList<T> list = getAllRecords(db);

        // Return the user at the specified index
        return list.get(index);
    }

    public abstract String getFromRecord(int index, ColumnEnum column, SQLiteDatabase db);

    public abstract int existsInDB(ColumnEnum column, String value, SQLiteDatabase db);
}
