package com.example.ui;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ViewHolder> {

    private List<OrderActivity.ProductOrder> productList;
    private OnItemClickListener listener;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    // Photo types
    public static final int PHOTO_TYPE_PRODUCT = 0;
    public static final int PHOTO_TYPE_PACKAGE = 1;

    public interface OnItemClickListener {
        void onPhotoClick(int position, int photoType);
        void onQuantityChange(int position, double newQuantity);
        void onRestockClick(int position);
    }

    public ProductOrderAdapter(List<OrderActivity.ProductOrder> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderActivity.ProductOrder product = productList.get(position);
        
        // Set product image (placeholder for now)
        holder.ivProduct.setImageResource(R.drawable.photo_placeholder);
        
        // Set product details
        holder.tvProductName.setText(product.getName());
        holder.tvBrand.setText("品牌：" + product.getBrand());
        holder.tvQuality.setText("质量等级：" + product.getQuality());
        holder.tvUnitPrice.setText("单价：" + DECIMAL_FORMAT.format(product.getUnitPrice()) + " 元/" + product.getUnit());
        holder.tvOrigin.setText("产地：" + product.getOrigin());
        holder.tvPackaging.setText("包装规格：" + product.getPackaging());
        
        // Set quantity and amount
        holder.tvOrderQuantity.setText("订单数量：" + DECIMAL_FORMAT.format(product.getOrderQuantity()) + product.getUnit());
        holder.tvOrderAmount.setText("订单金额：¥" + DECIMAL_FORMAT.format(product.getOrderAmount()));
        
        // Set verified quantity and amount
        if (product.isReturned()) {
            holder.tvVerifiedQuantity.setText("复核数量：--");
            holder.tvVerifiedAmount.setText("复核金额：¥--");
        } else {
            holder.tvVerifiedQuantity.setText("复核数量：" + DECIMAL_FORMAT.format(product.getVerifiedQuantity()) + product.getUnit());
            holder.tvVerifiedAmount.setText("复核金额：¥" + DECIMAL_FORMAT.format(product.getVerifiedAmount()));
        }
        
        // Set photos
        if (product.getProductPhoto() != null) {
            holder.ivPhoto1.setImageBitmap(product.getProductPhoto());
        } else {
            holder.ivPhoto1.setImageResource(R.drawable.photo_placeholder);
        }
        
        if (product.getPackagePhoto() != null) {
            holder.ivPhoto2.setImageBitmap(product.getPackagePhoto());
        } else {
            holder.ivPhoto2.setImageResource(R.drawable.photo_placeholder);
        }
        
        // Set photo click listeners
        holder.ivPhoto1.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(position, PHOTO_TYPE_PRODUCT);
            }
        });
        
        holder.ivPhoto2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(position, PHOTO_TYPE_PACKAGE);
            }
        });
        
        // Handle returned items differently
        if (product.isReturned()) {
            // Add visual indication for returned items
            holder.itemView.setAlpha(0.7f);
            holder.tvVerifiedQuantity.setTextColor(holder.itemView.getContext().getColor(R.color.text_gray));
            holder.tvVerifiedAmount.setTextColor(holder.itemView.getContext().getColor(R.color.text_gray));
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.tvVerifiedQuantity.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
            holder.tvVerifiedAmount.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }
    
    public void setProductPhoto(int position, int photoType, Bitmap photo) {
        if (position >= 0 && position < productList.size()) {
            OrderActivity.ProductOrder product = productList.get(position);
            if (photoType == PHOTO_TYPE_PRODUCT) {
                product.setProductPhoto(photo);
            } else if (photoType == PHOTO_TYPE_PACKAGE) {
                product.setPackagePhoto(photo);
            }
            notifyItemChanged(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName;
        TextView tvBrand;
        TextView tvQuality;
        TextView tvUnitPrice;
        TextView tvOrigin;
        TextView tvPackaging;
        TextView tvOrderQuantity;
        TextView tvVerifiedQuantity;
        TextView tvOrderAmount;
        TextView tvVerifiedAmount;
        ImageView ivPhoto1;
        ImageView ivPhoto2;
        View layoutPhoto2Container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvQuality = itemView.findViewById(R.id.tv_quality);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvOrigin = itemView.findViewById(R.id.tv_origin);
            tvPackaging = itemView.findViewById(R.id.tv_packaging);
            tvOrderQuantity = itemView.findViewById(R.id.tv_order_quantity);
            tvVerifiedQuantity = itemView.findViewById(R.id.tv_verified_quantity);
            tvOrderAmount = itemView.findViewById(R.id.tv_order_amount);
            tvVerifiedAmount = itemView.findViewById(R.id.tv_verified_amount);
            ivPhoto1 = itemView.findViewById(R.id.iv_photo1);
            ivPhoto2 = itemView.findViewById(R.id.iv_photo2);
            layoutPhoto2Container = itemView.findViewById(R.id.layout_photo2_container);
        }
    }
}