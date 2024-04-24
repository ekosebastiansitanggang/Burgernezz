package com.example.burgernezz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burgernezz.Models.Invoice;
import com.example.burgernezz.generate.RandomInvoiceID;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    EditText inputEmail, inputPhone, inputAddress;
    TextView retrieveTotalPayment;
    CheckBox checkBandung;
    RadioGroup paymentGroup;
    RadioButton paymentButton;
    Button btnConfirmPayment;
    Calendar calendar;
    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    private ArrayList<String> invoiceArrayList;
    String stringTotalPayment, stringEmail, stringPhone, stringAddress, stringPaymentType, stringPaymentID;
    String stringDate, stringTime, stringDateTime, stringStatus, shrdInvoiceID, shrdEmail;
    String newInvoiceID;
    Integer integerTotalPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Checkout Pembayaran");
        firestore = FirebaseFirestore.getInstance();
        loadViewByID();
        shrdInvoiceID = getSharedInvoiceID();
        shrdEmail = getSharedPreferences();
        if (!shrdEmail.isEmpty()){
            inputEmail.setText(shrdEmail);
        }
        getTotalPrice();
        btnConfirmPayment.setOnClickListener(view -> {
            stringEmail = inputEmail.getText().toString();
            stringPhone = inputPhone.getText().toString();
            stringAddress = inputAddress.getText().toString();
            if (stringEmail.isEmpty()){
                inputEmail.performClick();
                Toast.makeText(this, "Email kosong, harap diisi.", Toast.LENGTH_SHORT).show();
            } else if (stringPhone.isEmpty()){
                inputPhone.performClick();
                Toast.makeText(this, "No HP kosong, harap diisi.", Toast.LENGTH_SHORT).show();
            }
            else if (stringAddress.isEmpty()){
                inputAddress.performClick();
                Toast.makeText(this, "Alamat kosong, harap diisi.", Toast.LENGTH_SHORT).show();
            } else if (checkBandung.isChecked() && stringAddress.contains("Bandung")){
                getPaymentTypes();
            } else {
                inputAddress.performClick();
                Toast.makeText(this, "Hanya melayani kota Bandung.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void InsertIntoInvoice(){
        calendar = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        stringDate = dateFormat.format(calendar.getTime());
        stringTime = timeFormat.format(calendar.getTime());
        stringDateTime = stringDate+"-"+stringTime;
        DocumentReference documentReference = firestore.collection("Order").document(shrdInvoiceID);
        Invoice invoice = new Invoice(shrdInvoiceID,stringEmail,stringPhone,stringAddress,integerTotalPayment,stringDateTime,stringPaymentType,stringPaymentID,stringStatus);
        documentReference.set(invoice).addOnFailureListener(e ->
                Toast.makeText(CheckoutActivity.this, "Gagal mengirimkan data", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(unused -> {
                    GenerateRandomID();
                    Intent intent = new Intent(CheckoutActivity.this,TrackingActivity.class);
                    startActivity(intent);
                });

    }

    private void GenerateRandomID (){
        RandomInvoiceID randomInvoiceID = new RandomInvoiceID();
        newInvoiceID = randomInvoiceID.generateInvoiceID(20);
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("InvoiceID",newInvoiceID);
        sharedPreferencesEditor.commit();
    }

    public void getPaymentTypes(){
        if (paymentGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(this, "Metode pembayaran belum dipilih", Toast.LENGTH_SHORT).show();
        } else {
            Integer selectedPaymentType = paymentGroup.getCheckedRadioButtonId();
            paymentButton = paymentGroup.findViewById(selectedPaymentType);
            stringPaymentType = paymentButton.getText().toString();
            if (selectedPaymentType.equals(R.id.payment1)){
                stringPaymentID = "";
                stringStatus = "Sudah Pesan, belum bayar";
                AlertWarning();
            } else if (selectedPaymentType.equals(R.id.payment2)){
                DialogBankTransferPayment();
            } else if (selectedPaymentType.equals(R.id.payment3)){
                Toast.makeText(this, "Currently unsupported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void DialogBankTransferPayment(){
        Dialog dialog = new Dialog(CheckoutActivity.this);
        dialog.setContentView(R.layout.dialog_enterpaymentid);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView paymentDialog_TXTHowtoPay = dialog.findViewById(R.id.paymentDialog_TXTHowtoPay);
        EditText paymentDialogue_inputID = dialog.findViewById(R.id.paymentDialog_inputID);
        Button emailDialog_btnInput = dialog.findViewById(R.id.paymentDialog_btnInput);
        Button emailDialog_btnCancel = dialog.findViewById(R.id.paymentDialog_btnCancel);
        String textPayment1 = getString(R.string.paymentdialogue_Tutorial1);
        String textPayment2 = getString(R.string.paymentdialogue_Tutorial2);
        String textPayment = textPayment1 + " Rp "+ integerTotalPayment + " " + textPayment2;
        paymentDialog_TXTHowtoPay.setText(textPayment);
        emailDialog_btnCancel.setOnClickListener(view -> dialog.dismiss());
        emailDialog_btnInput.setOnClickListener(view -> {
            String paymentID_inputted = paymentDialogue_inputID.getText().toString();
            if (paymentID_inputted.isEmpty()){
                paymentDialogue_inputID.setError("Pembayaran tidak diinput dengan benar");
            } else {
                stringStatus = "Sudah Bayar, belum di check";
                stringPaymentID = paymentID_inputted;
                dialog.dismiss();
                AlertWarning();
            }
        });
        dialog.show();
    }

    public void AlertWarning(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Peringatan Pembayaran");
        builder.setIcon(R.drawable.ic_baseline_warning_24);
        builder.setMessage("Setelah anda melakukan pembayaran, anda tidak dapat mengubah pesanan lagi. \n Lanjutkan?");
        builder.setCancelable(false);
        builder.setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setPositiveButton("Yes", (dialogInterface, i) -> InsertIntoInvoice());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getTotalPrice (){
        Intent prev_intent = getIntent();
        integerTotalPayment = prev_intent.getIntExtra("TotalPrice",0);
        if (integerTotalPayment.toString().isEmpty() || integerTotalPayment.equals(0)){
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CheckoutActivity.this,CartActivity.class);
            startActivity(intent);
        } else {
            stringTotalPayment = "Rp " + integerTotalPayment;
            retrieveTotalPayment.setText(stringTotalPayment);
            btnConfirmPayment.setVisibility(View.VISIBLE);
            btnConfirmPayment.setClickable(true);
            checkBandung.setClickable(true);
            checkBandung.setChecked(false);
        }
    }

    public void loadViewByID(){
        //Card for Total Price and it's component
        retrieveTotalPayment = findViewById(R.id.retrieveTotalPayment);
        //Card for Input Identity and it's component
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputAddress = findViewById(R.id.inputAddress);
        checkBandung = findViewById(R.id.checkBandung);
        checkBandung.setClickable(false);
        //Card for Payment (Radio Group and Radio Button)
        paymentGroup = findViewById(R.id.paymentGroup);
        //Button
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnConfirmPayment.setClickable(false);
        btnConfirmPayment.setVisibility(View.INVISIBLE);
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