package com.inaxdevelopers.weatherforecast.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.inaxdevelopers.weatherforecast.R;
import com.inaxdevelopers.weatherforecast.adapter.FavCityAdapter;
import com.inaxdevelopers.weatherforecast.adapter.WeatherModelAdapter;
import com.inaxdevelopers.weatherforecast.databinding.ActivityMainBinding;
import com.inaxdevelopers.weatherforecast.model.FavCityModel;
import com.inaxdevelopers.weatherforecast.model.WeatherModel;
import com.inaxdevelopers.weatherforecast.utils.NetworkCheck;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public static ArrayList<String> mycity = new ArrayList();

    static {
        System.loadLibrary("keys");
    }

    private final int PERMISSION_CODE = 1;
    ActivityMainBinding binding;
    String[] saveKey = {"CurrentWeatherData", "ForecastWeatherData", "New York", "Singapore", "Mumbai", "Delhi", "Sydney", "Melbourne"};
    double lat, lon;
    private FusedLocationProviderClient fusedLocationClient;
    //    String apiKey = new String(Base64.decode(getApiKey(), Base64.DEFAULT));
    private WeatherModelAdapter weatherModelAdapter;
    private FavCityAdapter favCityAdapter;
    private ArrayList<WeatherModel> arr;
    private ArrayList<FavCityModel> favArr;
    private LocationManager locationManager;
    private Location location;

    public native String getApiKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        arr = new ArrayList<>();
        favArr = new ArrayList<>();
        GetLocation();
        SetAdapter();
        boolean isInternetAvailable = NetworkCheck.isNetworkAvailable(getApplicationContext());
        if (isInternetAvailable) {
            for (int i = 2; i < saveKey.length; i++) {
                getFavCoord(saveKey[i], i);
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < saveKey.length; i++) {
                retrieveLastResponse(i);
            }
        }
        SetClicks();
    }

    private void GetLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationUpdates();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Toast.makeText(this, "Fetching Last known location", Toast.LENGTH_SHORT).show();
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        getCurrentWeather(lat, lon);
        getForecastWeather(lat, lon);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    private void SetAdapter() {
        weatherModelAdapter = new WeatherModelAdapter(this, arr);
        binding.rvWeather.setAdapter(weatherModelAdapter);

        favCityAdapter = new FavCityAdapter(this, favArr);
        binding.rvFavs.setAdapter(favCityAdapter);

    }

    private void SetClicks() {
        binding.historyCity.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
        binding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInternetAvailable = NetworkCheck.isNetworkAvailable(getApplicationContext());
                if (isInternetAvailable) {
                    String city = binding.editCityName.getText().toString();

                    if (city.equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter city Name", Toast.LENGTH_SHORT).show();
                        binding.editCityName.requestFocus();
                        binding.editCityName.setError("Please Enter City Name");
                    } else {

                        updateWeather(city);
                        if (!city.isEmpty()) {
                            mycity.add(city);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInternetAvailable = NetworkCheck.isNetworkAvailable(getApplicationContext());
                if (isInternetAvailable) {
                    Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                    getCurrentWeather(lat, lon);
                    getForecastWeather(lat, lon);
                    favArr.clear();
                    for (int i = 2; i < saveKey.length; i++) {
                        getFavCoord(saveKey[i], i);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
            }
        }, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateWeather(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + getApiKey() + "&units=metric";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("cod").equals("404")) {
                        Toast.makeText(MainActivity.this, "Please enter correct city name", Toast.LENGTH_LONG).show();
                    } else {
                        binding.textLastTime.setText(getCurrentTime());
                        lon = response.getJSONObject("coord").getDouble("lon");
                        lat = response.getJSONObject("coord").getDouble("lat");
                        getCurrentWeather(lat, lon);
                        getForecastWeather(lat, lon);
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Please enter correct city name", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fetch Error", "city not found");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private String getCurrentTime() {
        LocalDateTime dateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(formatter);
    }

    public void getCurrentWeather(double lat, double lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String urlCurrent = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + getApiKey() + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlCurrent, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                binding.textLastTime.setText(getCurrentTime());
                saveLastResponse(response, 0);
                binding.pBarLoading.setVisibility(View.GONE);
                binding.RLHome.setVisibility(View.VISIBLE);
                updateCurrentWeather(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fetch Error", error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void updateCurrentWeather(JSONObject response) {
        try {
            String city = response.getString("name");
            binding.textCityName.setText(city);
            String temperature = response.getJSONObject("main").getString("temp");
            binding.textTemp.setText(temperature + "°C");
            String img = response.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("icon");
            Picasso.get().load("https://openweathermap.org/img/w/" + img + ".png").into(binding.imgWeather);
            String condition = response.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("main");
            binding.textConditions.setText(condition);
            double wSpeed = response.getJSONObject("wind").getDouble("speed");
            binding.textWindSpeed.setText(wSpeed + "Km/h");
            if (img.charAt(img.length() - 1) == 'd')
                binding.imgBG.setImageResource(R.drawable.day);
            if (img.charAt(img.length() - 1) == 'n')
                binding.imgBG.setImageResource(R.drawable.night);
        } catch (Exception e) {
        }

    }

    private void getForecastWeather(double lat, double lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String urlForecast = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + getApiKey() + "&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlForecast, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                binding.textLastTime.setText(getCurrentTime());
                saveLastResponse(response, 1);
                arr.clear();
                updateForecastWeather(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fetch Error", error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void updateForecastWeather(JSONObject response) {
        try {
            int loop = response.getInt("cnt");
            JSONArray forecast = response.getJSONArray("list");

            for (int i = 0; i < loop; i += 1) {
                String time = forecast.getJSONObject(i).getString("dt_txt");
                double temp = forecast.getJSONObject(i).getJSONObject("main").getDouble("temp");
                String condition = forecast.getJSONObject(i)
                        .getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("icon");
                double wSpeed = forecast.getJSONObject(i)
                        .getJSONObject("wind")
                        .getDouble("speed");
                String pod = forecast.getJSONObject(i)
                        .getJSONObject("sys")
                        .getString("pod");
                arr.add(new WeatherModel(temp, condition, wSpeed, time, pod));
                if (i == 0) {
                    if (pod.equals("n")) {
                        binding.imgBG.setImageResource(R.drawable.night);
                    } else {
                        binding.imgBG.setImageResource(R.drawable.day);
                    }
                }
            }
            weatherModelAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("Update Res", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location loca) {
        location = loca;
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("Latitude", String.valueOf(lat));
        Log.d("Longitude", String.valueOf(lon));
        locationManager.removeUpdates(this);
    }

    private void saveLastResponse(JSONObject res, int time) {

        SharedPreferences sharedPreferences = getSharedPreferences("WeatherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            String save = res.toString();
            editor.putString(saveKey[time], save);
            editor.putString("Time", getCurrentTime());
            editor.apply();
        } catch (Exception e) {
            Log.d("Save Res", e.getMessage());
        }
    }


    private void retrieveLastResponse(int time) {
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherData", Context.MODE_PRIVATE);
        String weatherData = sharedPreferences.getString(saveKey[time], "");
        String dateTime = sharedPreferences.getString("Time", "");
        binding.textLastTime.setText(dateTime);
        try {
            JSONObject response = new JSONObject(weatherData);
            switch (time) {
                case 0:
                    updateCurrentWeather(response);
                    break;
                case 1:
                    updateForecastWeather(response);
                    break;
                case 2:
                    updateFavWeather(response);
                    break;
                default:
                    Log.d("retrieveLastResponse", "Wrong time");
                    break;
            }
        } catch (Exception ex) {
            Log.d("Load Res", ex.getMessage());
        }
    }

    public void getFavCoord(String name, int i) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + name
                + "&appid="
                + getApiKey()
                + "&units=metric";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    binding.textLastTime.setText(getCurrentTime());
                    double lon = response.getJSONObject("coord").getDouble("lon");
                    double lat = response.getJSONObject("coord").getDouble("lat");
                    getFavWeather(lat, lon, i);
                } catch (Exception ex) {
                    Log.d("getFavCoord", "Favorite City Fetch Failed");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fetch Error", error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getFavWeather(double lat, double lon, int i) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        String urlCurrent = "https://api.openweathermap.org/data/2.5/weather?lat="
                + lat
                + "&lon="
                + lon
                + "&appid="
                + getApiKey()
                + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlCurrent, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                saveLastResponse(response, i);
                updateFavWeather(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fetch Error", error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void updateFavWeather(JSONObject response) {
        try {
            String city = response.getString("name");
            String temperature = response.getJSONObject("main").getString("temp");
            String img = response.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("icon");
            String condition = response.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("main");
            double wSpeed = response.getJSONObject("wind")
                    .getDouble("speed");
            favArr.add(new FavCityModel(0, city, temperature, condition, String.valueOf(wSpeed), img));
        } catch (Exception e) {
            Log.d("Update Res", e.getMessage());
        }
        favCityAdapter.notifyDataSetChanged();
    }

}