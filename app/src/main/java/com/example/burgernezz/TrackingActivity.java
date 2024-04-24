package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.Models.Invoice;
import com.example.burgernezz.RVAdapter.InvoiceRVAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrackingActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    RecyclerView trackingRecycleView;
    ArrayList<Invoice> invoiceArrayList;
    InvoiceRVAdapter invoiceRVAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        getSupportActionBar().setTitle("Tracking Invoice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firestore = FirebaseFirestore.getInstance();
        trackingRecycleView = findViewById(R.id.trackingRecycleView);
        progressBar = findViewById(R.id.idProgressBar);
        invoiceArrayList = new ArrayList<>();
        trackingRecycleView.setHasFixedSize(true);
        trackingRecycleView.setLayoutManager(new LinearLayoutManager(TrackingActivity.this));
        invoiceRVAdapter = new InvoiceRVAdapter(invoiceArrayList,TrackingActivity.this);
        trackingRecycleView.setAdapter(invoiceRVAdapter);
        spawnOrder();
    }

    public void spawnOrder() {
        String shrdEmail = getSharedPreferences();
        CollectionReference collectionReference = firestore.collection("Order");
        Query query = collectionReference.whereEqualTo("email", shrdEmail);
        query.get().addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(TrackingActivity.this, "Tidak bisa terhubung", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                progressBar.setVisibility(View.GONE);
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list){
                    Invoice i = d.toObject(Invoice.class);
                    i.setInvoiceID(d.getId());
                    invoiceArrayList.add(i);
                }
                invoiceRVAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(TrackingActivity.this, "Belum ada pesanan yang diproses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getSharedPreferences(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }
}