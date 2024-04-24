package com.example.burgernezz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.generate.RandomInvoiceID;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    FirebaseFirestore firestore;
    List<String> foodNameList;
    List<Integer> foodPriceList;
    String[] product_types;
    String currency, selectedTypes, shrdInvoiceID, getInvoiceID, dataTypes;
    Integer[] productPrice;
    Integer price = 0, selectedProductPrice = 0;
    int quantity = 1;
    TextView inputQuantity, inputPrice, text_product, desc_product, desc_cheese;
    ImageView img_product;
    Spinner spinner_product;
    ImageButton quantityDecrease, quantityIncrease;
    Button btn_order;
    Intent intent;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        DB = new DBHelper(this);
        firestore = FirebaseFirestore.getInstance();
        foodNameList = new ArrayList<>();
        foodPriceList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        img_product = findViewById(R.id.img_product);
        text_product = findViewById(R.id.text_product);
        spinner_product = findViewById(R.id.spinner_product);
        desc_product = findViewById(R.id.desc_product);
        desc_cheese = findViewById(R.id.desc_cheese);
        inputQuantity = findViewById(R.id.inputQuantity);
        inputPrice = findViewById(R.id.price_product);
        quantityDecrease = findViewById(R.id.quantityDecrease);
        quantityIncrease = findViewById(R.id.quantityIncrease);
        btn_order = findViewById(R.id.btn_order);
        getIntentAndProduct();
        spinner_product.setOnItemSelectedListener(this);
        ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, product_types);
        productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product.setAdapter(productTypeAdapter);
        selectedTypes = product_types[0];
        selectedProductPrice = productPrice[0];
        calculatePrice();

        quantityDecrease.setOnClickListener(View -> decreaseQuantity());
        quantityIncrease.setOnClickListener(View -> increaseQuantity());
        btn_order.setOnClickListener(view -> {
            shrdInvoiceID = getSharedPreferences();
            if (shrdInvoiceID.isEmpty()){
                GenerateRandomID();
            } else {
                String inputedProduct = selectedTypes;
                Integer inputedQuantity = quantity;
                Integer inputedPrice = price;
                InsertToCart(inputedProduct, inputedQuantity,inputedPrice);
            }
        });
    }

    public void decreaseQuantity () {
        if (quantity <= 1) { quantity = 1; } else {
            quantity--; }
        String setQuantity = ""+quantity;
        inputQuantity.setText(setQuantity);
        calculatePrice();
    }

    public void increaseQuantity () {
        if (quantity >= 10) { quantity = 10; } else {
            quantity++; }
        String setQuantity = ""+quantity;
        inputQuantity.setText(setQuantity);
        calculatePrice();
    }

    private void calculatePrice (){
        price = quantity*selectedProductPrice;
        currency = "Rp "+price+"";
        inputPrice.setText(currency);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        selectedTypes = product_types[position];
        selectedProductPrice = productPrice[position];
        if (selectedTypes.contains("Keju Telor")){
            desc_cheese.setText(R.string.desc_cheeseegg);
            desc_cheese.setVisibility(View.VISIBLE);
        } else if (selectedTypes.contains("Keju") && !selectedTypes.contains("Telor")){
            desc_cheese.setText(R.string.desc_cheese);
            desc_cheese.setVisibility(View.VISIBLE);
        } else {
            desc_cheese.setVisibility(View.INVISIBLE);
        }
        calculatePrice();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void InsertToCart(String inputedProduct, Integer inputedQuantity, Integer inputedPrice){
        CollectionReference collectionReference = firestore.collection("Order").document(shrdInvoiceID).collection("Cart");
        Cart cart = new Cart(inputedProduct, inputedQuantity,inputedPrice);
        collectionReference.add(cart).addOnFailureListener(e ->
                Toast.makeText(ProductActivity.this,"Gagal menambahkan produk",Toast.LENGTH_SHORT).show())
            .addOnSuccessListener(documentReference -> {
            DB.insertCart(shrdInvoiceID,inputedProduct,inputedQuantity,inputedPrice);
            Toast.makeText(ProductActivity.this, "Produk telah dimasukan", Toast.LENGTH_SHORT).show();
            intent = new Intent(ProductActivity.this,CartActivity.class);
            startActivity(intent);
        });
    }

    public String getSharedPreferences(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        getInvoiceID = sharedPreferences.getString("InvoiceID", "");
        if (getInvoiceID.isEmpty()){
            GenerateRandomID();
        }
        return getInvoiceID;
    }

    private void GenerateRandomID (){
        RandomInvoiceID randomInvoiceID = new RandomInvoiceID();
        shrdInvoiceID = randomInvoiceID.generateInvoiceID(20);
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("InvoiceID",shrdInvoiceID);
        sharedPreferencesEditor.commit();
    }

    private void getProductandPrice (){
        CollectionReference collectionReference = firestore.collection("Food");
        Query foodQuery = collectionReference.whereEqualTo("type", dataTypes);
        foodQuery.get().addOnFailureListener(e -> {
            Toast.makeText(ProductActivity.this, "Tidak bisa terhubung", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }).addOnSuccessListener(queryFoodSnapshot -> {
            product_types = new String[]{};
            productPrice = new Integer[]{};
        });
    }

    private void getIntentAndProduct (){
        Intent previous_intent = getIntent();
        Integer getProduct = previous_intent.getIntExtra("getProduct",0);
        if (getProduct.equals(0)){
            img_product.setImageResource(R.drawable.kebab);
            text_product.setText(R.string.text_kebab);
            desc_product.setText(R.string.desc_kebab_ori);
            getSupportActionBar().setTitle(R.string.text_kebab);
            dataTypes = "Kebab";
            product_types = new String[]{"Kebab Original", "Kebab Keju"};
            productPrice = new Integer[]{10000, 13000};
        } else if (getProduct.equals(1)){
            img_product.setImageResource(R.drawable.sosis_bakar);
            text_product.setText(R.string.text_sosis);
            desc_product.setText(R.string.desc_sosis);
            getSupportActionBar().setTitle(R.string.text_sosis);
            dataTypes = "Sosis";
            product_types = new String[]{"Sosis Goreng Mayonnaise"};
            productPrice = new Integer[]{8000};
        } else if (getProduct.equals(2)){
            img_product.setImageResource(R.drawable.burger_ayam);
            text_product.setText(R.string.text_chickenburger);
            desc_product.setText(R.string.desc_chickenburger);
            getSupportActionBar().setTitle(R.string.text_chickenburger);
            dataTypes = "Burger Ayam";
            product_types = new String[]{"Burger Ayam", "Burger Ayam Keju"};
            productPrice = new Integer[]{10000, 12000};
        } else if (getProduct.equals(3)){
            img_product.setImageResource(R.drawable.beef_burger);
            text_product.setText(R.string.text_beefburger);
            desc_product.setText(R.string.desc_beefburger);
            getSupportActionBar().setTitle(R.string.text_beefburger);
            dataTypes = "Burger Sapi";
            product_types = new String[]{"Burger Sapi", "Burger Sapi Keju", "Burger Sapi Keju Telor"};
            productPrice = new Integer[]{10000, 12000, 15000};
        } else if (getProduct.equals(4)){
            img_product.setImageResource(R.drawable.hotdog);
            text_product.setText(R.string.text_hotdog);
            desc_product.setText(R.string.desc_hotdog);
            getSupportActionBar().setTitle(R.string.text_hotdog);
            dataTypes = "Hot Dog";
            product_types = new String[]{"Hot Dog"};
            productPrice = new Integer[]{11000};
        } else {
            img_product.setImageResource(R.drawable.kebab);
            text_product.setText(R.string.text_kebab);
            getSupportActionBar().setTitle(R.string.text_kebab);
            dataTypes = "Kebab";
            product_types = new String[]{"Kebab Original", "Kebab Keju"};
            productPrice = new Integer[]{10000, 13000};
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}