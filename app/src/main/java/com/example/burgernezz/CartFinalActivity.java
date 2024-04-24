package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.RVAdapter.FinalCartRVAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFinalActivity extends AppCompatActivity {

    RecyclerView cartFinalListView;
    FinalCartRVAdapter finalCartRVAdapter;
    TextView cartFinalTXTInvoiceID;
    private ArrayList<Cart> cartFinalList;
    String cartFinalInvoiceID;
    ProgressBar progressBar;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_final);
        getSupportActionBar().setTitle("Keranjang Akhir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firestore = FirebaseFirestore.getInstance();
        cartFinalTXTInvoiceID = findViewById(R.id.cartFinalInvoice);
        cartFinalListView = findViewById(R.id.cartFinalListView);
        progressBar = findViewById(R.id.idProgressBar);
        cartFinalList = new ArrayList<>();
        cartFinalListView.setHasFixedSize(true);
        cartFinalListView.setLayoutManager(new LinearLayoutManager(CartFinalActivity.this));
        finalCartRVAdapter = new FinalCartRVAdapter(cartFinalList,CartFinalActivity.this);
        cartFinalListView.setAdapter(finalCartRVAdapter);
        getInvoiceID();
    }

    private void getInvoiceID() {
        Intent previous_intent = getIntent();
        if (previous_intent.hasExtra("get_id")){
            String cartFinalInvoiceID = previous_intent.getExtras().getString("get_id");
            if (cartFinalInvoiceID.isEmpty()){
                Toast.makeText(CartFinalActivity.this, "Tidak ada bon", Toast.LENGTH_SHORT).show();
                cartFinalTXTInvoiceID.setVisibility(View.INVISIBLE);
                cartFinalListView.setVisibility(View.VISIBLE);
            } else {
                cartFinalTXTInvoiceID.setVisibility(View.VISIBLE);
                cartFinalTXTInvoiceID.setText(cartFinalInvoiceID);
                getCartFinalListView(cartFinalInvoiceID);
            }
        } else {
            Toast.makeText(CartFinalActivity.this, "Tidak ada bon", Toast.LENGTH_SHORT).show();
            cartFinalTXTInvoiceID.setVisibility(View.INVISIBLE);
            cartFinalListView.setVisibility(View.INVISIBLE);
        }

    }

    private void getCartFinalListView(String cartFinalInvoiceID) {
        firestore.collection("Order").document(cartFinalInvoiceID).collection("Cart")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        progressBar.setVisibility(View.GONE);
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list){
                            Cart c = d.toObject(Cart.class);
                            c.setCartID(d.getId());
                            cartFinalList.add(c);
                        }
                        finalCartRVAdapter.notifyDataSetChanged();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CartFinalActivity.this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CartFinalActivity.this, "Gagal mendapatkan keranjang", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}