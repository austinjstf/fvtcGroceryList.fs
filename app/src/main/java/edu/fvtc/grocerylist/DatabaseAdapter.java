package edu.fvtc.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseAdapter {

    public static final String TAG = "DatabaseAdapter";

    // Database helper.
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    public DatabaseAdapter(Context context)
    {
        dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASE_NAME,
                null,
                DatabaseHelper.DATABASE_VERSION);
        open();
    }

    public void open() throws SQLException {
        open(false);
    }

    public void open(boolean refresh)  throws SQLException{

        db = dbHelper.getWritableDatabase();
        if(refresh) refreshData();
        Log.d(TAG, "open: " + db.isOpen());
    }

    public void refreshData()
    {
        Log.d(TAG, "refreshData: Start");
        ArrayList<GroceryList> groceryLists = new ArrayList<GroceryList>();

        groceryLists.add(new GroceryList(1, "Milk", true, false));
        groceryLists.add(new GroceryList(2, "Eggs", false, true));
        groceryLists.add(new GroceryList(3, "Cheese", true, true));
        groceryLists.add(new GroceryList(3, "Bacon", false, false));

        // Delete and reinsert all the teams
        int results = 0;
        for(GroceryList groceryList : groceryLists){
            results += Insert(groceryList);
        }
        Log.d(TAG, "refreshData: End: " + results + " rows...");
    }

    public void DeleteAllChecked() {
        try {
            if (db != null) {
                db.delete("tblGroceryList", "IsInShoppingCart = ?", new String[]{"1"});
            }
        } catch (Exception e) {
            Log.d(TAG, "DeleteAllChecked: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*public void DeleteAllChecked() {
        try {
            if (db != null) {
                db.delete("tblGroceryList", "IsInShoppingCart = true", null);
            }
        } catch (Exception e) {
            Log.d(TAG, "DeleteAllChecked: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    public int Update(GroceryList groceryList)
    {
        Log.d(TAG, "update: Start" + groceryList.toString());
        int rowsaffected = 0;

        if(groceryList.getId() < 1)
            return Insert(groceryList);

        try{
            ContentValues values = new ContentValues();
            values.put("item", groceryList.getDescription());
            values.put("IsOnShoppingList", groceryList.getOnShoppingList());
            values.put("IsInCart", groceryList.getInCart());


            String where = "id = " + groceryList.getId();

            rowsaffected = (int)db.update("tblGroceryList", values, where, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }

        return rowsaffected;
    }

    public void Clear() {

        // Database check
        if (db != null)
        {
            // whereClause, getting a certain Id to update.
            //String whereClause = "Id == 2";


            // Different way to insert using database context instead of RawQuery.
            ContentValues contentValues = new ContentValues();

            // Updated values.
            contentValues.put("IsOnShoppingList", 0);

            // db Update, table, values, where, argument.
            db.update("tblGroceryList", contentValues, null, null);
            Log.d(TAG, "Update: " + contentValues);

            // Refresh, to get most up to date current items in database.
            //Load();
        }
    }

    public int Insert(GroceryList groceryList)
    {
        Log.d(TAG, "insert: Start");
        int rowsaffected = 0;

        try{
            ContentValues values = new ContentValues();
            values.put("Item", groceryList.getDescription());
            values.put("IsInShoppingCart", 0);
            values.put("IsInCart", 0);

            rowsaffected = (int)db.insert("tblGroceryList", null, values);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsaffected;

    }

    public int Insert(String item)
    {
        Log.d(TAG, "insert: Start");
        int rowsaffected = 0;

        try{
            ContentValues values = new ContentValues();
            values.put("Item", item);
            values.put("IsOnShoppingList", 0);
            values.put("IsInCart", 0);

            rowsaffected = (int)db.insert("tblGroceryList", null, values);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsaffected;

    }

    public ArrayList<GroceryList> LoadGroceries() {

        ArrayList<GroceryList> groceryList = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT * FROM tblGroceryList;", null);

            while(cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String description = cursor.getString(1);
                boolean isOnShoppingList = cursor.getInt(2) == 1;
                boolean isInCart = cursor.getInt(3) == 1;

                // Create GroceryList object and add it to the list
                GroceryList groceryItem = new GroceryList(id, description, isOnShoppingList, isInCart);
                groceryList.add(groceryItem);
            }

            cursor.close();
        }

        return groceryList;

    }

    public ArrayList<GroceryList> LoadShopping() {

        ArrayList<GroceryList> groceryList = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT * FROM tblGroceryList;", null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String description = cursor.getString(1);
                boolean isOnShoppingList = cursor.getInt(2) == 1;
                boolean isInCart = cursor.getInt(3) == 1;

                // Only include items where IsOnShoppingList is true
                if (isOnShoppingList) {
                    GroceryList groceryItem = new GroceryList(id, description, isOnShoppingList, isInCart);
                    groceryList.add(groceryItem);
                }
            }

            cursor.close();
        }

        return groceryList;

    }
}
