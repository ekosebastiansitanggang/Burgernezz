package com.example.burgernezz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.burgernezz.MainActivity;
import com.example.burgernezz.ProductActivity;
import com.example.burgernezz.R;


public class MenuAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    String[] menuName, menuPrice;
    int[] menuImage;

    public MenuAdapter(Context context, String[] menuName, String[] menuPrice, int[] menuImage) {
        this.context=context;
        this.menuName=menuName;
        this.menuPrice=menuPrice;
        this.menuImage=menuImage;
        inflater.from(context);
    }

    @Override
    public int getCount() {
        return menuName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.menu_gridview,null);
        };
        ImageView imageMenu = convertView.findViewById(R.id.img_menu);
        TextView textMenu = convertView.findViewById(R.id.text_menu);
        TextView priceMenu = convertView.findViewById(R.id.price_menu);
        imageMenu.setImageResource(menuImage[position]);
        textMenu.setText(menuName[position]);
        priceMenu.setText(menuPrice[position]);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("getProduct",position);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

}
