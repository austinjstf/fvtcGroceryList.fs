package edu.fvtc.grocerylist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GroceryOwner extends AppCompatActivity {
    public static final String TAG = "GroceryOwner";

    EditText etOwner;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: Before Setting Layout");
        setContentView(R.layout.grocery_owner);
        Log.d(TAG, "onCreate: After Setting Layout");


        // Call method from below.
        initButtonSave();
        getOwnerPreference();

    }

    private void getOwnerPreference() {
        etOwner = findViewById(R.id.etOwner); // Initialize etOwner first
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Owner", MODE_PRIVATE);
        String isOwner = preferences.getString("ownerName", ""); // Retrieve owner name preference
        etOwner.setText(isOwner);
    }

    private void initButtonSave() {
        btnSave = findViewById(R.id.btnSaveOwner); // Use the class-level btnSave variable
        Log.d(TAG, "initButtonSave: Start");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Inside");
                // Set the owner name preference
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("Owner", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ownerName", etOwner.getText().toString());
                editor.apply(); // Use apply() instead of commit() for asynchronous saving

                Log.d(TAG, "onClick: End");

                // Showing Main Page
                Log.d(TAG, "onClick: Before Change Page");
                startActivity(new Intent(view.getContext(), MainActivity.class));
                Log.d(TAG, "onClick: After Change Page");
            }
        });
    }

}
