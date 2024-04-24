package com.example.burgernezz.RVAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burgernezz.CartFinalActivity;
import com.example.burgernezz.Models.Invoice;
import com.example.burgernezz.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class InvoiceRVAdapter extends RecyclerView.Adapter<InvoiceRVAdapter.ViewHolder> {

    private ArrayList<Invoice> invoiceArrayList;
    private Context context;
    private FirebaseFirestore firestore;

    public InvoiceRVAdapter(ArrayList<Invoice> invoiceArrayList, Context context) {
        this.invoiceArrayList = invoiceArrayList;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public InvoiceRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_listview,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceRVAdapter.ViewHolder holder, int position) {
        holder.trackingID.setText(invoiceArrayList.get(position).getInvoiceID());
        holder.trackingEmail.setText(invoiceArrayList.get(position).getEmail());
        holder.trackingPhone.setText(invoiceArrayList.get(position).getPhone());
        holder.trackingAddress.setText(invoiceArrayList.get(position).getAddress());
        holder.trackingTotalPrice.setText(String.valueOf(invoiceArrayList.get(position).getPaidPrice()));
        holder.trackingDateOrder.setText(invoiceArrayList.get(position).getDateTime());
        holder.trackingPaymentTypes.setText(invoiceArrayList.get(position).getPaymentType());
        holder.trackingPaymentID.setText(invoiceArrayList.get(position).getPaymentID());
        holder.trackingStatus.setText(invoiceArrayList.get(position).getStatus());
        holder.openInvoiceCartListView.setOnClickListener(view -> {
            int adapterPostion = holder.getAdapterPosition();
            String getInvoiceID = invoiceArrayList.get(adapterPostion).getInvoiceID();
            Intent intent = new Intent(context, CartFinalActivity.class);
            intent.putExtra("get_id",getInvoiceID);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return invoiceArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trackingID;
        private final TextView trackingEmail;
        private final TextView trackingPhone;
        private final TextView trackingAddress;
        private final TextView trackingTotalPrice;
        private final TextView trackingDateOrder;
        private final TextView trackingPaymentTypes;
        private final TextView trackingPaymentID;
        private final TextView trackingStatus;
        private final Button openInvoiceCartListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingID = itemView.findViewById(R.id.trackingID);
            trackingEmail = itemView.findViewById(R.id.trackingEmail);
            trackingPhone = itemView.findViewById(R.id.trackingPhone);
            trackingAddress = itemView.findViewById(R.id.trackingAddress);
            trackingTotalPrice = itemView.findViewById(R.id.trackingTotalPrice);
            trackingDateOrder = itemView.findViewById(R.id.trackingDateOrder);
            trackingPaymentTypes = itemView.findViewById(R.id.trackingPaymentTypes);
            trackingPaymentID = itemView.findViewById(R.id.trackingPaymentID);
            trackingStatus = itemView.findViewById(R.id.trackingStatus);
            openInvoiceCartListView = itemView.findViewById(R.id.openInvoiceCartListView);
        }
    }
}
