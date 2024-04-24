package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.Models.Invoice;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PurchaseActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    TextView textTotalPrice;
    EditText inputEmail, inputPhone, inputAddress;
    CheckBox checkBandung;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button btnConfirmPayment;
    Calendar calendar;
    SimpleDateFormat dateFormat, timeFormat;
    private ArrayList<HashMap<String, String>> cartList;
    String date, time, DateTime, confirmID, paymentID, status, shrdEmail;
    String confirmEmail, confirmPhone, confirmAddress, confirmPayment, confirmTotalPrice;
    Integer retrieveTotalPrice;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dbHelper = new DBHelper(this);
        textTotalPrice = findViewById(R.id.retrieveTotalPayment);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputAddress = findViewById(R.id.inputAddress);
        checkBandung = findViewById(R.id.checkBandung);
        radioGroup = findViewById(R.id.paymentGroup);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        retrieveTotalPrice = dbHelper.getTotalPrice();
        confirmTotalPrice = "Rp "+retrieveTotalPrice;
        textTotalPrice.setText(confirmTotalPrice);
        cartList = dbHelper.getAllCart();
        getprevIntent();
        btnConfirmPayment.setOnClickListener(view -> {
            int selectedID = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(selectedID);
            confirmPayment = radioButton.getText().toString();
        });
    }

    public void setConfirmPayment(){
        confirmEmail = inputEmail.getText().toString();
        confirmPhone = inputPhone.getText().toString();
        confirmAddress = inputAddress.getText().toString();
        if (confirmEmail.isEmpty() || !confirmEmail.contains("@")){
            inputEmail.performClick();
            Toast.makeText(this, "Email belum terisi dengan benar", Toast.LENGTH_SHORT).show();
        } else if (confirmPhone.isEmpty()) {
            inputPhone.performClick();
            Toast.makeText(this, "No HP belum terisi", Toast.LENGTH_SHORT).show();
        } else if (confirmAddress.isEmpty() || !confirmAddress.contains("Bandung") || !checkBandung.isChecked()){
            inputAddress.performClick();
            Toast.makeText(this, "Alamat belum terisi dengan benar, hanya melayani kota Bandung", Toast.LENGTH_SHORT).show();
        } else {
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            timeFormat = new SimpleDateFormat("HH:mm:ss");
            date = dateFormat.format(calendar.getTime());
            time = timeFormat.format(calendar.getTime());
            DateTime = date+"-"+time;
            confirmID = confirmEmail+"-"+date+"-"+time;
            AlertWarning();
        }

    }

    public void getprevIntent(){
        Intent prev_intent = getIntent();
        shrdEmail = prev_intent.getStringExtra("getShrdEmail");
        if (!shrdEmail.isEmpty()){
            inputEmail.setText(shrdEmail);
        } else {
            inputEmail.performClick();
        }
    }

    public void DialogPaymentID(){
        Dialog dialog = new Dialog(PurchaseActivity.this);
        dialog.setContentView(R.layout.dialog_enteremail);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        EditText paymentDialogue_inputID = dialog.findViewById(R.id.paymentDialog_inputID);
        TextView paymentDialog_warning = dialog.findViewById(R.id.paymentDialog_warning);
        Button emailDialog_btnInput = dialog.findViewById(R.id.emailDialog_btnInput);
        Button emailDialog_btnCancel = dialog.findViewById(R.id.emailDialog_btnCancel);
        emailDialog_btnCancel.setOnClickListener(view -> dialog.dismiss());
        emailDialog_btnInput.setOnClickListener(view -> {
            String paymentID_inputted = paymentDialogue_inputID.getText().toString();
            if (paymentID_inputted.isEmpty()){
                paymentDialog_warning.setText(R.string.dialogue_warning);
            } else {
                status = "Sudah Bayar";
                paymentID = paymentID_inputted;
                AlertWarning();
            }
        });
        dialog.show();
    }

    public void AlertWarning(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
        builder.setTitle("Peringatan Pembayaran");
        builder.setIcon(R.drawable.ic_baseline_warning_24);
        builder.setMessage("Setelah anda melakukan pembayaran, anda tidak dapat mengubah pesanan lagi. \n Lanjutkan?");
        builder.setCancelable(false);
        builder.setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton("Ya", (dialogInterface, i) -> InsertToInvoice());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void InsertToInvoice(){
        firestore = FirebaseFirestore.getInstance();
        dbHelper.insertInVoice1(confirmID,confirmEmail,confirmPhone,confirmAddress,retrieveTotalPrice, DateTime, confirmPayment, paymentID, status);
        dbHelper.updateCartInvoice(shrdEmail,confirmID);
        Intent intent = new Intent(PurchaseActivity.this,TrackingActivity.class);
        startActivity(intent);
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