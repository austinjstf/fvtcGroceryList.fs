package edu.fvtc.grocerylist;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileIO {

    public static final String TAG = "FileIO";

    public ArrayList<GroceryList> ReadFromXML(String filename, AppCompatActivity appCompatActivity)
    {
        ArrayList<GroceryList> groceryLists = new ArrayList<>();
        Log.d(TAG, "ReadFromXML: BeforeTry");
        try {
            Log.d(TAG, "ReadFromXML: Start");
            InputStream is = appCompatActivity.openFileInput(filename);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            InputStreamReader isr = new InputStreamReader(is);
            xmlPullParser.setInput(isr);

            while(xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                if(xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                {
                    if(xmlPullParser.getName().equals("grocery"))
                    {
                        int id = Integer.parseInt(xmlPullParser.getAttributeValue(null,"id"));
                        String Description = xmlPullParser.getAttributeValue(null,"Description");
                        boolean IsOnShoppingList = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "IsOnShoppingList"));
                        boolean IsInCart = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "IsInCart"));

                        GroceryList groceryList = new GroceryList(id, Description, IsOnShoppingList, IsInCart);
                        groceryLists.add(groceryList);
                        Log.d(TAG, "ReadFromXML: End" + groceryList.toString());
                    }
                }
                xmlPullParser.next();
            }

        } catch (Exception e)
        {
            Log.e(TAG, "ReadFromXML: " + e.getMessage() );
        }

        Log.d(TAG, "ReadFromXML: End");
        return groceryLists;
    }

    public void writeXMLFile(String filename, AppCompatActivity appCompatActivity, ArrayList<GroceryList> groceryList)
    {
        Log.d(TAG, "writeXMLFile: Start");

        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(filename);
        try {
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(appCompatActivity.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE));

            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","actors");
            serializer.attribute("","number", String.valueOf(groceryList.size()));

            for(GroceryList groceryList1: groceryList)
            {
                serializer.startTag("","actor");
                serializer.attribute("","id", String.valueOf(groceryList1.getId()));

                serializer.attribute("","Description", String.valueOf(groceryList1.getDescription()));
                serializer.attribute("","IsOnShoppingList", String.valueOf(groceryList1.getInCart()));
                serializer.attribute("","IsInCart", String.valueOf(groceryList1.getOnShoppingList()));

                serializer.endTag("","actor");

                Log.d(TAG, "writeXMLFile: " + groceryList.toString());
            }

            serializer.endTag("","actors");
            serializer.endDocument();
            serializer.flush();
            writer.close();
            Log.d(TAG, "writeXMLFile: " + groceryList.size() + "actors written");

        } catch (Exception e)
        {
            Log.d(TAG, "writeXMLFile: " + e.getMessage());
        }

        Log.d(TAG, "writeXMLFile: End");
    }

    public void writeFile(String filename, AppCompatActivity appCompatActivity, String[] items)
    {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(appCompatActivity.openFileOutput(filename,
                    Context.MODE_PRIVATE));

            String line;
            for(Integer counter = 0; counter < items.length; counter++)
            {
                line = items[counter];
                if(counter<items.length-1)
                {
                    line += "r\n";
                    writer.write(line);
                    Log.d(TAG, "writeFile: ");
                }
                writer.close();
            }

        } catch (Exception e)
        {
            Log.d(TAG, "writeFile: " + e.getMessage());
        }

    }

    public ArrayList<String> readFile(String filename, AppCompatActivity appCompatActivity)
    {
        ArrayList<String> items = new ArrayList<String>();
        try
        {
            InputStream is = appCompatActivity.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                items.add(line);
            }
            is.close();

        } catch(Exception e)
        {
            Log.d(TAG, "readFile: " + e.getMessage());
        }

        return items;

    }
}
