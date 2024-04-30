package com.example.iot_app.home_page;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iot_app.MainActivity;
import com.example.iot_app.R;
//import com.example.iot_app.SharedViewModel;
import com.example.iot_app.StatusService;
import com.example.iot_app.device.Device;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private RecyclerView rcvData;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> listRoom;
//    private ArrayList<Device>  listDevice;
    private HashMap<String, Device> hmDevice;

    private MainActivity mainActivity;

//    private SharedViewModel viewModel;
    private TextView userName;

    final String OpenWeatherAPI = "40f1a0e03d3137aa8d8d47e1e37ca0dc";
    final String Weather_URL = "https://api.openweathermap.org/data/2.5/weather";
    final String OpenCageAPI = "687509bf23494ebba9ba8a42b6f10bb4";
    final String openCageUrl = "https://api.opencagedata.com/geocode/v1/json";

    private String latitude ="" ;
    private String longitude ="" ;
    String name = "";

    TextView NameofCity, Temperature, Humidity, dateTime;
    ImageView weatherIcon;
    // Add a member variable to store the context
    private Context fragmentContext;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("rooms");
    DatabaseReference myRefBot = database.getReference("robot");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d("Room", String.valueOf(R.drawable.cold_storage));
        Log.d("Living Room", String.valueOf(R.drawable.living_room));
        Log.d("Bath Room", String.valueOf(R.drawable.bathroom));
        Log.d("Bed Room", String.valueOf(R.drawable.bedroom));
        Log.d("Kitchen Room", String.valueOf(R.drawable.kitchen_room));

        Intent intent = new Intent(getActivity(), StatusService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        } else {
            getActivity().startService(intent);

        }

        myRefBot.child("SOS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // if "SOS" does not exist, create it and set its value to "false"
                    myRefBot.child("SOS").setValue("false");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseDatabase error", "child SOS error!");
            }
        });

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new java.util.Date());

        mainActivity = (MainActivity) getActivity();

        Temperature = view.findViewById(R.id.numTemp);
        Humidity = view.findViewById(R.id.numHum);

        weatherIcon = view.findViewById(R.id.ic_weather);
        NameofCity = view.findViewById(R.id.textAddress);
        dateTime = view.findViewById(R.id.textDateTime);

        dateTime.setText(formattedDate);
        /*DatabaseReference tempRef = myRef.child(name).child("Temp");
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                String temp = dataSnapshot.getValue(String.class);
                if(temp != null && !temp.equals("null")){
                    if(Float.parseFloat(temp)> 32){
                        Log.d("txtTemp: ", String.valueOf(Float.parseFloat(temp)));
                        myRef.child(name).child("SOS").setValue("true");
                    }else if (Float.parseFloat(temp) < 32) {
                        myRef.child(name).child("SOS").setValue("false");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });*/

        userName = view.findViewById(R.id.userName);
        rcvData = view.findViewById(R.id.rcv_data);

        userName.setText("Hi");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvData.setLayoutManager(gridLayoutManager);

        /*viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<ArrayList<Room>>() {
            @Override
            public void onChanged(ArrayList<Room> rooms) {
                roomAdapter.setRooms(rooms);
                roomAdapter.notifyDataSetChanged();
            }
        });*/

        FloatingActionButton btnAddRoom = view.findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.add_area_layout);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                EditText edtNameRoom = dialog.findViewById(R.id.edtNameArea);


                Button btnAdd = dialog.findViewById(R.id.btnAddArea);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!edtNameRoom.getText().toString().equals("") ){
                            name = edtNameRoom.getText().toString();
                            Log.d("latitude firebase", latitude);
                            Log.d("longitude firebase", longitude);

//                            Room newRoom = new Room(R.drawable.cold_storage, name , "0 Thiết bị");
                            hmDevice = new HashMap<>();
                            Room newRoom1 = new Room(R.drawable.cold_storage, name , "0 Thiết bị", "null", "null","null", hmDevice);
                            myRef.child(name).setValue(newRoom1);
//                            viewModel.addRoom(newRoom);
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getContext(), "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                dialog.show();
            }
        });

        listRoom = new ArrayList<>();
        myRef.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listRoom.clear();
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {

                    Room room = roomSnapshot.getValue(Room.class);
                    listRoom.add(room);
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        roomAdapter = new RoomAdapter(listRoom);

        roomAdapter.notifyDataSetChanged();
        rcvData.setAdapter(roomAdapter);

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(fragmentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }

        if (ContextCompat.checkSelfPermission(fragmentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            startLocationUpdates();
        }

        return view;
    }

    private void startLocationUpdates() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the app has location permission again (just in case)
                if (ContextCompat.checkSelfPermission(fragmentContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Check if the "network" provider is available
                    try {
                        locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    } catch (IllegalArgumentException e) {
                        Log.e("Location", "Network provider does not exist", e);
                        // Handle the case where the network provider does not exist
                    }
                }
            }
        }, 2000);
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Get location
            latitude = String.valueOf(location.getLatitude());
            Log.d("latitude", latitude);
            longitude = String.valueOf(location.getLongitude());
            Log.d("longitude", longitude);
            myRefBot.child("Longitude").setValue(longitude);
            myRefBot.child("Latitude").setValue(latitude);

            // Get city information from latitude and longitude
            getCityInfo(latitude, longitude);
            // Get city information from latitude and longitude
            getWeatherData(latitude, longitude);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            // Handle provider enabled
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            // Handle provider disabled
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Handle status changed
        }
    };

    // Define this as a member variable
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startLocationUpdates();
                } else {
                    //user denied the permission
                }
            });

    final String nominatimUrl = "https://nominatim.openstreetmap.org/reverse";
    final String format = "json";

    private void getCityInfo(String latitude, String longitude) {
        final RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("format", format);

        final AsyncHttpClient client = new AsyncHttpClient();
        final String[] addressComponents = {""};
        final Handler handler = new Handler();

        // Set User-Agent header to avoid 429 Too Many Requests response from Nominatim
        client.addHeader("User-Agent", "YourApp");

        // Set the timeout for the HTTP client
        client.setTimeout(15000); // 15 seconds

        client.get(nominatimUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject address = response.getJSONObject("address");
                    String suburb = address.optString("suburb");
                    String city = address.optString("city");

                    // Concatenate suburb and city
                    if (suburb != null && !suburb.isEmpty()) {
                        addressComponents[0] += suburb + ", ";
                    }
                    if (city != null && !city.isEmpty()) {
                        addressComponents[0] += city;
                    }

                    // Remove trailing comma and space
                    if (addressComponents[0].endsWith(", ")) {
                        addressComponents[0] = addressComponents[0].substring(0, addressComponents[0].length() - 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI or perform any other actions with the address components
                            Log.d("address", "Address: " + addressComponents[0]);
                            // Use the concatenated addressComponents[0] as needed
                            NameofCity.setText(addressComponents[0]);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Nominatim", "Failed to get address components: " + errorResponse);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Handle failure or update UI accordingly
                    }
                });
            }
        });
    }

    private void getWeatherData(String latitude, String longitude) {
        Context context = getContext();

        if (context == null || !isAdded()) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", OpenWeatherAPI);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Weather_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!isAdded()) {
                    return;
                }

                weatherData weather = weatherData.fromJson(response);

                if (getActivity() != null) {
                    Temperature.setText(weather.getTemperature());
                    Humidity.setText(weather.getHumidity());

                    int resourceID = context.getResources().getIdentifier(weather.getWeatherIcon(), "drawable", context.getPackageName());
                    weatherIcon.setImageResource(resourceID);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (!isAdded()) {
                    return;
                }
            }
        });
    }

}