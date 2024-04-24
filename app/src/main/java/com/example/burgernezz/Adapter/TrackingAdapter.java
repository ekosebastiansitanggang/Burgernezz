package com.example.burgernezz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.burgernezz.CartFinalActivity;
import com.example.burgernezz.Database.DBHelper;
import com.example.burgernezz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackingAdapter extends SimpleAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<HashMap<String, String>> inVoiceList;
    Button btnReveal;
    DBHelper DB;

    public TrackingAdapter (Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.inVoiceList=data;
        inflater.from(context);
    }

    @Override
    public int getCount() {
        return inVoiceList.size();
    }

    @Override
    public Object getItem(int position) {
        return inVoiceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DB = new DBHelper(context);
        View view = super.getView(position,convertView,parent);
        btnReveal = view.findViewById(R.id.openInvoiceCartListView);
        btnReveal.setOnClickListener(view1 -> {
            String getInvoiceID = inVoiceList.get(position).get("trackingID");
            Intent intent = new Intent(view.getContext(), CartFinalActivity.class);
            intent.putExtra("get_id", getInvoiceID);
            context.startActivity(intent);
        });
        return view;
    }
}
