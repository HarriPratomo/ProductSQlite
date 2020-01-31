package com.example.inventarisku.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventarisku.R;
import com.example.inventarisku.model.ProductObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductObject> dataProduct;

    public ProductAdapter(Context context, List<ProductObject> dataProduct) {
        this.context = context;
        this.dataProduct = dataProduct;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductObject productObject = dataProduct.get(position);


        holder.imgProduct.setImageURI(Uri.parse(productObject.getImg()));
        holder.nameProduct.setText(productObject.getName());
        holder.priceProduct.setText("Rp" + productObject.getPrice());

        holder.timeProduct.setText(formatDate(productObject.getTime()));
    }

    @Override
    public int getItemCount() {
        return dataProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProduct;
        public TextView nameProduct, priceProduct, timeProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.productPhoto);
            nameProduct = itemView.findViewById(R.id.productName);
            priceProduct = itemView.findViewById(R.id.productPrice);
            timeProduct = itemView.findViewById(R.id.time);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (Exception e) {

        }
        return "";
    }
}
