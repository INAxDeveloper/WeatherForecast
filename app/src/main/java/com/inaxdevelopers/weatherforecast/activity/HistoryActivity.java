package com.inaxdevelopers.weatherforecast.activity;

import static com.inaxdevelopers.weatherforecast.activity.MainActivity.mycity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inaxdevelopers.weatherforecast.R;
import com.inaxdevelopers.weatherforecast.adapter.HistoryAdapter;
import com.inaxdevelopers.weatherforecast.databinding.ActivityHistoryBinding;

public class HistoryActivity extends AppCompatActivity {

    ActivityHistoryBinding binding;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setTitle("Your Search History");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(this, mycity);
        binding.historyRecyclerView.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();

    }
}