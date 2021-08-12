package com.example.shopdeck.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopdeck.ItemClickListner;
import com.example.shopdeck.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    public TextView txtproductName, txtproductDecs, txtproductPrice;
    public ImageView imageView;
    public ItemClickListner listner;
    public Button show;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView)itemView.findViewById(R.id.product_Image);
        txtproductName = (TextView)itemView.findViewById(R.id.product_Name);
        txtproductDecs = (TextView)itemView.findViewById(R.id.product_Desc);
        txtproductPrice = (TextView)itemView.findViewById(R.id.product_Price);
        show=(Button)itemView.findViewById(R.id.show_products1);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v,getAdapterPosition(), false);
    }
}
