package com.example.burgernezz.RVAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FinalCartRVAdapter extends RecyclerView.Adapter<FinalCartRVAdapter.ViewHolder>{
    private ArrayList<Cart> cartArrayList;
    private Context context;
    private FirebaseFirestore firestore;

    public FinalCartRVAdapter(ArrayList<Cart> cartArrayList, Context context) {
        this.cartArrayList = cartArrayList;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FinalCartRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.finalcart_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinalCartRVAdapter.ViewHolder holder, int position) {
        holder.trackingProduct.setText(cartArrayList.get(position).getProduct());
        holder.trackingQuantity.setText(String.valueOf(cartArrayList.get(position).getQuantity()));
        holder.trackingPrice.setText(String.valueOf(cartArrayList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trackingProduct;
        private final TextView trackingQuantity;
        private final TextView trackingPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingProduct = itemView.findViewById(R.id.trackingProduct);
            trackingQuantity = itemView.findViewById(R.id.trackingQuantity);
            trackingPrice = itemView.findViewById(R.id.trackingPrice);
        }
    }
}
