package edu.fvtc.grocerylist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter {

    public static final String TAG = "Adapter";

    private ArrayList<GroceryList> groceryList;

    private View.OnClickListener onItemClickListener;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private Context parentContext;

    private boolean isDeleting;

    public class GroceryViewAdapterHolder extends RecyclerView.ViewHolder {

    public TextView tvGroceryItem;

    public CheckBox chkItem;

    public ImageButton imageButtonPhoto;

        public GroceryViewAdapterHolder(@NonNull View itemView) {

            super(itemView);
            tvGroceryItem = itemView.findViewById(R.id.tvGroceryItemName);
            chkItem = itemView.findViewById(R.id.chkItem);
            imageButtonPhoto = itemView.findViewById(R.id.imgPhoto);

            // Code involving with click an item in the list.
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);

            chkItem.setTag(this);
            chkItem.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        public TextView getTvGroceryItem()
        {
            return tvGroceryItem;
        }

        public CheckBox getChkItem() { return chkItem; }

        public void setDelete(boolean b)
        {
            isDeleting = b;
        }

        public ImageButton getImageButtonPhoto() {return imageButtonPhoto;}

    }

    public GroceryAdapter(ArrayList<GroceryList> data, Context context)
    {
        groceryList = data;
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener)
    {
        Log.d(TAG, "setOnItemClickListener: ");
        onItemClickListener = itemClickListener;
    }

    public void setOnItemCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener)
    {
        Log.d(TAG, "setOnItemCheckedChangeListener: ");
        onCheckedChangeListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item_view, parent, false);
        return new GroceryViewAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Binding values.
        GroceryViewAdapterHolder groceryViewAdapterHolder = (GroceryViewAdapterHolder) holder;
        groceryViewAdapterHolder.getTvGroceryItem().setText(groceryList.get(position).getDescription());
        groceryViewAdapterHolder.getChkItem().setChecked(groceryList.get(position).getOnShoppingList());

        if(groceryList.get(position).getPhoto() != null)
            groceryViewAdapterHolder.getImageButtonPhoto().setImageBitmap(groceryList.get(position).getPhoto());

        groceryViewAdapterHolder.chkItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }
}
