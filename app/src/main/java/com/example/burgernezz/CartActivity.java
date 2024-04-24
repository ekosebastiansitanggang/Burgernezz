package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.RVAdapter.CartRVAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    RecyclerView cartRecycleView;
    ProgressBar progressBar;
    Button btn_deleteall, btn_purchase, btn_orderagain;
    private ArrayList<Cart> cartArrayList;
    CartRVAdapter cartRVAdapter;
    TextView inputTotalPrice;
    Integer totalPrice;
    String TXTotalPrice, shrdInvoiceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //get Action Bar
        getSupportActionBar().setTitle("Keranjang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_shopping_cart_24);
        // get Shared Preferences Email
        sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        // get Firestore Instances
        firestore = FirebaseFirestore.getInstance();
        // get View Elements
        btn_orderagain = findViewById(R.id.btn_orderagain);
        inputTotalPrice = findViewById(R.id.input_totalprice);
        cartRecycleView = findViewById(R.id.cartRecycleView);
        btn_deleteall = findViewById(R.id.btn_removeall);
        btn_purchase = findViewById(R.id.btn_purchase);
        progressBar = findViewById(R.id.idProgressBar);
        // getSharedPreferencesEmail
        shrdInvoiceID = getSharedPreferences();
        // cartRecycleView
        cartArrayList = new ArrayList<>();
        cartRecycleView.setHasFixedSize(true);
        cartRecycleView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        cartRVAdapter = new CartRVAdapter(cartArrayList,CartActivity.this, shrdInvoiceID);
        cartRecycleView.setAdapter(cartRVAdapter);
        // getRecycleView
        getRecycleView();
        // Order Again
        btn_orderagain.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this,MainActivity.class);
            startActivity(intent);
        });
        // Delete All Menu
        btn_deleteall.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete AllData");
            builder.setMessage("Are you sure to delete all data?");
            builder.setIcon(R.drawable.ic_baseline_remove_shopping_cart_black_24);
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                dialogInterface.cancel();
                Toast.makeText(CartActivity.this, "Keranjang dihapus", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this,MainActivity.class);
                startActivity(intent);
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
        // Purchase Product
        btn_purchase.setOnClickListener(view -> {
            if (cartArrayList.isEmpty()){
                Toast.makeText(this, "Keranjang Kosong", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("TotalPrice", totalPrice);
                startActivity(intent);
            }
        });

    }

    public String getSharedPreferences(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        return sharedPreferences.getString("InvoiceID", "");
    }

    private void getRecycleView(){
        final Integer[] price = {0};
        final Integer[] getPrevPrice = new Integer[1];
        String initTotalPrice = "Rp "+ price[0];
        inputTotalPrice.setText(initTotalPrice);
        firestore.collection("Order").document(shrdInvoiceID).collection("Cart")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                progressBar.setVisibility(View.GONE);
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list){
                    Cart c = d.toObject(Cart.class);
                    c.setCartID(d.getId());
                    getPrevPrice[0] = price[0];
                    price[0] = getPrevPrice[0] +(+c.getPrice());
                    cartArrayList.add(c);
                }
                String stringTotalPrice = "Rp "+ price[0];
                totalPrice = price[0];
                inputTotalPrice.setText(stringTotalPrice);
                cartRVAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.GONE);
                String stringTotalPrice = "Rp "+ price[0];
                inputTotalPrice.setText(stringTotalPrice);
                Toast.makeText(CartActivity.this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
                    String stringTotalPrice = "Rp "+ price[0];
                    inputTotalPrice.setText(stringTotalPrice);
            Toast.makeText(CartActivity.this, "Gagal mendapatkan keranjang", Toast.LENGTH_SHORT).show();
        });
    }

}