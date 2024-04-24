package com.example.burgernezz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.example.burgernezz.CartActivity;
import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CartAdapter extends SimpleAdapter {
    SharedPreferences sharedPreferences;
    Context context;
    ArrayList<HashMap<String, String>> cartList;
    DBHelper DB;
    String shrdEmail;

    public CartAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.cartList=data;
        LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent){
        DB = new DBHelper(context);
        shrdEmail = getSharedPreferences();
        View view = super.getView(position,convertView,parent);
        TextView TXTquantity = view.findViewById(R.id.cartQuantity);
        TextView TXTprice = view.findViewById(R.id.cartPrice);
        ImageButton cartDecrease = view.findViewById(R.id.cartDecrease);
        ImageButton cartIncrease = view.findViewById(R.id.cartIncrease);
        Button btn_remove = view.findViewById(R.id.btn_removeone);

        cartDecrease.setOnClickListener(view1 -> {
            String product = cartList.get(position).get("product");
            int quantity = Integer.parseInt(cartList.get(position).get("quantity"));
            int price = Integer.parseInt(cartList.get(position).get("price"));
            int singlePrice = price/quantity;
            if (quantity <= 1) { quantity = 1; } else {
                quantity--; }
            Integer setPrice = singlePrice*quantity;
            DB.updateCart(shrdEmail, product,quantity,setPrice);
            this.notifyDataSetChanged();
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });

        cartIncrease.setOnClickListener(view13 -> {
            String product = cartList.get(position).get("product");
            int quantity = Integer.parseInt(cartList.get(position).get("quantity"));
            int price = Integer.parseInt(cartList.get(position).get("price"));
            int singlePrice = price/quantity;
            if (quantity >= 10) { quantity = 10; } else {
                quantity++; }
            Integer setPrice = singlePrice*quantity;
            DB.updateCart(shrdEmail, product,quantity,setPrice);
            this.notifyDataSetChanged();
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });

        btn_remove.setOnClickListener(view12 -> {
            String product = cartList.get(position).get("product");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Data");
            builder.setMessage("Are you sure to delete "+product+"?");
            builder.setIcon(R.drawable.ic_baseline_remove_shopping_cart_black_24);
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                DB.deleteProduct(shrdEmail, product);
                cartList.remove(product);
                dialogInterface.cancel();
                notifyDataSetChanged();
                Intent intent = new Intent(context, CartActivity.class);
                context.startActivity(intent);
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        return view;
    }

    public String getSharedPreferences(){
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences("UserEmail", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

}
