package com.example.burgernezz.RVAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burgernezz.Models.Cart;
import com.example.burgernezz.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CartRVAdapter extends RecyclerView.Adapter<CartRVAdapter.ViewHolder> {
    private ArrayList<Cart>cartArrayList;
    private Context context;
    private String shrdInvoiceID;
    private FirebaseFirestore firestore;

    public CartRVAdapter(ArrayList<Cart> cartArrayList, Context context, String shrdInvoiceID) {
        this.cartArrayList = cartArrayList;
        this.context = context;
        this.shrdInvoiceID = shrdInvoiceID;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_tableview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRVAdapter.ViewHolder holder, int position) {
        String setPrice = "Rp "+ cartArrayList.get(position).getPrice();
        holder.cartProduct.setText(cartArrayList.get(position).getProduct());
        holder.cartQuantity.setText(String.valueOf(cartArrayList.get(position).getQuantity()));
        holder.cartPrice.setText(setPrice);
        holder.cartDecrease.setOnClickListener(view -> {
            int adapterPostion = holder.getAdapterPosition();
            String getProduct = cartArrayList.get(adapterPostion).getProduct();
            Integer getQuantity = cartArrayList.get(adapterPostion).getQuantity();
            Integer getPrice = cartArrayList.get(adapterPostion).getPrice();
            Integer getOnePrice = getPrice/getQuantity;
            if (getQuantity <= 1) { getQuantity = 1; } else {
                getQuantity--; }
            Integer setQuantity = getQuantity;
            Integer setPrice1 = getOnePrice*setQuantity;
            Cart updatedCart = new Cart(getProduct,setQuantity, setPrice1);
            firestore.collection("Order").document(shrdInvoiceID).collection("Cart")
                    .document(cartArrayList.get(adapterPostion).getCartID()).set(updatedCart)
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Data failed to change", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Data sucessfully changed", Toast.LENGTH_SHORT).show();
                        cartArrayList.get(adapterPostion).setQuantity(setQuantity);
                        cartArrayList.get(adapterPostion).setPrice(setPrice1);
                        notifyItemChanged(adapterPostion);
                        notifyDataSetChanged();
                    });
        });
        holder.cartIncrease.setOnClickListener(view -> {
            int adapterPostion = holder.getAdapterPosition();
            String getProduct = cartArrayList.get(adapterPostion).getProduct();
            Integer getQuantity = cartArrayList.get(adapterPostion).getQuantity();
            Integer getPrice = cartArrayList.get(adapterPostion).getPrice();
            Integer getOnePrice = getPrice/getQuantity;
            if (getQuantity >= 10) { getQuantity = 10; } else {
                getQuantity++; }
            Integer setQuantity = getQuantity;
            Integer setPrice12 = getOnePrice*setQuantity;
            Cart updatedCart = new Cart(getProduct,setQuantity, setPrice12);
            firestore.collection("Order").document(shrdInvoiceID).collection("Cart")
                    .document(cartArrayList.get(adapterPostion).getCartID()).set(updatedCart)
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Data failed to change", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Data sucessfully changed", Toast.LENGTH_SHORT).show();
                        cartArrayList.get(adapterPostion).setQuantity(setQuantity);
                        cartArrayList.get(adapterPostion).setPrice(setPrice12);
                        notifyItemChanged(adapterPostion);
                        notifyDataSetChanged();
                    });
        });
        holder.btnDelete.setOnClickListener(view -> {
            int adapterPostion = holder.getAdapterPosition();
            String getProduct = cartArrayList.get(adapterPostion).getProduct();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Data");
            builder.setMessage("Are you sure to delete "+getProduct+"?");
            builder.setIcon(R.drawable.ic_baseline_remove_shopping_cart_black_24);
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                firestore.collection("Order").document(shrdInvoiceID).collection("Cart")
                                .document(cartArrayList.get(adapterPostion).getCartID()).delete()
                                .addOnFailureListener(e ->
                                    Toast.makeText(context, "Data failed to delete", Toast.LENGTH_SHORT).show())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Data sucessfully deleted", Toast.LENGTH_SHORT).show();
                                    cartArrayList.remove(cartArrayList.get(adapterPostion));
                                    notifyItemRemoved(adapterPostion);
                                    notifyDataSetChanged();
                                });
                dialogInterface.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cartProduct;
        private final TextView cartQuantity;
        private final TextView cartPrice;
        private ImageButton cartDecrease;
        private ImageButton cartIncrease;
        private Button btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartProduct = itemView.findViewById(R.id.cartProduct);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            cartPrice = itemView.findViewById(R.id.cartPrice);
            cartDecrease = itemView.findViewById(R.id.cartDecrease);
            cartIncrease = itemView.findViewById(R.id.cartIncrease);
            btnDelete = itemView.findViewById(R.id.btn_removeone);
        }
    }
}
