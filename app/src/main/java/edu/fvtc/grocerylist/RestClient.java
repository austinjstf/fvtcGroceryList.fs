package edu.fvtc.grocerylist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RestClient {
    public static final String TAG = "RestClient";
    public static void execGetRequest(String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<GroceryList> groceries = new ArrayList<GroceryList>();
        Log.d(TAG, "execGetRequest: " + url);

        try {
            Log.d(TAG, "execGetRequest: Inside the try #1 now?");
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);

                            try {
                                Log.d(TAG, "onResponse: Inside the try #2 now?");
                                JSONArray items = new JSONArray(response);
                                for(int i = 0; i < items.length(); i++)
                                {
                                    JSONObject object = items.getJSONObject(i);
                                    GroceryList grocery = new GroceryList();
                                    grocery.setId(object.getInt("id"));
                                    grocery.setDescription(object.getString("item"));
                                    grocery.setOnShoppingList(Boolean.valueOf(object.getString("isOnShoppingList")));
                                    grocery.setInCart(Boolean.valueOf(object.getString("isInCart")));

                                    String jsonPhoto = object.getString("photo");

                                    Log.d(TAG, "onResponse: " + object);
                                    Log.d(TAG, "onResponse: " + grocery);

                                    if(jsonPhoto != null)
                                    {
                                        byte[] bytePhoto = null;
                                        bytePhoto = Base64.decode(jsonPhoto, Base64.DEFAULT);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                        grocery.setPhoto(bmp);
                                    }


                                    groceries.add(grocery);

                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(groceries);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void execDeleteRequest(GroceryList groceryList,
                                         String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        try {
            executeRequest(groceryList, url, context, volleyCallback, Request.Method.DELETE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execPutRequest(GroceryList groceryList,
                                      String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        try {
            Log.d(TAG, "execPutRequest: Start");
            executeRequest(groceryList, url, context, volleyCallback, Request.Method.PUT);
            Log.d(TAG, "execPutRequest: End");

        } catch (Exception e) {
            Log.d(TAG, "execPutRequest: Something went wrong");
            throw new RuntimeException(e);
        }
    }
    public static void execPostRequest(GroceryList groceryList,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback)
    {
        try {
            Log.d(TAG, "execPostRequest: Start");
            executeRequest(groceryList, url, context, volleyCallback, Request.Method.POST);
            Log.d(TAG, "execPostRequest: End");
        } catch (Exception e) {
            Log.d(TAG, "execPostRequest: Something went wrong");
            throw new RuntimeException(e);
        }
    }

    private static void executeRequest(GroceryList groceryList,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback,
                                       int method)
    {
        Log.d(TAG, "executeRequest: " + method + ":" + url);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject object = new JSONObject();

            object.put("id", groceryList.getId());
            object.put("item", groceryList.getDescription());
            object.put("isOnShoppingList", 0);
            object.put("isInCart", 0);
            object.put("owner", "bfoote");

            Log.d(TAG, "executeRequest: " + object);

            if(groceryList.getPhoto() != null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createScaledBitmap(groceryList.getPhoto(), 144, 144, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String jsonPhoto = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                object.put("photo", jsonPhoto);
            }
            else
            {
                object.put("photo", null);
            }


            final String requestBody = object.toString();
            Log.d(TAG, "executeRequest: " + requestBody);

            JsonObjectRequest request = new JsonObjectRequest(method, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }
            })
            {
                @Override
                public byte[] getBody(){
                    Log.i(TAG, "getBody: " + object.toString());
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void execGetOneRequest(String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetOneRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<GroceryList> groceryLists = new ArrayList<GroceryList>();
        Log.d(TAG, "execGetOneRequest: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);

                            try {
                                JSONObject object = new JSONObject(response);
                                GroceryList grocery = new GroceryList();
                                grocery.setId(object.getInt("id"));
                                grocery.setDescription(object.getString("item"));
                                grocery.setOnShoppingList(Boolean.valueOf(object.getString("isOnShoppingList")));
                                grocery.setInCart(Boolean.valueOf(object.getString("isInCart")));
                                String jsonPhoto = object.getString("photo");

                                Log.d(TAG, "onResponse: Before Picture");

                                if(jsonPhoto != null)
                                {
                                    Log.d(TAG, "onResponse: Process Photo");
                                    byte[] bytePhoto = null;
                                    bytePhoto = Base64.decode(jsonPhoto, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                    grocery.setPhoto(bmp);
                                }

                                groceryLists.add(grocery);

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(groceryLists);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.d(TAG, "execGetOneRequest: Error" + e.getMessage());
        }
    }
}
