package com.inaxdevelopers.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inaxdevelopers.weatherforecast.R;
import com.inaxdevelopers.weatherforecast.databinding.FavcityBinding;
import com.inaxdevelopers.weatherforecast.model.FavCityModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavCityAdapter extends RecyclerView.Adapter<FavCityAdapter.ViewHolder> {
    FavcityBinding binding;
    private final Context context;
    private final ArrayList<FavCityModel> arr;

    public FavCityAdapter(Context context, ArrayList<FavCityModel> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = FavcityBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavCityModel favCityModel = arr.get(position);
        holder.bind(favCityModel);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FavcityBinding binding;

        public ViewHolder(@NonNull FavcityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavCityModel favCityModel) {
            binding.imgCardBG.setImageResource(R.drawable.favoritecard);
            binding.textCity.setText(favCityModel.getCity());
            binding.textTemperature.setText(favCityModel.getTemperature());
            binding.textCondition.setText(favCityModel.getCondition());
            binding.textWindSpeed.setText(favCityModel.getWindSpeed() + "Km/h");
            Picasso.get().load("https://openweathermap.org/img/w/" + favCityModel.getImgCondition() + ".png").into(binding.imgCondition);

        }
    }
}
