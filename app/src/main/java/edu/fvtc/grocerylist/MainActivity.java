package edu.fvtc.grocerylist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.fvtc.grocerylist.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";
    public static final String FILENAME = "data.txt";
    public static final String XMLFILENAME = "data.xml";
    ArrayList<GroceryList> groceries = new ArrayList<>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    RecyclerView rvGrocery;
    GroceryAdapter groceryAdapter;
    GroceryList grocery;


    // Constants to define the type of list
    public static final int TYPE_MASTER_LIST = 1;
    public static final int TYPE_SHOPPING_LIST = 2;

    // Database properties added
    DatabaseAdapter dbAdapter;
    DatabaseHelper dbHelper;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "onClick: Start");
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            // Use the index to get.
            GroceryList groceryList = groceries.get(position);
            Intent intent = new Intent(MainActivity.this, GroceryEdit.class);
            intent.putExtra("itemId", groceryList.getId());
            intent.putExtra("ownerId", groceryList.getOwner());

            Log.d(TAG, "onClick: " + groceryList.getOwner());

            startActivity(intent);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            Log.d(TAG, "onCheckedChanged: " + isChecked);
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) buttonView.getTag();
            int position = viewHolder.getAdapterPosition();
            groceries.get(position).setOnShoppingList(isChecked);
            DatabaseAdapter ds = new DatabaseAdapter(MainActivity.this);
            ds.Update(groceries.get(position));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Init database.
        //InitDatabase();

        // Getting owner preference from Grocery Owner page.
        Log.d(TAG, "onCreate: Before Setting Owner");
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Owner", MODE_PRIVATE);
        String ownerSet = new String(preferences.getString("ownerName", ""));

        if(ownerSet.isEmpty())
        {
            Log.d(TAG, "onCreate: Owner is NULL");
            Intent intent = new Intent(MainActivity.this, GroceryOwner.class);

            Log.d(TAG, "onCreate: BEFORE GOING TO OWNER PAGE");
            startActivity(intent);
            finish();
            Log.d(TAG, "onCreate: WE SHOULD BE ON THE OWNER PAGE NOW");
        }
        else
        {
            Log.d(TAG, "onCreate: Before OwnerSet");
            // Owner is set all good to go.

            // Now we are reading from the API instead.
            Log.d(TAG, "onCreate: Start (API)");
            readFromAPI(ownerSet);
            Log.d(TAG, "onCreate: End (API)");

            //if(groceries.size() == 0)
            //{
            // Call Data.
            //CreateGroceries();
            //}

            // Otherwise, please rebind.
            //RebindGroceries();


            // Check if the activity was started with an intent containing list type
            int listType = getIntent().getIntExtra("LIST_TYPE", TYPE_MASTER_LIST);
            if (listType == TYPE_MASTER_LIST) {
                // Master List
                setTitle("Master List for: " + ownerSet);
                //RebindGroceries();

            }
            else
            {
                // Shopping List
                setTitle("Shopping List for: " + ownerSet);
                //RebindShopping();
            }
        }
    }

    private void readFromAPI(String owner)
    {
        try{
            Log.d(TAG, "readFromAPI: Start");
            Log.d(TAG, "readFromAPI: " + owner);


            RestClient.execGetRequest("https://fvtcdp.azurewebsites.net/api/GroceryList/" + owner,
                    this,
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<GroceryList> result) {
                            Log.d(TAG, "onSuccess: Got Here!");
                            groceries = result;
                            RebindGroceries();
                        }
                    });
        }
        catch(Exception e){
            Log.e(TAG, "readFromAPI: Error: " + e.getMessage());
        }
    }

    private void RebindGroceries()
    {
        // Rebind the RecyclerView
        Log.d(TAG, "RebindTeams: Start");
        rvGrocery = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvGrocery.setLayoutManager(layoutManager);
        groceryAdapter = new GroceryAdapter(groceries, this);
        groceryAdapter.setOnItemClickListener(onClickListener);
        groceryAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
        rvGrocery.setAdapter(groceryAdapter);
        groceryAdapter.notifyDataSetChanged();
    }

    private void RebindShopping() {
        // Load shopping items from the database
        DatabaseAdapter dbAdapter = new DatabaseAdapter(MainActivity.this);
        dbAdapter.open(); // Ensure database is opened
        ArrayList<GroceryList> shoppingItems = dbAdapter.LoadShopping();

        // Filtered list of items with IsOnShoppingList = 1
        ArrayList<GroceryList> filteredList = new ArrayList<>();
        for (GroceryList item : shoppingItems) {
            if (item.getOnShoppingList()) {
                filteredList.add(item);
            }
        }

        // Rebind the RecyclerView with the filtered list
        rvGrocery = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvGrocery.setLayoutManager(layoutManager);
        groceryAdapter = new GroceryAdapter(filteredList, this);
        groceryAdapter.setOnItemClickListener(onClickListener);
        groceryAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
        rvGrocery.setAdapter(groceryAdapter);
    }

    private void CreateGroceries()
    {
        groceries = new ArrayList<GroceryList>();
        DatabaseAdapter ds = new DatabaseAdapter(MainActivity.this);
        ds.open(true);
        groceries = ds.LoadGroceries();
    }

    private void InitDatabase()
    {
        DatabaseAdapter da = new DatabaseAdapter(MainActivity.this);
        groceries = da.LoadGroceries();
        Log.d(TAG, "InitDatabase: Groceries Loaded: " + groceries.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menuAddItem)
        {
            addItemDialog();
        }

        if(id == R.id.menuDeleteChecked)
        {
            deleteCheckedItems();
        }

        if(id == R.id.menuClearAll)
        {
            Log.d(TAG, "onOptionsItemSelected: Check");
            ClearAll();
        }

        if(id== R.id.menuShowMaster)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.menuShoppingList) {
            Log.d(TAG, "onOptionsItemSelected: MenuShoppingListStart");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("LIST_TYPE", MainActivity.TYPE_SHOPPING_LIST);
            startActivity(intent);
            Log.d(TAG, "onOptionsItemSelected: MenuShoppingListEnd");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCheckedItems()
    {
        if (dbAdapter == null) {
            dbAdapter = new DatabaseAdapter(MainActivity.this);
        }

        Log.d(TAG, "deleteCheckedItems: Start, ?");
        dbAdapter.DeleteAllChecked();

        Log.d(TAG, "deleteCheckedItems: End, ... ");

        // Reload the groceries from the database
        groceries.clear(); // Clear the current data set
        groceries.addAll(dbAdapter.LoadGroceries()); // Load fresh data from the database

        // Refresh the UI after removing items
        RecyclerView recyclerView = findViewById(R.id.rvList);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void ClearAll()
    {
        Log.d(TAG, "ClearAll: Check1");
        dbAdapter = new DatabaseAdapter(MainActivity.this);
        dbAdapter.Clear();

        // Reload the groceries from the database
        groceries.clear(); // Clear the current data set
        groceries.addAll(dbAdapter.LoadGroceries()); // Load fresh data from the database

        // Refresh the UI after removing items
        RecyclerView recyclerView = findViewById(R.id.rvList);
        recyclerView.getAdapter().notifyDataSetChanged();
        Log.d(TAG, "ClearAll: Check2");
    }

    @Override
    public boolean onSupportNavigateUp () {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void addItemDialog () {
        Log.d(TAG, "addItemDialog: Start");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View addView = layoutInflater.inflate(R.layout.additem, null);

        new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(addView)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Check the list type
                                int listType = getIntent().getIntExtra("LIST_TYPE", MainActivity.TYPE_MASTER_LIST);
                                EditText txtAddItem = addView.findViewById(R.id.etAddItem);
                                String item = txtAddItem.getText().toString();

                                // Master List
                                if (listType == MainActivity.TYPE_MASTER_LIST) {
                                    Log.d(TAG, "onClick: Before Insert");

                                    /*if (dbAdapter == null) {
                                        dbAdapter = new DatabaseAdapter(MainActivity.this);
                                    }

                                    dbAdapter.Insert(item);*/

                                    RestClient restClient;

                                    Log.d(TAG, "onClick: After Insert");

                                    // Reload the groceries from the database
                                    groceries.clear(); // Clear the current data set
                                    groceries.addAll(dbAdapter.LoadGroceries()); // Load fresh data from the database

                                    // Refresh the UI after removing items
                                    RecyclerView recyclerView = findViewById(R.id.rvList);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }

                                // Viewing Shopping List.
                                else {

                                }
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: CANCEL");
                            }
                        }).show();
    }

    private void WriteXMLFile () {
        try {
            Log.d(TAG, "WriteXMLFile: Start");
            FileIO fileIO = new FileIO();
            fileIO.writeXMLFile(XMLFILENAME, this, groceries);
            Log.d(TAG, "WriteXMLFile: End");
        } catch (Exception e) {
            Log.d(TAG, "WriteXMLFile: " + e.getMessage());
        }
    }

    private void ReadXMLFile () {
        try {
            FileIO fileIO = new FileIO();
            groceries = fileIO.ReadFromXML(XMLFILENAME, this);
            Log.d(TAG, "ReadXMLFile: Actors: " + groceries.size());
        } catch (Exception e) {
            Log.d(TAG, "ReadXMLFile: " + e.getMessage());
        }

    }

    private void WriteTextFile () {
        try {
            FileIO fileIO = new FileIO();
            int counter = 0;
            String[] data = new String[groceries.size()];

            for (GroceryList groceryList : groceries) {
                data[counter++] = groceryList.toString();
            }
            fileIO.writeFile(FILENAME, this, data);

        } catch (Exception e) {
            Log.d(TAG, "WriteTextFile: " + e.getMessage());
        }
    }

    private void ReadTextFile() {
        try {
            FileIO fileIO = new FileIO();
            ArrayList<String> strData = fileIO.readFile(FILENAME, this);

            groceries = new ArrayList<GroceryList>();

            for (String s : strData) {
                String[] data = s.split("\\|");

                boolean isOnShoppingList = Boolean.parseBoolean(data[2]);
                boolean isinCart = Boolean.parseBoolean(data[3]);

                groceries.add(new GroceryList(Integer.parseInt(data[0]), data[1], isOnShoppingList, isinCart));

                Log.d(TAG, "ReadTextFile: " + groceries.get(groceries.size() - 1).getDescription());
            }
            Log.d(TAG, "ReadTextFile: " + groceries.size());

        } catch (Exception e) {
            Log.d(TAG, "ReadTextFile: " + e.getMessage());
        }
    }

}