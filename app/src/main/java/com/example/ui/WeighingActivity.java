package com.example.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Random;

public class WeighingActivity extends AppCompatActivity {

    // UI Components
    private TextView tvWeightDisplay;
    private TextView tvTareWeight;
    private TextView tvGrossWeight;
    private TextView tvUnitPrice;
    private TextView tvTotalPrice;
    private ScaleDashboardView scaleDashboard;
    
    // Mode buttons
    private Button btnStable;
    private Button btnNet;
    private Button btnZero;
    
    // Keypad buttons
    private Button[] keypadButtons = new Button[12];
    
    // Action buttons
    private Button btnTareAction;
    private Button btnZeroAction;
    
    // Weight and price data
    private double currentWeight = 0.000;
    private double tareWeight = 0.000;
    private double grossWeight = 0.000;
    private double unitPrice = 0.00;
    private double totalPrice = 0.00;
    
    // Input state
    private StringBuilder priceInput = new StringBuilder();
    private boolean isInputtingPrice = false;
    
    // Scale simulation
    private Handler weightHandler = new Handler();
    private Random random = new Random();
    private boolean isStable = false;
    private boolean isSimulatingWeight = false;
    
    // Format patterns
    private DecimalFormat weightFormat = new DecimalFormat("0.000");
    private DecimalFormat priceFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weighing);
        
        initializeViews();
        setupEventListeners();
        startWeightSimulation();
    }
    
    private void initializeViews() {
        // Weight displays
        tvWeightDisplay = findViewById(R.id.tv_weight_display);
        tvTareWeight = findViewById(R.id.tv_tare_weight);
        tvGrossWeight = findViewById(R.id.tv_gross_weight);
        tvUnitPrice = findViewById(R.id.tv_unit_price);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        scaleDashboard = findViewById(R.id.scale_dashboard);
        
        // Mode buttons
        btnStable = findViewById(R.id.btn_stable);
        btnNet = findViewById(R.id.btn_net);
        btnZero = findViewById(R.id.btn_zero);
        
        // Keypad buttons
        keypadButtons[0] = findViewById(R.id.btn_1);
        keypadButtons[1] = findViewById(R.id.btn_2);
        keypadButtons[2] = findViewById(R.id.btn_3);
        keypadButtons[3] = findViewById(R.id.btn_4);
        keypadButtons[4] = findViewById(R.id.btn_5);
        keypadButtons[5] = findViewById(R.id.btn_6);
        keypadButtons[6] = findViewById(R.id.btn_7);
        keypadButtons[7] = findViewById(R.id.btn_8);
        keypadButtons[8] = findViewById(R.id.btn_9);
        keypadButtons[9] = findViewById(R.id.btn_0);
        keypadButtons[10] = findViewById(R.id.btn_dot);
        keypadButtons[11] = findViewById(R.id.btn_clear);
        
        // Action buttons
        btnTareAction = findViewById(R.id.btn_tare_action);
        btnZeroAction = findViewById(R.id.btn_zero_action);
        
        // Set initial display values
        updateWeightDisplays();
        updatePriceDisplays();
        
        // Set initial needle position to zero
        scaleDashboard.setWeight(0f);
        
        // Set initial mode button selection (Net Weight mode)
        setModeButtonSelection(btnNet);
    }
    
    private void setupEventListeners() {
        // Mode button listeners
        btnStable.setOnClickListener(v -> {
            setModeButtonSelection(btnStable);
            isStable = true;
            stopWeightSimulation();
            Toast.makeText(this, "稳定模式", Toast.LENGTH_SHORT).show();
        });
        
        btnNet.setOnClickListener(v -> {
            setModeButtonSelection(btnNet);
            isStable = false;
            startWeightSimulation();
            Toast.makeText(this, "净重模式", Toast.LENGTH_SHORT).show();
        });
        
        btnZero.setOnClickListener(v -> {
            setModeButtonSelection(btnZero);
            zeroScale();
            Toast.makeText(this, "零位模式", Toast.LENGTH_SHORT).show();
        });
        
        // Keypad listeners
        for (int i = 0; i < 10; i++) {
            final int digit = (i == 9) ? 0 : i + 1;
            keypadButtons[i].setOnClickListener(v -> onNumberPressed(String.valueOf(digit)));
        }
        
        // Dot button
        keypadButtons[10].setOnClickListener(v -> onNumberPressed("."));
        
        // Clear button
        keypadButtons[11].setOnClickListener(v -> onClearPressed());
        
        // Action button listeners
        btnTareAction.setOnClickListener(v -> performTare());
        btnZeroAction.setOnClickListener(v -> performZero());
        
        // Long press to start price input
        tvUnitPrice.setOnLongClickListener(v -> {
            startPriceInput();
            return true;
        });
    }
    
    private void setModeButtonSelection(Button selectedButton) {
        btnStable.setSelected(false);
        btnNet.setSelected(false);
        btnZero.setSelected(false);
        selectedButton.setSelected(true);
    }
    
    private void startWeightSimulation() {
        if (isSimulatingWeight) return;
        
        isSimulatingWeight = true;
        weightHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isStable && isSimulatingWeight) {
                    // Simulate weight fluctuation for 0-150kg scale
                    double baseWeight = 2.0 + (random.nextDouble() * 25.0); // 2.0 to 27.0 kg
                    double fluctuation = (random.nextDouble() - 0.5) * 0.2; // ±0.1 kg
                    currentWeight = Math.max(0, baseWeight + fluctuation);
                    
                    updateWeightDisplays();
                    updateNeedlePosition();
                    calculateTotalPrice();
                    
                    weightHandler.postDelayed(this, 200); // Update every 200ms
                }
            }
        }, 200);
    }
    
    private void stopWeightSimulation() {
        isSimulatingWeight = false;
        weightHandler.removeCallbacksAndMessages(null);
    }
    
    private void updateWeightDisplays() {
        grossWeight = currentWeight + tareWeight;
        
        tvWeightDisplay.setText(weightFormat.format(currentWeight) + " kg");
        tvTareWeight.setText(weightFormat.format(tareWeight) + " kg");
        tvGrossWeight.setText(weightFormat.format(grossWeight) + " kg");
    }
    
    private void updatePriceDisplays() {
        tvUnitPrice.setText(priceFormat.format(unitPrice));
        tvTotalPrice.setText(priceFormat.format(totalPrice));
    }
    
    private void updateNeedlePosition() {
        // Update the custom dashboard view with current weight
        scaleDashboard.animateToWeight((float) currentWeight, 150);
    }
    
    private void zeroScale() {
        currentWeight = 0.000;
        tareWeight = 0.000;
        grossWeight = 0.000;
        updateWeightDisplays();
        updateNeedlePosition();
        calculateTotalPrice();
    }
    
    private void performTare() {
        tareWeight = -currentWeight;
        updateWeightDisplays();
        calculateTotalPrice();
        Toast.makeText(this, "皮重已设置", Toast.LENGTH_SHORT).show();
    }
    
    private void performZero() {
        zeroScale();
        Toast.makeText(this, "已归零", Toast.LENGTH_SHORT).show();
    }
    
    private void onNumberPressed(String number) {
        if (!isInputtingPrice) return;
        
        if (priceInput.length() < 8) { // Limit input length
            priceInput.append(number);
            updatePriceInput();
        }
    }
    
    private void onClearPressed() {
        if (isInputtingPrice) {
            if (priceInput.length() > 0) {
                priceInput.deleteCharAt(priceInput.length() - 1);
                updatePriceInput();
            } else {
                finishPriceInput();
            }
        } else {
            // Clear current weight simulation
            stopWeightSimulation();
            currentWeight = 0.000;
            updateWeightDisplays();
            updateNeedlePosition();
            calculateTotalPrice();
        }
    }
    
    private void startPriceInput() {
        isInputtingPrice = true;
        priceInput.setLength(0);
        tvUnitPrice.setBackgroundColor(0xFF0078D4); // Highlight input
        tvUnitPrice.setTextColor(0xFFFFFFFF);
        Toast.makeText(this, "请输入单价", Toast.LENGTH_SHORT).show();
    }
    
    private void updatePriceInput() {
        try {
            String inputText = priceInput.toString();
            if (!inputText.isEmpty()) {
                double price = Double.parseDouble(inputText);
                tvUnitPrice.setText(priceFormat.format(price));
            }
        } catch (NumberFormatException e) {
            // Handle invalid input
        }
    }
    
    private void finishPriceInput() {
        isInputtingPrice = false;
        
        try {
            String inputText = priceInput.toString();
            if (!inputText.isEmpty()) {
                unitPrice = Double.parseDouble(inputText);
            }
        } catch (NumberFormatException e) {
            unitPrice = 0.00;
        }
        
        // Reset appearance
        tvUnitPrice.setBackgroundColor(0x00000000); // Transparent
        tvUnitPrice.setTextColor(0xFF000000); // Black text
        
        updatePriceDisplays();
        calculateTotalPrice();
        
        Toast.makeText(this, "单价已设置", Toast.LENGTH_SHORT).show();
    }
    
    private void calculateTotalPrice() {
        totalPrice = currentWeight * unitPrice;
        updatePriceDisplays();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWeightSimulation();
    }
}