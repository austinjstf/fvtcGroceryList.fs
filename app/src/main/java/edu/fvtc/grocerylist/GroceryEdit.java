package edu.fvtc.grocerylist;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GroceryEdit extends AppCompatActivity {

    public static final String TAG = "GroceryEdit";

    public static final int PERMISSION_REQUEST_PHONE = 102;
    public static final int PERMISSION_REQUEST_CAMERA = 103;
    public static final int CAMERA_REQUEST = 1888;

    GroceryList groceryList;
    int itemId = -1;
    String ownerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_edit);


        Log.d(TAG, "onCreate: Start");

        Bundle extras = getIntent().getExtras();
        itemId = extras.getInt("itemId");
        ownerId = extras.getString("ownerId");

        Log.d(TAG, "onCreate: " + ownerId);

        this.setTitle("GroceryList Edit: " + itemId);

        Log.d(TAG, "onCreate: End: " + itemId);

        groceryList = new GroceryList();
        if(itemId != -1)
        {
            // Get the team
            initGrocery(itemId);
        }

        // Init methods.
        initImageButton();
        initSaveButton();

        // Since we are only changing the item and the picture.
        initTextChanged(R.id.etEditItem);

        Log.d(TAG, "onCreate: End");
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Log.d(TAG, "onActivityResult: Here");
                Bitmap photo= (Bitmap)data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageButton = findViewById(R.id.imgEditPhoto);
                imageButton.setImageBitmap(scaledPhoto);
                groceryList.setPhoto(scaledPhoto);
            }
        }
    }

    private void initImageButton() {
        ImageButton imageTeam = findViewById(R.id.imgEditPhoto);

        imageTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    // Check for the manifest permission
                    if(ContextCompat.checkSelfPermission(GroceryEdit.this, android.Manifest.permission.CAMERA) != PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(GroceryEdit.this, android.Manifest.permission.CAMERA)){
                            Snackbar.make(findViewById(R.id.grocery_edit), "Teams requires this permission to take a photo.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: snackBar");
                                    ActivityCompat.requestPermissions(GroceryEdit.this,
                                            new String[] {android.Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                                }
                            }).show();
                        }
                        else {
                            Log.d(TAG, "onClick: ");
                            ActivityCompat.requestPermissions(GroceryEdit.this,
                                    new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                            takePhoto();
                        }
                    }
                    else{
                        Log.d(TAG, "onClick: ");
                        takePhoto();
                    }
                }
                else {
                    // Only rely on the previous permissions
                    takePhoto();
                }
            }
        });

    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void readFromAPI(int itemId)
    {
        try{
            Log.d(TAG, "readFromAPI: Start");
            RestClient.execGetOneRequest("https://fvtcdp.azurewebsites.net/api/GroceryList/" + itemId,
                    this,
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<GroceryList> result) {
                            Log.d(TAG, "onSuccess: Got Here!");
                            groceryList = result.get(0);
                            RebindGrocery();
                        }
                    });
        }
        catch(Exception e){
            Log.e(TAG, "readFromAPI: Error: " + e.getMessage());
        }
    }

    private void readFromAPI(String ownerId)
    {
        try{
            Log.d(TAG, "readFromAPI: Start");
            RestClient.execGetOneRequest("https://fvtcdp.azurewebsites.net/api/GroceryList/" + ownerId,
                    this,
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<GroceryList> result) {
                            Log.d(TAG, "onSuccess: Got Here!");
                            groceryList = result.get(0);
                            RebindGrocery();
                        }
                    });
        }
        catch(Exception e){
            Log.e(TAG, "readFromAPI: Error: " + e.getMessage());
        }
    }

    private void initSaveButton() {
        Button btnSave = findViewById(R.id.btnSaveEdit);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemId == -1)
                {
                    Log.d(TAG, "onClick: " + groceryList.toString());

                    RestClient.execPostRequest(groceryList, "https://fvtcdp.azurewebsites.net/api/GroceryList/",
                            GroceryEdit.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryList> result) {
                                    groceryList.setId(result.get(0).getId());
                                    Log.d(TAG, "onSuccess: Post" + groceryList.getId());
                                }
                            });
                }
                else {
                    Log.d(TAG, "onClick: Before Put Request.");
                    RestClient.execPutRequest(groceryList, "https://fvtcdp.azurewebsites.net/api/GroceryList/" + itemId,
                            GroceryEdit.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<GroceryList> result) {
                                    Log.d(TAG, "onSuccess: Put" + groceryList.getId());
                                }
                            });
                }

                Log.d(TAG, "onClick: " + ownerId);
                readFromAPI(ownerId);

            }
        });
    }

    private void initTextChanged(int controlId)
    {
        EditText editText = findViewById(controlId);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groceryList.setControlText(controlId, s.toString());
            }
        });
    }

    private void initGrocery(int itemId)
    {
        readFromAPI(itemId);
    }

    private void RebindGrocery() {
        EditText editName = findViewById(R.id.etEditItem);
        ImageButton imageButtonPhoto = findViewById(R.id.imgEditPhoto);

        editName.setText(groceryList.getDescription());

        if(groceryList.getPhoto() == null)
        {
            Log.d(TAG, "rebindTeam: Null photo");
            groceryList.setPhoto(BitmapFactory.decodeResource(this.getResources(), R.drawable.photoicon));
        }
        imageButtonPhoto.setImageBitmap(groceryList.getPhoto());
    }

}
