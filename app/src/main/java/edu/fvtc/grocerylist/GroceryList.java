package edu.fvtc.grocerylist;

import android.graphics.Bitmap;

public class GroceryList {
    private int Id;
    private String Description;
    private Boolean IsOnShoppingList;
    private Boolean IsInCart;
    private String isOwner;

    public GroceryList(int id, String description, Boolean IsOnShoppingList, Boolean IsInCart)
    {
        this.Id = id;
        this.Description = description;
        this.IsOnShoppingList = IsOnShoppingList;
        this.IsInCart = IsInCart;
    }

    public GroceryList()
    {
        this.Id = -1;
        this.Description = "";
        this.IsOnShoppingList = false;
        this.IsInCart = false;
    }

    private Bitmap photo;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String toString()
    {
        return Id + "|" + Description;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean getOnShoppingList() {
        return IsOnShoppingList;
    }

    public void setOnShoppingList(Boolean onShoppingList) {
        IsOnShoppingList = onShoppingList;
    }

    public Boolean getInCart() {
        return IsInCart;
    }

    public void setInCart(Boolean inCart) {
        IsInCart = inCart;
    }

    public void setControlText(int controlId, String value)
    {
        if(controlId == R.id.etEditItem)
        {
            this.setDescription(value);
        }

    }

    public String getOwner() { return isOwner; }

    public void setIsOwner() {isOwner = isOwner; }

}
