package com.inaxdevelopers.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inaxdevelopers.weatherforecast.R;
import com.inaxdevelopers.weatherforecast.databinding.WeatherModelItemBinding;
import com.inaxdevelopers.weatherforecast.model.WeatherModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeatherModelAdapter extends RecyclerView.Adapter<WeatherModelAdapter.ViewHolder> {

    WeatherModelItemBinding binding;
    private final Context context;
    private final ArrayList<WeatherModel> arr;

    public WeatherModelAdapter(Context context, ArrayList<WeatherModel> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = WeatherModelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherModel model = arr.get(position);
        bind(model);
    }


    @Override
    public int getItemCount() {
        if (arr != null) {
            return arr.size();
        }
        return 0;
    }

    private void bind(WeatherModel model) {
        if (model.getPod().equals("d")) {
            binding.imgCardBG.setImageResource(R.drawable.day);
        } else {
            binding.imgCardBG.setImageResource(R.drawable.night);
        }
        binding.textTime.setText(model.getTime());
        binding.textTemp.setText(model.getTemp() + "°c");
        Picasso.get().load("https://openweathermap.org/img/w/" + model.getIcon() + ".png").into(binding.imgCondition);
        binding.textWSpeed.setText(model.getwSpeed() + " Km/h");

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        WeatherModelItemBinding binding;

        public ViewHolder(@NonNull WeatherModelItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
