package com.abearablecode.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rumiyamurtazina on 12/23/15.
 */
public class TodoItemDatabase extends SQLiteOpenHelper {
    private static TodoItemDatabase sInstance;

    // Database Info
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_ITEMS = "items";

    // Items Table Columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TEXT = "text";

    // A string used as a tag when printing log messages to the console (ddms)
    private static final String TAG = "YOUR-TAG-NAME";

    public static synchronized TodoItemDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the tables
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY," + // Define a primary keY
                KEY_ITEM_TEXT + " TEXT" +
                ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    // Insert an item into the database
    public void addItem(Item item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, item.text);
            // SQLite auto increments the primary key column.

            db.insertOrThrow(TABLE_ITEMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add item to database");
        } finally {
            db.endTransaction();
        }
    }

    // Update an item in the database
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_TEXT, item.text);


        // Updating a to do list item for a specific item id
        return db.update(TABLE_ITEMS, values, KEY_ITEM_ID + " = ?",
                new String[] { String.valueOf(item.id)});
    }

    // Get all items in the database
    public List<Item> getAllItems(){
        List<Item> items = new ArrayList<>();

        // SELECT * FROM ITEMS
        String ITEMS_SELECT_QUERY = String.format("SELECT * FROM " + TABLE_ITEMS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Item newItem = new Item();
                    newItem.id = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ID));
                    newItem.text = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TEXT));

                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }
}
