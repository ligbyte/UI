package com.example.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    
    // UI Components
    private TextView tvTitle;
    private TextView tvOrderTime;
    private TextView tvSupplier;
    private RecyclerView rvProducts;
    private TextView tvProductCount;
    private TextView tvVerifiedCount;
    private TextView tvReturnCount;
    private TextView tvOrderTotal;
    private TextView tvVerifiedTotal;
    private AppCompatButton btnConfirm;
    
    // Data
    private ProductOrderAdapter adapter;
    private List<ProductOrder> productList;
    private OrderSummary orderSummary;
    
    // Constants
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        updateSummary();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvSupplier = findViewById(R.id.tv_supplier);
        rvProducts = findViewById(R.id.rv_products);
        tvProductCount = findViewById(R.id.tv_product_count);
        tvVerifiedCount = findViewById(R.id.tv_verified_count);
        tvReturnCount = findViewById(R.id.tv_return_count);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        tvVerifiedTotal = findViewById(R.id.tv_verified_total);
        btnConfirm = findViewById(R.id.btn_confirm);
    }
    
    private void initData() {
        // Initialize order summary
        orderSummary = new OrderSummary();
        orderSummary.setOrderTime("2025-09-22 18:35:36");
        orderSummary.setSupplier("河南中农食品集团有限公司");
        
        // Initialize product list with sample data
        productList = new ArrayList<>();
        
        // Product 1: 五花肉
        ProductOrder product1 = new ProductOrder();
        product1.setName("五花肉");
        product1.setBrand("牧原");
        product1.setQuality("新鲜一级");
        product1.setUnitPrice(8.30);
        product1.setUnit("斤");
        product1.setOrigin("河南南阳");
        product1.setPackaging("散装");
        product1.setOrderQuantity(20.00);
        product1.setVerifiedQuantity(19.00);
        product1.setOrderAmount(166.00);
        product1.setVerifiedAmount(157.70);
        productList.add(product1);
        
        // Product 2: 菜籽油
        ProductOrder product2 = new ProductOrder();
        product2.setName("菜籽油");
        product2.setBrand("福临门");
        product2.setQuality("初榨一级");
        product2.setUnitPrice(8.30);
        product2.setUnit("桶");
        product2.setOrigin("山东临沂");
        product2.setPackaging("5升/桶");
        product2.setOrderQuantity(1.00);
        product2.setVerifiedQuantity(1.00);
        product2.setOrderAmount(166.00);
        product2.setVerifiedAmount(166.00);
        productList.add(product2);
        
        // Product 3: 青椒
        ProductOrder product3 = new ProductOrder();
        product3.setName("青椒");
        product3.setBrand("--");
        product3.setQuality("新鲜一级");
        product3.setUnitPrice(8.30);
        product3.setUnit("斤");
        product3.setOrigin("河南洛阳");
        product3.setPackaging("散装");
        product3.setOrderQuantity(20.00);
        product3.setVerifiedQuantity(0.00);
        product3.setOrderAmount(166.00);
        product3.setVerifiedAmount(0.00);
        product3.setIsReturned(true);
        productList.add(product3);
        
        // Set order info
        tvOrderTime.setText("订单时间：" + orderSummary.getOrderTime());
        tvSupplier.setText("供货单位：" + orderSummary.getSupplier());
    }
    
    private void setupRecyclerView() {
        adapter = new ProductOrderAdapter(productList, new ProductOrderAdapter.OnItemClickListener() {
            @Override
            public void onPhotoClick(int position, int photoType) {
                capturePhoto(position, photoType);
            }
            
            @Override
            public void onQuantityChange(int position, double newQuantity) {
                updateProductQuantity(position, newQuantity);
            }
            
            @Override
            public void onRestockClick(int position) {
                handleRestockClick(position);
            }
        });
        
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }
    
    private void capturePhoto(int productPosition, int photoType) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Store the position and photo type for later use
            takePictureIntent.putExtra("product_position", productPosition);
            takePictureIntent.putExtra("photo_type", photoType);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "无法打开相机", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            
            // Get the product position and photo type
            int productPosition = data.getIntExtra("product_position", -1);
            int photoType = data.getIntExtra("photo_type", -1);
            
            if (productPosition >= 0 && photoType >= 0) {
                // Update the adapter with the new photo
                adapter.setProductPhoto(productPosition, photoType, imageBitmap);
                Toast.makeText(this, "照片已保存", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void updateProductQuantity(int position, double newQuantity) {
        if (position >= 0 && position < productList.size()) {
            ProductOrder product = productList.get(position);
            product.setVerifiedQuantity(newQuantity);
            
            // Recalculate verified amount
            double verifiedAmount = newQuantity * product.getUnitPrice();
            product.setVerifiedAmount(verifiedAmount);
            
            // Update the adapter
            adapter.notifyItemChanged(position);
            
            // Update summary
            updateSummary();
        }
    }
    
    private void updateSummary() {
        int productCount = productList.size();
        int verifiedCount = 0;
        int returnCount = 0;
        double orderTotal = 0.0;
        double verifiedTotal = 0.0;
        
        for (ProductOrder product : productList) {
            orderTotal += product.getOrderAmount();
            verifiedTotal += product.getVerifiedAmount();
            
            if (product.isReturned()) {
                returnCount++;
            } else if (product.getVerifiedQuantity() > 0) {
                verifiedCount++;
            }
        }
        
        // Update summary views
        tvProductCount.setText("商品数量：" + productCount);
        tvVerifiedCount.setText("签收数量：" + verifiedCount);
        tvReturnCount.setText("退货数量：" + returnCount);
        tvOrderTotal.setText("订单总额：¥" + DECIMAL_FORMAT.format(orderTotal));
        tvVerifiedTotal.setText("复核总额：¥" + DECIMAL_FORMAT.format(verifiedTotal));
        
        // Update order summary object
        orderSummary.setProductCount(productCount);
        orderSummary.setVerifiedCount(verifiedCount);
        orderSummary.setReturnCount(returnCount);
        orderSummary.setOrderTotal(orderTotal);
        orderSummary.setVerifiedTotal(verifiedTotal);
    }
    
    private void confirmOrder() {
        // Validate that all required photos are taken
        boolean allPhotosValid = validatePhotos();
        
        if (!allPhotosValid) {
            Toast.makeText(this, "请为所有商品拍摄必要的照片", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Show success dialog
        SuccessDialog dialog = new SuccessDialog(this, "签收确认", "确认完成");
        dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
            @Override
            public void onSingleButtonClicked() {
                finishOrder();
            }
            
            @Override
            public void onLeftButtonClicked() {
                // Not used in single button mode
            }
            
            @Override
            public void onRightButtonClicked() {
                // Not used in single button mode
            }
            
            @Override
            public void onDialogDismissed() {
                finishOrder();
            }
        });
        dialog.show();
    }
    
    private boolean validatePhotos() {
        // Check if all non-returned products have required photos
        for (ProductOrder product : productList) {
            if (!product.isReturned()) {
                if (product.getProductPhoto() == null || product.getPackagePhoto() == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void finishOrder() {
        // Process the order completion
        Toast.makeText(this, "订单确认完成", Toast.LENGTH_SHORT).show();
        
        // Return to previous activity or main screen
        finish();
    }
    
    private void handleRestockClick(int position) {
        if (position >= 0 && position < productList.size()) {
            ProductOrder product = productList.get(position);
            
            // Show restock confirmation dialog
            SuccessDialog dialog = SuccessDialog.createWithTwoButtons(this, "取消", "确认补货");
            dialog.setTitle("是否确认补货：" + product.getName() + "?");
            
            dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
                @Override
                public void onSingleButtonClicked() {
                    // Not used
                }
                
                @Override
                public void onLeftButtonClicked() {
                    // Cancel - do nothing
                }
                
                @Override
                public void onRightButtonClicked() {
                    // Confirm restock
                    product.setIsReturned(false);
                    product.setVerifiedQuantity(product.getOrderQuantity());
                    product.setVerifiedAmount(product.getOrderAmount());
                    
                    adapter.notifyItemChanged(position);
                    updateSummary();
                    
                    Toast.makeText(OrderActivity.this, "已确认补货：" + product.getName(), Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onDialogDismissed() {
                    // Auto dismiss - do nothing
                }
            });
            
            dialog.show();
        }
    }
    
    // Data Models
    public static class ProductOrder {
        private String name;
        private String brand;
        private String quality;
        private double unitPrice;
        private String unit;
        private String origin;
        private String packaging;
        private double orderQuantity;
        private double verifiedQuantity;
        private double orderAmount;
        private double verifiedAmount;
        private boolean isReturned;
        private Bitmap productPhoto;
        private Bitmap packagePhoto;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
        
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        
        public String getPackaging() { return packaging; }
        public void setPackaging(String packaging) { this.packaging = packaging; }
        
        public double getOrderQuantity() { return orderQuantity; }
        public void setOrderQuantity(double orderQuantity) { this.orderQuantity = orderQuantity; }
        
        public double getVerifiedQuantity() { return verifiedQuantity; }
        public void setVerifiedQuantity(double verifiedQuantity) { this.verifiedQuantity = verifiedQuantity; }
        
        public double getOrderAmount() { return orderAmount; }
        public void setOrderAmount(double orderAmount) { this.orderAmount = orderAmount; }
        
        public double getVerifiedAmount() { return verifiedAmount; }
        public void setVerifiedAmount(double verifiedAmount) { this.verifiedAmount = verifiedAmount; }
        
        public boolean isReturned() { return isReturned; }
        public void setIsReturned(boolean returned) { isReturned = returned; }
        
        public Bitmap getProductPhoto() { return productPhoto; }
        public void setProductPhoto(Bitmap productPhoto) { this.productPhoto = productPhoto; }
        
        public Bitmap getPackagePhoto() { return packagePhoto; }
        public void setPackagePhoto(Bitmap packagePhoto) { this.packagePhoto = packagePhoto; }
    }
    
    public static class OrderSummary {
        private String orderTime;
        private String supplier;
        private int productCount;
        private int verifiedCount;
        private int returnCount;
        private double orderTotal;
        private double verifiedTotal;
        
        // Getters and Setters
        public String getOrderTime() { return orderTime; }
        public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
        
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        
        public int getProductCount() { return productCount; }
        public void setProductCount(int productCount) { this.productCount = productCount; }
        
        public int getVerifiedCount() { return verifiedCount; }
        public void setVerifiedCount(int verifiedCount) { this.verifiedCount = verifiedCount; }
        
        public int getReturnCount() { return returnCount; }
        public void setReturnCount(int returnCount) { this.returnCount = returnCount; }
        
        public double getOrderTotal() { return orderTotal; }
        public void setOrderTotal(double orderTotal) { this.orderTotal = orderTotal; }
        
        public double getVerifiedTotal() { return verifiedTotal; }
        public void setVerifiedTotal(double verifiedTotal) { this.verifiedTotal = verifiedTotal; }
    }
}