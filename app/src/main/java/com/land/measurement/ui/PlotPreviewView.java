package com.land.measurement.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.land.measurement.R;
import com.land.measurement.model.CalculationMode;

import java.util.Locale;

public class PlotPreviewView extends View {

    private Paint borderPaint;
    private Paint fillPaint;
    private Paint diagonalPaint;
    private Paint textPaint;
    private Paint labelBgPaint;
    private Paint cornerPaint;
    private Paint cornerTextPaint;

    private double sideA = 40;
    private double sideB = 60;
    private double sideC = 40;
    private double sideD = 60;
    private double diagonal = 72.11;
    private String unitText = "ft";
    private CalculationMode mode = CalculationMode.HERON_IRREGULAR;

    public PlotPreviewView(Context context) {
        super(context);
        init();
    }

    public PlotPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlotPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int primaryGreen = ContextCompat.getColor(getContext(), R.color.pak_green_primary);
        int goldColor = ContextCompat.getColor(getContext(), R.color.pak_gold_accent);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(primaryGreen);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.argb(35, 11, 102, 35));
        fillPaint.setStyle(Paint.Style.FILL);

        diagonalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        diagonalPaint.setColor(goldColor);
        diagonalPaint.setStyle(Paint.Style.STROKE);
        diagonalPaint.setStrokeWidth(5f);
        diagonalPaint.setPathEffect(new DashPathEffect(new float[]{16, 12}, 0));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.pak_green_dark));
        textPaint.setTextSize(34f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        labelBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelBgPaint.setColor(Color.argb(235, 255, 255, 255));
        labelBgPaint.setStyle(Paint.Style.FILL);

        cornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerPaint.setColor(primaryGreen);
        cornerPaint.setStyle(Paint.Style.FILL);

        cornerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerTextPaint.setColor(Color.WHITE);
        cornerTextPaint.setTextSize(26f);
        cornerTextPaint.setFakeBoldText(true);
        cornerTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void updateDimensions(double a, double b, double c, double d, double diag, String unit, CalculationMode mode) {
        this.sideA = a > 0 ? a : 40;
        this.sideB = b > 0 ? b : 60;
        this.sideC = c > 0 ? c : 40;
        this.sideD = d > 0 ? d : 60;
        this.diagonal = diag;
        this.unitText = unit;
        this.mode = mode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        float padX = w * 0.16f;
        float padY = h * 0.18f;
        float plotW = w - 2 * padX;
        float plotH = h - 2 * padY;

        PointF p0 = new PointF(padX, padY);
        PointF p1 = new PointF(padX + plotW, padY + (mode == CalculationMode.HERON_IRREGULAR ? plotH * 0.08f : 0));
        PointF p2 = new PointF(padX + plotW * (mode == CalculationMode.HERON_IRREGULAR ? 0.92f : 1f), padY + plotH);
        PointF p3 = new PointF(padX * (mode == CalculationMode.HERON_IRREGULAR ? 1.2f : 1f), padY + plotH);

        // Draw Filled Polygon
        Path polygonPath = new Path();
        polygonPath.moveTo(p0.x, p0.y);
        polygonPath.lineTo(p1.x, p1.y);
        polygonPath.lineTo(p2.x, p2.y);
        polygonPath.lineTo(p3.x, p3.y);
        polygonPath.close();

        canvas.drawPath(polygonPath, fillPaint);
        canvas.drawPath(polygonPath, borderPaint);

        // Draw Diagonal if irregular mode
        if (mode == CalculationMode.HERON_IRREGULAR && diagonal > 0) {
            canvas.drawLine(p0.x, p0.y, p2.x, p2.y, diagonalPaint);

            float diagMidX = (p0.x + p2.x) / 2f;
            float diagMidY = (p0.y + p2.y) / 2f;
            String diagStr = String.format(Locale.US, "Diag: %.1f %s", diagonal, unitText);
            drawBadgeText(canvas, diagStr, diagMidX, diagMidY, true);
        }

        // Draw Corner Dots & Letters
        drawCorner(canvas, p0, "A");
        drawCorner(canvas, p1, "B");
        drawCorner(canvas, p2, "C");
        drawCorner(canvas, p3, "D");

        // Side A (Top)
        String labelA = String.format(Locale.US, "A: %.1f %s", sideA, unitText);
        drawBadgeText(canvas, labelA, (p0.x + p1.x) / 2f, (p0.y + p1.y) / 2f - 24f, false);

        // Side B (Right)
        String labelB = String.format(Locale.US, "B: %.1f %s", sideB, unitText);
        drawBadgeText(canvas, labelB, (p1.x + p2.x) / 2f + 32f, (p1.y + p2.y) / 2f, false);

        // Side C (Bottom)
        String labelC = String.format(Locale.US, "C: %.1f %s", sideC, unitText);
        drawBadgeText(canvas, labelC, (p3.x + p2.x) / 2f, (p3.y + p2.y) / 2f + 36f, false);

        // Side D (Left)
        String labelD = String.format(Locale.US, "D: %.1f %s", sideD, unitText);
        drawBadgeText(canvas, labelD, (p0.x + p3.x) / 2f - 32f, (p0.y + p3.y) / 2f, false);
    }

    private void drawCorner(Canvas canvas, PointF pt, String label) {
        float radius = 22f;
        canvas.drawCircle(pt.x, pt.y, radius, cornerPaint);
        float textOffset = (cornerTextPaint.descent() + cornerTextPaint.ascent()) / 2f;
        canvas.drawText(label, pt.x, pt.y - textOffset, cornerTextPaint);
    }

    private void drawBadgeText(Canvas canvas, String text, float cx, float cy, boolean isGold) {
        float textWidth = textPaint.measureText(text);
        float padH = 18f;
        float padV = 10f;
        RectF rect = new RectF(cx - textWidth / 2f - padH, cy - 24f - padV, cx + textWidth / 2f + padH, cy + 12f + padV);

        Paint bgPaint = new Paint(labelBgPaint);
        if (isGold) {
            bgPaint.setColor(Color.argb(245, 254, 243, 199));
        }
        canvas.drawRoundRect(rect, 12f, 12f, bgPaint);

        Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(2f);
        stroke.setColor(isGold ? ContextCompat.getColor(getContext(), R.color.pak_gold) : ContextCompat.getColor(getContext(), R.color.pak_light_card_border));
        canvas.drawRoundRect(rect, 12f, 12f, stroke);

        Paint tPaint = new Paint(textPaint);
        if (isGold) {
            tPaint.setColor(ContextCompat.getColor(getContext(), R.color.pak_gold));
        }
        canvas.drawText(text, cx, cy, tPaint);
    }
}
