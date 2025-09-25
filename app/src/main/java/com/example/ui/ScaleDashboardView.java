package com.example.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;

/**
 * Custom dashboard view that draws a precise weighing scale using Java code
 * Following the design specifications for accurate visual representation
 */
public class ScaleDashboardView extends View {
    
    // Dashboard dimensions and positioning
    private static final float DASHBOARD_SIZE = 900f; // Increased to 1.5x (600 * 1.5)
    private static final float CENTER_X = DASHBOARD_SIZE / 2f;
    private static final float CENTER_Y = DASHBOARD_SIZE / 2f;
    private static final float OUTER_RADIUS = 420f; // Increased to 1.5x (280 * 1.5)
    private static final float INNER_RADIUS = 390f; // Increased to 1.5x (260 * 1.5)
    private static final float MAJOR_TICK_LENGTH = 37.5f; // Increased to 1.5x (25 * 1.5)
    private static final float MINOR_TICK_LENGTH = 22.5f; // Increased to 1.5x (15 * 1.5)
    
    // Needle properties
    private static final float NEEDLE_LENGTH = 320f; // Shortened by 10px from 330f to prevent overlap with scale text
    private static final float NEEDLE_WIDTH = 12f; // Increased to 1.5x (8 * 1.5)
    private static final float NEEDLE_BASE_RADIUS = 30f; // Increased to 1.5x (20 * 1.5)
    private static final float TINY_TICK_LENGTH = 12f; // Increased to 1.5x (8 * 1.5)
    
    // Scale properties
    private static final float START_ANGLE = 135f; // Start from top-left (0kg)
    private static final float END_ANGLE = 45f; // End at top-right (150kg)
    private static final float SWEEP_ANGLE = 270f; // Cover 270 degrees with gap at bottom
    private static final float GAP_ANGLE = 90f; // 90 degree gap at bottom (270° to 360° and 0° to 90°)
    private static final int MAX_VALUE = 150; // 0-150kg range
    private static final int MAJOR_TICK_INTERVAL = 10; // Major ticks every 10kg
    private static final int MINOR_TICK_INTERVAL = 5; // Minor ticks every 5kg
    private static final int TINY_TICK_INTERVAL = 1; // Tiny ticks every 1kg
    
    // Paint objects for different elements
    private Paint circlePaint;
    private Paint majorTickPaint;
    private Paint minorTickPaint;
    private Paint tinyTickPaint;
    private Paint needlePaint;
    private Paint needleBasePaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    
    // Current weight value for needle position
    private float currentWeight = 0f;
    
    // Colors
    private int scaleGreen;
    private int scaleDarkGreen;
    private int backgroundColor;
    private int textColor;
    
    public ScaleDashboardView(Context context) {
        super(context);
        init();
    }
    
    public ScaleDashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ScaleDashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // Initialize colors from resources
        scaleGreen = ContextCompat.getColor(getContext(), R.color.scale_green);
        scaleDarkGreen = ContextCompat.getColor(getContext(), R.color.scale_dark_green);
        backgroundColor = ContextCompat.getColor(getContext(), R.color.white);
        textColor = ContextCompat.getColor(getContext(), R.color.black);
        
        // Initialize paint objects
        setupPaints();
    }
    
    private void setupPaints() {
        // Background paint
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setDither(true);
        
        // Circle border paint with enhanced smoothness
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(scaleGreen);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(18f); // Increased to 1.5x (12 * 1.5)
        circlePaint.setStrokeCap(Paint.Cap.ROUND); // Smooth end caps
        circlePaint.setFilterBitmap(true);
        circlePaint.setDither(true);
        circlePaint.setPathEffect(null); // Ensure no path effects interfere
        
        // Major tick paint
        majorTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        majorTickPaint.setColor(scaleGreen);
        majorTickPaint.setStyle(Paint.Style.STROKE);
        majorTickPaint.setStrokeWidth(9f); // Increased to 1.5x (6 * 1.5)
        majorTickPaint.setStrokeCap(Paint.Cap.ROUND);
        majorTickPaint.setFilterBitmap(true);
        majorTickPaint.setDither(true);
        
        // Minor tick paint
        minorTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minorTickPaint.setColor(scaleGreen);
        minorTickPaint.setStyle(Paint.Style.STROKE);
        minorTickPaint.setStrokeWidth(4.5f); // Increased to 1.5x (3 * 1.5)
        minorTickPaint.setStrokeCap(Paint.Cap.ROUND);
        minorTickPaint.setFilterBitmap(true);
        minorTickPaint.setDither(true);
        
        // Tiny tick paint
        tinyTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tinyTickPaint.setColor(scaleGreen);
        tinyTickPaint.setStyle(Paint.Style.STROKE);
        tinyTickPaint.setStrokeWidth(2.25f); // Increased to 1.5x (1.5 * 1.5)
        tinyTickPaint.setStrokeCap(Paint.Cap.ROUND);
        tinyTickPaint.setFilterBitmap(true);
        tinyTickPaint.setDither(true);
        
        // Needle paint
        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(scaleGreen);
        needlePaint.setStyle(Paint.Style.FILL);
        needlePaint.setFilterBitmap(true);
        needlePaint.setDither(true);
        
        // Needle base paint
        needleBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needleBasePaint.setColor(scaleDarkGreen);
        needleBasePaint.setStyle(Paint.Style.FILL);
        needleBasePaint.setFilterBitmap(true);
        needleBasePaint.setDither(true);
        
        // Text paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(scaleGreen); // Match scale markings color
        textPaint.setTextSize(30f); // Increased to 1.5x (20 * 1.5)
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT);
        textPaint.setFilterBitmap(true);
        textPaint.setDither(true);
        textPaint.setSubpixelText(true); // Enable subpixel text rendering
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) DASHBOARD_SIZE;
        setMeasuredDimension(size, size);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background circle
        drawBackground(canvas);
        
        // Draw outer circle border
        drawCircleBorder(canvas);
        
        // Draw scale markings
        drawScaleMarkings(canvas);
        
        // Draw needle
        drawNeedle(canvas);
        
        // Draw needle base
        drawNeedleBase(canvas);
    }
    
    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(CENTER_X, CENTER_Y, OUTER_RADIUS, backgroundPaint);
    }
    
    private void drawCircleBorder(Canvas canvas) {
        // Create precise oval bounds for arc drawing
        RectF oval = new RectF(CENTER_X - OUTER_RADIUS, CENTER_Y - OUTER_RADIUS, 
                              CENTER_X + OUTER_RADIUS, CENTER_Y + OUTER_RADIUS);
        
        // Draw arc from 135° to 405° (270° sweep with 90° gap at bottom)
        // Use Path for better control over arc quality
        Path arcPath = new Path();
        arcPath.addArc(oval, START_ANGLE, SWEEP_ANGLE);
        
        // Draw the arc path with enhanced smoothness
        canvas.drawPath(arcPath, circlePaint);
    }
    
    private void drawScaleMarkings(Canvas canvas) {
        // Draw all tick marks with fine precision
        for (int value = 0; value <= MAX_VALUE; value += TINY_TICK_INTERVAL) {
            // Determine tick type and properties
            boolean isMajorTick = (value % MAJOR_TICK_INTERVAL == 0);
            boolean isMinorTick = (value % MINOR_TICK_INTERVAL == 0) && !isMajorTick;
            boolean isTinyTick = !isMajorTick && !isMinorTick;
            
            // Skip drawing at the gap area (bottom 90 degrees)
            float progress = value / (float) MAX_VALUE;
            float angle = START_ANGLE + progress * SWEEP_ANGLE;
            
            // The gap is at the bottom from 45° to 135° (90° gap)
            // We draw from 135° to 405° (wrapping around), so all angles in this range are valid
            // No need to skip any angles since we're only drawing the arc portion
            
            float angleRad = (float) Math.toRadians(angle);
            
            // Select appropriate tick properties
            float tickLength;
            Paint tickPaint;
            
            if (isMajorTick) {
                tickLength = MAJOR_TICK_LENGTH;
                tickPaint = majorTickPaint;
            } else if (isMinorTick) {
                tickLength = MINOR_TICK_LENGTH;
                tickPaint = minorTickPaint;
            } else {
                tickLength = TINY_TICK_LENGTH;
                tickPaint = tinyTickPaint;
            }
            
            // Calculate tick mark positions (from outer edge inward)
            float outerX = CENTER_X + (OUTER_RADIUS - 9) * (float) Math.cos(angleRad); // Adjusted for larger stroke
            float outerY = CENTER_Y + (OUTER_RADIUS - 9) * (float) Math.sin(angleRad);
            
            float innerX = CENTER_X + (OUTER_RADIUS - 9 - tickLength) * (float) Math.cos(angleRad);
            float innerY = CENTER_Y + (OUTER_RADIUS - 9 - tickLength) * (float) Math.sin(angleRad);
            
            // Draw tick mark
            canvas.drawLine(outerX, outerY, innerX, innerY, tickPaint);
            
            // Draw numbers for major ticks
            if (isMajorTick) {
                drawTickNumber(canvas, value, angle);
            }
        }
    }
    
    private void drawTickNumber(Canvas canvas, int value, float angle) {
        float angleRad = (float) Math.toRadians(angle);
        
        // Position text further inside from the tick marks - moved further from center
        float textRadius = OUTER_RADIUS - 80f; // Increased distance from 45f to 80f
        float textX = CENTER_X + textRadius * (float) Math.cos(angleRad);
        float textY = CENTER_Y + textRadius * (float) Math.sin(angleRad);
        
        // Adjust text position for better readability
        textY += textPaint.getTextSize() / 3f; // Center text vertically
        
        // Draw the number
        canvas.drawText(String.valueOf(value), textX, textY, textPaint);
    }
    
    private void drawNeedle(Canvas canvas) {
        // Calculate needle angle based on current weight
        // Map weight (0-150kg) to angle range (135° to 405°, which is 135° to 45° wrapping around)
        float progress = currentWeight / (float) MAX_VALUE;
        float needleAngle = START_ANGLE + progress * SWEEP_ANGLE;
        
        // Save canvas state
        canvas.save();
        
        // Rotate canvas to needle angle
        // Since the needle is drawn pointing upward (-Y direction) and 0° is at 3 o'clock,
        // we need to add 90° to align properly
        canvas.rotate(needleAngle + 90f, CENTER_X, CENTER_Y);
        
        // Create smooth needle path
        Path needlePath = new Path();
        
        // Needle body (rectangle with rounded corners)
        float needleHalfWidth = NEEDLE_WIDTH / 2f;
        needlePath.moveTo(CENTER_X - needleHalfWidth, CENTER_Y);
        needlePath.lineTo(CENTER_X + needleHalfWidth, CENTER_Y);
        needlePath.lineTo(CENTER_X + needleHalfWidth, CENTER_Y - NEEDLE_LENGTH + 30f);
        needlePath.lineTo(CENTER_X - needleHalfWidth, CENTER_Y - NEEDLE_LENGTH + 30f);
        needlePath.close();
        
        // Needle tip (triangle with smooth curves)
        Path needleTip = new Path();
        needleTip.moveTo(CENTER_X, CENTER_Y - NEEDLE_LENGTH);
        needleTip.lineTo(CENTER_X - needleHalfWidth - 5f, CENTER_Y - NEEDLE_LENGTH + 30f);
        needleTip.lineTo(CENTER_X + needleHalfWidth + 5f, CENTER_Y - NEEDLE_LENGTH + 30f);
        needleTip.close();
        
        // Draw needle with enhanced paint
        canvas.drawPath(needlePath, needlePaint);
        canvas.drawPath(needleTip, needlePaint);
        
        // Restore canvas state
        canvas.restore();
    }
    
    private void drawNeedleBase(Canvas canvas) {
        // Draw outer base circle (dark green)
        canvas.drawCircle(CENTER_X, CENTER_Y, NEEDLE_BASE_RADIUS, needleBasePaint);
        
        // Draw inner base circle (light green)
        Paint innerBasePaint = new Paint(needlePaint);
        canvas.drawCircle(CENTER_X, CENTER_Y, NEEDLE_BASE_RADIUS * 0.7f, innerBasePaint);
        
        // Draw center dot (dark green)
        canvas.drawCircle(CENTER_X, CENTER_Y, NEEDLE_BASE_RADIUS * 0.3f, needleBasePaint);
    }
    
    /**
     * Set the current weight value and update needle position
     * @param weight Weight value in kg (0-150)
     */
    public void setWeight(float weight) {
        this.currentWeight = Math.max(0, Math.min(weight, MAX_VALUE));
        invalidate(); // Trigger redraw
    }
    
    /**
     * Get the current weight value
     * @return Current weight in kg
     */
    public float getWeight() {
        return currentWeight;
    }
    
    /**
     * Test method to verify needle positioning at specific weights
     * @param weight Test weight value
     * @return Calculated angle for debugging
     */
    public float getAngleForWeight(float weight) {
        float progress = weight / (float) MAX_VALUE;
        return START_ANGLE + progress * SWEEP_ANGLE;
    }
    
    /**
     * Animate needle to target weight
     * @param targetWeight Target weight in kg
     * @param duration Animation duration in milliseconds
     */
    public void animateToWeight(float targetWeight, long duration) {
        // Simple animation implementation
        float startWeight = currentWeight;
        float endWeight = Math.max(0, Math.min(targetWeight, MAX_VALUE));
        
        post(new Runnable() {
            private long startTime = System.currentTimeMillis();
            
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1f, elapsed / (float) duration);
                
                // Linear interpolation
                float currentAnimWeight = startWeight + (endWeight - startWeight) * progress;
                setWeight(currentAnimWeight);
                
                if (progress < 1f) {
                    postDelayed(this, 16); // ~60fps
                }
            }
        });
    }
}