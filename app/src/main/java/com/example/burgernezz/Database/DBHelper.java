package com.example.burgernezz.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final String TABLE_FOOD = "db_food";
    public static final String TABLE_CART = "db_cart";
    public static final String TABLE_INVOICE = "db_tracking";
    public static final String TABLE_CARTINVOICE = "db_carttracking";
    public static final String KEY_MENU = "menu";
    public static final String KEY_PRODUCT = "product";
    public static final String I_QUANTITY = "jumlah";
    public static final String I_PRICE = "price";
    public static final String I_TOTAL = "total";
    public static final String KEY_INVOICEID = "NoBon";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PHONE = "Phone";
    public static final String S_ADDRESS = "Address";
    public static final String I_PAIDPRICE = "TotalPrice";
    public static final String S_DATEORDER = "DateOrder";
    public static final String S_PAYMENTTYPE = "PaymentType";
    public static final String ID_PAYMENT = "PaymentID";
    public static final String S_STATUS = "Status";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase userDatabase) {
        String CREATE_TABLEFOOD = "CREATE TABLE "+TABLE_FOOD+"("+KEY_PRODUCT+" TEXT PRIMARY KEY, "+KEY_MENU+" TEXT, "+I_PRICE+" INTEGER"+")";
        String CREATE_TABLECART = "CREATE TABLE "+TABLE_CART+"("+KEY_PRODUCT+" TEXT PRIMARY KEY, "+I_QUANTITY+" INTEGER, "+I_TOTAL+" INTEGER"+")";
        String CREATE_TABLEINVOICE = "CREATE TABLE "+TABLE_INVOICE+"("
                +KEY_INVOICEID+" TEXT PRIMARY KEY, "
                +KEY_EMAIL+" TEXT, "
                +KEY_PHONE+" TEXT, "
                +S_ADDRESS+" TEXT, "
                +I_PAIDPRICE+" INTEGER, "
                +S_DATEORDER+" TEXT, "
                +S_PAYMENTTYPE+" TEXT, "
                +ID_PAYMENT+" TEXT ,"
                +S_STATUS+" TEXT"+")";
        String CREATE_TABLECARTINVOICE = "CREATE TABLE "+TABLE_CARTINVOICE+"("+KEY_INVOICEID+" TEXT , "+KEY_PRODUCT+" TEXT, "+I_QUANTITY+" INTEGER, "+I_TOTAL+" INTEGER"+")";
        userDatabase.execSQL(CREATE_TABLEFOOD);
        userDatabase.execSQL(CREATE_TABLECART);
        userDatabase.execSQL(CREATE_TABLEINVOICE);
        userDatabase.execSQL(CREATE_TABLECARTINVOICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase userDatabase, int i, int i1) {
        userDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_FOOD);
        userDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_CART);
        userDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_INVOICE);
        userDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_CARTINVOICE);
        onCreate(userDatabase);
    }

    //Inserting Cart
    public void insertCart(String dummy, String product, Integer quantity, Integer price) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(KEY_PRODUCT, product);
        contentValues1.put(I_QUANTITY, quantity);
        contentValues1.put(I_TOTAL, price);
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(KEY_INVOICEID, dummy);
        contentValues2.put(KEY_PRODUCT, product);
        contentValues2.put(I_QUANTITY, quantity);
        contentValues2.put(I_TOTAL, price);
        userDatabase.insert(TABLE_CART, null, contentValues1);
        userDatabase.insert(TABLE_CARTINVOICE, null, contentValues2);
        userDatabase.close();
    }

    //Updating Cart
    public void updateCart(String dummy, String product, Integer quantity, Integer price) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(I_QUANTITY, quantity);
        contentValues.put(I_TOTAL, price);
        userDatabase.update(TABLE_CART, contentValues, KEY_PRODUCT + " = ?", new String[]{product});
        userDatabase.update(TABLE_CARTINVOICE, contentValues, KEY_INVOICEID + " = ? AND "+ KEY_PRODUCT + " = ?", new String[]{dummy, product});
        userDatabase.close();
    }


    //Deleting Product
    public void deleteProduct(String dummy, String product) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        userDatabase.delete(TABLE_CART, KEY_PRODUCT + " = ?", new String[]{product});
        userDatabase.delete(TABLE_CARTINVOICE, KEY_INVOICEID + " = ? AND "+ KEY_PRODUCT + " = ?", new String[]{dummy, product});
        userDatabase.close();
    }

    //Deleting Cart
    public void deleteCart(String dummy) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        userDatabase.delete(TABLE_CART, null, null);
        userDatabase.delete(TABLE_CARTINVOICE, KEY_INVOICEID + " = ?", new String[]{dummy});
        userDatabase.close();
    }


    //Get Product
    public Boolean getProduct (String product){
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        Cursor cursor = userDatabase.rawQuery("SELECT * FROM "+TABLE_CART+" WHERE "+KEY_PRODUCT+" = ?", new String[]{product});
        if (cursor.getCount() > 0){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    //Check if product spinner exists
    public Boolean checkIfProductSpinnerExists (){
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        Cursor cursor = userDatabase.query(TABLE_FOOD,null,null,null,null,null,null);
        if (cursor.getCount() > 0){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    //Get Cart List
    public ArrayList<HashMap<String, String>> getAllCart() {
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> cartList = new ArrayList<>();
        String[] columns = new String[]{KEY_PRODUCT, I_QUANTITY, I_TOTAL};
        Cursor cursor = userDatabase.query(TABLE_CART, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> cartData = new HashMap<>();
            cartData.put("product", cursor.getString(0));
            cartData.put("quantity", cursor.getString(1));
            cartData.put("price", cursor.getString(2));
            cartList.add(cartData);
        }
        cursor.close();
        userDatabase.close();
        return cartList;
    }

    //Get Cart List by ID
    public ArrayList<HashMap<String, String>> getAllCartByID(String inVoiceID) {
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> cartList = new ArrayList<>();
        String[] columns = new String[]{KEY_PRODUCT, I_QUANTITY, I_TOTAL};
        Cursor cursor = userDatabase.query(TABLE_CARTINVOICE,columns,KEY_INVOICEID+" = ?",new String[]{inVoiceID},null,null,null);
        while (cursor.moveToNext()) {
            HashMap<String, String> cartData = new HashMap<>();
            cartData.put("product", cursor.getString(0));
            cartData.put("quantity", cursor.getString(1));
            cartData.put("price", cursor.getString(2));
            cartList.add(cartData);
        }
        cursor.close();
        userDatabase.close();
        return cartList;
    }

    //Calculate Total Price
    public int getTotalPrice () {
        int total = 0;
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        Cursor cursor = userDatabase.rawQuery("SELECT SUM ("+I_TOTAL+") FROM "+TABLE_CART+"", null);
        if (cursor.moveToFirst())
        {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    // Insert Invoice
    public void insertInVoice1(String invoiceID, String email, String phone, String address, Integer totalprice, String dateOrder, String paymentType, String paymentID, String status) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(KEY_INVOICEID, invoiceID);
        contentValues1.put(KEY_EMAIL, email);
        contentValues1.put(KEY_PHONE, phone);
        contentValues1.put(S_ADDRESS, address);
        contentValues1.put(I_PAIDPRICE, totalprice);
        contentValues1.put(S_DATEORDER, dateOrder);
        contentValues1.put(S_PAYMENTTYPE, paymentType);
        contentValues1.put(ID_PAYMENT, paymentID);
        contentValues1.put(S_STATUS, status);
        userDatabase.insert(TABLE_INVOICE, null, contentValues1);
        userDatabase.close();
    }

    //Update Cart Invoice ID
    public void updateCartInvoice(String dummy, String newInvoiceID) {
        SQLiteDatabase userDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INVOICEID, newInvoiceID);
        userDatabase.update(TABLE_CARTINVOICE,contentValues,KEY_INVOICEID+" = ?", new String[]{dummy});
        userDatabase.delete(TABLE_CART, null, null);
        userDatabase.close();
    }

    //Get All Invoice ID
    public List<String> getInvoiceID(){
        List<String> getInvoiceID = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = new String[]{KEY_INVOICEID};
        Cursor cursor = sqLiteDatabase.query(TABLE_INVOICE,columns,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                getInvoiceID.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return getInvoiceID;
    }

    //Get All Product Spinner
    public List<String> getAllSpinner (String selectedProduct){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        List<String> spinnerResult = new ArrayList<>();
        String[] columns = new String[]{KEY_MENU};
        Cursor cursor = sqLiteDatabase.query(TABLE_FOOD,columns,KEY_PRODUCT+" = ?",new String[]{selectedProduct},null,null,null);
        if (cursor.moveToFirst()){
            do {
                spinnerResult.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return spinnerResult;
    }

    //Get Price by Spinner
    public Integer getPriceBySelectedInteger (String selectedSpinner){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int spinnerResult;
        String[] columns = new String[]{I_PRICE};
        Cursor cursor = sqLiteDatabase.query(TABLE_FOOD,columns,KEY_MENU+" = ?",new String[]{selectedSpinner}, null, null, null);
        if (cursor.moveToFirst()){
            spinnerResult = cursor.getInt(0);
        } else {
            spinnerResult = 0;
        }

        cursor.close();
        sqLiteDatabase.close();
        return spinnerResult;
    }

}
