package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burgernezz.Adapter.MenuAdapter;
import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.generate.RandomInvoiceID;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    TextView txt_navEmail;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    GridView menuGridView;
    Intent intent;
    String getInvoiceID, setInvoiceID;
    String[] menuName = {"Kebab", "Sosis Goreng", "Burger Ayam", "Burger Sapi", "Hotdog"};
    String[] menuPrice = {"Rp 10.000", "Rp 8.000", "Rp 10.000", "Rp 10.000", "Rp 11.000"};
    int[] menuImage = {R.drawable.kebab, R.drawable.sosis_bakar, R.drawable.burger_ayam,
    R.drawable.beef_burger, R.drawable.hotdog};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigationDrawer();
        sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setTitle("Burgernezz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.burgernezz_logo);

        getInvoiceID = getSharedInvoiceID();
        if (getInvoiceID.isEmpty()){
            GenerateRandomID();
        }

        //GridView
        menuGridView = findViewById(R.id.menu_gridview);
        MenuAdapter menuAdapter = new MenuAdapter(MainActivity.this,menuName,menuPrice,menuImage);
        menuGridView.setAdapter(menuAdapter);

        menuGridView.setOnItemClickListener((adapterView, view, position, l) -> {
            intent = new Intent(MainActivity.this,ProductActivity.class);
            intent.putExtra("getProduct",position);
            startActivity(intent);
        });


    }

    private void NavigationHeaderAccount(){
        View headerView = navigationView.getHeaderView(0);
        txt_navEmail = headerView.findViewById(R.id.nav_theEmail);
        String shrdEmail = getSharedPreferences();
        txt_navEmail.setText(shrdEmail);
    }

    private void initNavigationDrawer() {
        navigationView = findViewById(R.id.navigation_view);
        NavigationHeaderAccount();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.nav_cart:
                    intent = new Intent(MainActivity.this,CartActivity.class);
                    MainActivity.this.startActivity(intent);
                    break;
                case R.id.nav_tracking:
                    intent = new Intent(MainActivity.this,TrackingActivity.class);
                    MainActivity.this.startActivity(intent);
                    break;
                case R.id.nav_about:
                    intent = new Intent(MainActivity.this,AboutActivity.class);
                    MainActivity.this.startActivity(intent);
                    break;
                case R.id.nav_editEmail:
                    EmailDialogue();
                    drawerLayout.closeDrawers();
                    break;
                default:
                    drawerLayout.closeDrawers();
                    break;
            }
            return true;
        });
    }

    public void EmailDialogue(){
        String shrdEmail = getSharedPreferences();
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_enteremail);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        EditText emailDialog_inputEmail = dialog.findViewById(R.id.emailDialog_inputEmail);
        TextView emailDialog_warning = dialog.findViewById(R.id.emailDialog_warning);
        Button emailDialog_btnInput = dialog.findViewById(R.id.emailDialog_btnInput);
        Button emailDialog_btnCancel = dialog.findViewById(R.id.emailDialog_btnCancel);
        emailDialog_inputEmail.setText(shrdEmail);
        emailDialog_btnCancel.setOnClickListener(view -> dialog.dismiss());
        emailDialog_btnInput.setOnClickListener(view -> {
            String emailDialog_inputted = emailDialog_inputEmail.getText().toString();
            if (emailDialog_inputted.isEmpty() || !emailDialog_inputted.contains("@")){
                 emailDialog_warning.setText(R.string.dialogue_warning);
            } else {
                DBHelper dbHelper = new DBHelper(this);
                dbHelper.updateCartInvoice(shrdEmail,emailDialog_inputted);
                setSharedPreferences(emailDialog_inputted);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String getSharedPreferences(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    public String getSharedInvoiceID(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        return sharedPreferences.getString("InvoiceID", "");
    }

    public void setSharedPreferences(String emailDialog_inputted){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("Email",emailDialog_inputted);
        sharedPreferencesEditor.commit();
        txt_navEmail.setText(emailDialog_inputted);
    }

    private void GenerateRandomID (){
        RandomInvoiceID randomInvoiceID = new RandomInvoiceID();
        setInvoiceID = randomInvoiceID.generateInvoiceID(20);
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("InvoiceID",setInvoiceID);
        sharedPreferencesEditor.commit();
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}