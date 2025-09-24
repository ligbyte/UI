package com.example.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class SuccessDialog extends Dialog {
    private TextView tvCountdown;
    private TextView tvTitle;
    private AppCompatButton btnSingle;
    private LinearLayout layoutDoubleButtons;
    private AppCompatButton btnLeft;
    private AppCompatButton btnRight;
    private CountDownTimer countDownTimer;
    private OnDialogActionListener listener;
    
    public interface OnDialogActionListener {
        void onSingleButtonClicked();
        void onLeftButtonClicked();
        void onRightButtonClicked();
        void onDialogDismissed();
    }

    // Single button constructor without title
    public SuccessDialog(@NonNull Context context, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(null, buttonText, null);
    }
    
    // Single button constructor with title
    public SuccessDialog(@NonNull Context context, String title, String buttonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, buttonText, null);
    }
    
    // Double button constructor with title
    public SuccessDialog(@NonNull Context context, String title, String leftButtonText, String rightButtonText) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        initDialog(title, leftButtonText, rightButtonText);
    }
    
    // Static factory method for double button without title
    public static SuccessDialog createWithTwoButtons(@NonNull Context context, String leftButtonText, String rightButtonText) {
        SuccessDialog dialog = new SuccessDialog(context, "");
        dialog.initDialog(null, leftButtonText, rightButtonText);
        return dialog;
    }

    private void initDialog(String title, String buttonText, String rightButtonText) {
        // Set dialog properties
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        
        // Inflate layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_success, null);
        setContentView(dialogView);
        
        // Make dialog fullscreen
        if (getWindow() != null) {
            getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        }
        
        // Initialize views
        tvCountdown = dialogView.findViewById(R.id.tv_countdown);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        btnSingle = dialogView.findViewById(R.id.btn_single);
        layoutDoubleButtons = dialogView.findViewById(R.id.layout_double_buttons);
        btnLeft = dialogView.findViewById(R.id.btn_left);
        btnRight = dialogView.findViewById(R.id.btn_right);
        
        // Set initial countdown text
        tvCountdown.setText("3s");
        tvCountdown.setVisibility(View.VISIBLE);
        
        // Configure title
        if (title != null && !title.trim().isEmpty()) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        
        // Configure buttons based on parameters
        if (rightButtonText == null) {
            // Single button mode
            btnSingle.setText(buttonText);
            btnSingle.setVisibility(View.VISIBLE);
            layoutDoubleButtons.setVisibility(View.GONE);
            
            btnSingle.setOnClickListener(v -> {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (listener != null) {
                    listener.onSingleButtonClicked();
                }
                dismiss();
            });
        } else {
            // Double button mode
            btnLeft.setText(buttonText);
            btnRight.setText(rightButtonText);
            btnSingle.setVisibility(View.GONE);
            layoutDoubleButtons.setVisibility(View.VISIBLE);
            
            btnLeft.setOnClickListener(v -> {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (listener != null) {
                    listener.onLeftButtonClicked();
                }
                dismiss();
            });
            
            btnRight.setOnClickListener(v -> {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (listener != null) {
                    listener.onRightButtonClicked();
                }
                dismiss();
            });
        }
        
        // Start countdown with a small delay
        tvCountdown.postDelayed(this::startCountdown, 100);
    }
    
    private void startCountdown() {
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvCountdown.setText(seconds + "s");
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onDialogDismissed();
                }
                dismiss();
            }
        };
        countDownTimer.start();
    }
    
    public void setOnDialogActionListener(OnDialogActionListener listener) {
        this.listener = listener;
    }
    
    public void setTitle(String title) {
        if (tvTitle != null) {
            if (title != null && !title.trim().isEmpty()) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    public void dismiss() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.dismiss();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}