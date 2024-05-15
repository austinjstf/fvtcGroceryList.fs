package edu.fvtc.grocerylist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "groceries.db";


    public DatabaseHelper(@Nullable Context context,
                          @Nullable String name,
                          @Nullable SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;

        sql ="CREATE TABLE IF NOT EXISTS tblGroceryList" +
                " (Id integer primary key autoincrement, " +
                "Item text, IsOnShoppingList int, IsInCart int);";
        Log.d(TAG, "onCreate: " + sql);
        // Create the table
        db.execSQL(sql);

        // Insert an item.
        sql = "INSERT INTO tblGroceryList VALUES (1, 'Milk', '1', '1,');";
        db.execSQL(sql);

        // Insert an item.
        sql = "INSERT INTO tblGroceryList VALUES (2, 'Eggs', '1', '1,');";
        db.execSQL(sql);

        // Insert an item.
        sql = "INSERT INTO tblGroceryList VALUES (3, 'Cheese', '1', '0,');";
        db.execSQL(sql);

        // Insert an item.
        sql = "INSERT INTO tblGroceryList VALUES (4, 'Bacon', '0', '0,');";
        db.execSQL(sql);

        // Insert an item.
        sql = "INSERT INTO tblGroceryList VALUES (5, 'Salsa', '0', '0,');";
        db.execSQL(sql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + "vs" + newVersion);
    }
}
