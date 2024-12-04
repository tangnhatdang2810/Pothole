package com.example.projectmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {
    private BarChart barChart;
    private PieChart pieChartLeft, pieChartRight;
    TextView todayCountValue, weeklyCountValue, monthlyCountValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home){
                    Intent intent = new Intent(DashBoardActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.user){
                    Intent intent = new Intent(DashBoardActivity.this, UpdateProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.setting){
                    Intent intent = new Intent(DashBoardActivity.this, SettingActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
        barChart = findViewById(R.id.barChart);
        pieChartLeft = findViewById(R.id.pieChartLeft);
        pieChartRight = findViewById(R.id.pieChartRight);
        todayCountValue = findViewById(R.id.todayCount);
        todayCountValue.setTooltipText("Số ổ gà ngày hôm nay");

        weeklyCountValue = findViewById(R.id.weeklyCount);
        weeklyCountValue.setTooltipText("Số ổ gà trong tuần");

        monthlyCountValue = findViewById(R.id.monthlyCount);
        monthlyCountValue.setTooltipText("Số ổ gà trong tháng");

        todayCountValue.setText("5");
        weeklyCountValue.setText("20");
        monthlyCountValue.setText("100");
        setupBarChart();
        setupPieCharts();
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1)); // Ngày 1
        entries.add(new BarEntry(1, 2)); // Ngày 2
        entries.add(new BarEntry(2, 3)); // Ngày 3
        entries.add(new BarEntry(3, 4)); // Ngày 4

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng ô gà phát hiện theo từng ngày");
        dataSet.setColor(Color.parseColor("#00BFFF")); // Màu cho cột
        dataSet.setValueTextColor(Color.WHITE); // Màu chữ hiển thị giá trị trên cột
        dataSet.setValueTextSize(10f); // Kích thước chữ hiển thị giá trị

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(50);
        // Thiết lập nhãn cho trục X
        String[] xLabels = new String[]{"Ngày 1", "Ngày 2", "Ngày 3", "Ngày 4"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setGranularity(1f); // Đặt độ phân giải cho trục X

        // Thiết lập trục Y để hiển thị số nguyên
        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setGranularity(1f); // Đặt độ phân giải cho trục Y bên trái
        leftYAxis.setGranularityEnabled(true); // Bật độ phân giải

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setGranularity(1f); // Đặt độ phân giải cho trục Y bên phải
        rightYAxis.setGranularityEnabled(true); // Bật độ phân giải

        barChart.invalidate(); // refresh
    }

    private void setupPieCharts() {
        // Biểu đồ bên trái: Mức độ nghiêm trọng của ổ gà
        ArrayList<PieEntry> pieEntriesLeft = new ArrayList<>();
        pieEntriesLeft.add(new PieEntry(30f, "Nghiêm trọng"));
        pieEntriesLeft.add(new PieEntry(70f, "Không nghiêm trọng"));

        PieDataSet pieDataSetLeft = new PieDataSet(pieEntriesLeft, "");
        pieDataSetLeft.setColors(new int[] {
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green)
        });
        pieDataSetLeft.setValueTextSize(12f);
        PieData pieDataLeft = new PieData(pieDataSetLeft);
        pieChartLeft.setData(pieDataLeft);
        pieChartLeft.getDescription().setEnabled(false);
        pieChartLeft.setDrawEntryLabels(false);
        pieChartLeft.setUsePercentValues(false);
        pieChartLeft.setDrawHoleEnabled(true);
        pieChartLeft.setTransparentCircleAlpha(0);
        pieChartLeft.setHoleColor(Color.WHITE);
        pieChartLeft.setCenterText("Mức độ nghiêm trọng"); // Thêm nhãn trung tâm
        pieChartLeft.animate();
        pieChartLeft.invalidate();

        // Biểu đồ bên phải: Phân vùng ổ gà
        ArrayList<PieEntry> pieEntriesRight = new ArrayList<>();
        pieEntriesRight.add(new PieEntry(50f, "Khu vực A"));
        pieEntriesRight.add(new PieEntry(30f, "Khu vực B"));
        pieEntriesRight.add(new PieEntry(20f, "Khu vực C"));

        PieDataSet pieDataSetRight = new PieDataSet(pieEntriesRight, "");
        pieDataSetRight.setColors(new int[] {
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.yellow),
                getResources().getColor(R.color.orange)
        });
        pieDataSetRight.setValueTextSize(12f);
        PieData pieDataRight = new PieData(pieDataSetRight);

        pieChartRight.getDescription().setEnabled(false);
        pieChartRight.setDrawEntryLabels(false);
        pieChartRight.setUsePercentValues(false);
        pieChartRight.setDrawHoleEnabled(true);
        pieChartRight.setTransparentCircleAlpha(0);
        pieChartRight.setHoleColor(Color.WHITE);
        pieChartRight.setCenterText("Phân vùng ổ gà"); // Thêm nhãn trung tâm
        pieChartRight.animate();
        pieChartRight.setData(pieDataRight);
        pieChartRight.invalidate();
    }

}