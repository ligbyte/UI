package com.example.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton btnShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        btnShowDialog = findViewById(R.id.btn_show_dialog);
    }
    
    private void setupClickListeners() {
        btnShowDialog.setOnClickListener(v -> showSuccessDialog());
    }
    
    private void showSuccessDialog() {
        // Example 1: Single button dialog with title
        SuccessDialog dialog = new SuccessDialog(this, "操作成功", "确认");
        
        // Example 2: Single button dialog without title (uncomment to test)
        // SuccessDialog dialog = new SuccessDialog(this, "确认");
        
        // Example 3: Double button dialog without title (uncomment to test)
        // SuccessDialog dialog = SuccessDialog.createWithTwoButtons(this, "取消", "确认");
        
        // Example 4: Double button dialog with title (uncomment to test)
        // SuccessDialog dialog = new SuccessDialog(this, "请选择操作", "取消", "确认");
        
        // Example 5: Set title after creation
        // dialog.setTitle("自定义标题");
        
        dialog.setOnDialogActionListener(new SuccessDialog.OnDialogActionListener() {
            @Override
            public void onSingleButtonClicked() {
                Toast.makeText(MainActivity.this, "Single button clicked", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onLeftButtonClicked() {
                Toast.makeText(MainActivity.this, "Left button clicked", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onRightButtonClicked() {
                Toast.makeText(MainActivity.this, "Right button clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDialogDismissed() {
                Toast.makeText(MainActivity.this, "Dialog auto-dismissed after countdown", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}