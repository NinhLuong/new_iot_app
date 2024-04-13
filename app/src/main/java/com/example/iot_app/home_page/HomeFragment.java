package com.example.iot_app.home_page;

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
import com.example.iot_app.SharedViewModel;
import com.example.iot_app.StatusService;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private RecyclerView rcvData;
    // A private variable for RecyclerView, which lets you display data in a scrolling list.
    private RoomAdapter roomAdapter;

    // A private variable for RoomAdapter, which binds data to views that are displayed within a RecyclerView.
    private List<Room> listRoom;
    // A private variable for a list of Room objects.

    private MainActivity mainActivity;

    private SharedViewModel viewModel;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = new Intent(getActivity(), StatusService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(intent);
        } else {
            getActivity().startService(intent);
        }


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault());
        String formattedDate = now.format(formatter);

        mainActivity = (MainActivity) getActivity();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");
        Temperature = view.findViewById(R.id.numTemp);
        Humidity = view.findViewById(R.id.numHum);

        weatherIcon = view.findViewById(R.id.ic_weather);
        NameofCity = view.findViewById(R.id.textAddress);
        dateTime = view.findViewById(R.id.textDateTime);

        dateTime.setText(formattedDate);
        DatabaseReference tempRef = myRef.child(name).child("Temp");
        /*tempRef.addValueEventListener(new ValueEventListener() {
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

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(fragmentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }

        userName = view.findViewById(R.id.userName);
        rcvData = view.findViewById(R.id.rcv_data);

        userName.setText("Hi, " + mainActivity.getGusername());
        // Find a view that was identified by the 'rcv_data' id attribute in XML layout file and assign it to 'rcvData'.

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvData.setLayoutManager(gridLayoutManager);
        // Set 'rcvData' to use a linear layout manager (which arranges its children in a single column).
        //    Create a new DividerItemDecoration with current context and vertical orientation.

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//        viewModel.loadData(getContext());
        // Get an instance of SharedViewModel associated with this activity.

        // Observe changes to list of rooms in SharedViewModel.
        // The second parameter is an observer that gets notified when list of rooms changes.
        viewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<Room>>() {
            // This method is called when list of rooms changes.
            @Override
            public void onChanged(List<Room> rooms) {
                roomAdapter.setRooms(rooms);
                // Update rooms in 'roomAdapter'.
                roomAdapter.notifyDataSetChanged();
                // Notify 'roomAdapter' that underlying data has changed and it should refresh itself.
            }
        });

        // Find a view that was identified by 'btnAddRoom' id attribute in XML layout file and assign it to 'btnAddRoom'.
        FloatingActionButton btnAddRoom = view.findViewById(R.id.btnAddRoom);
        // Set an OnClickListener on 'btnAddRoom'. This listener gets notified when 'btnAddRoom' is clicked or tapped.
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            // This method is called when 'btnAddRoom' is clicked or tapped.
            @Override
            public void onClick(View v) {
                // Create a new dialog instance with current context.
                Dialog dialog = new Dialog(getContext());
                // Set the content view of this dialog. The layout resource is 'add_room_layout'.
                dialog.setContentView(R.layout.add_area_layout);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
                // Set the background of this dialog window using a drawable resource.

                EditText edtNameRoom = dialog.findViewById(R.id.edtNameArea);
                /*EditText edtLati= dialog.findViewById(R.id.edtLati);
                EditText edtLongi= dialog.findViewById(R.id.edtLongi);*/

                Button btnAdd = dialog.findViewById(R.id.btnAddArea);

// Set an OnClickListener on 'btnAdd'. This listener gets notified when 'btnAdd' is clicked or tapped.
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    // This method is called when 'btnAdd' is clicked or tapped.
                    @Override
                    public void onClick(View view) {


                        if(!edtNameRoom.getText().toString().equals("") ){
                            name = edtNameRoom.getText().toString();
                            Log.d("latitude firebase", latitude);
                            Log.d("longitude firebase", longitude);

                            //Assign the text in 'edtNameRoom' to 'name'.
                            Room newRoom = new Room(R.drawable.cold_storage, name , "0 device");
                            // Create a new Room object with default image, name from 'edtNameRoom', and "0 device".
                            myRef.child(name).child("Temp").setValue("null");
                            myRef.child(name).child("Hum").setValue("null");
                            myRef.child(name).child("SOS").setValue("false");
                            myRef.child(name).child("Longitude").setValue(longitude);
                            myRef.child(name).child("Latitude").setValue(latitude);

                            viewModel.addRoom(newRoom);
                            // Add the new room to SharedViewModel.
//                            viewModel.saveData(getContext());
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

        // Create a new RoomAdapter with list of rooms and assign it to 'roomAdapter'.
        roomAdapter = new RoomAdapter(listRoom);
        // Set the adapter for 'rcvData' to be 'roomAdapter'.
        rcvData.setAdapter(roomAdapter);

        return view;
    }

    private void startLocationUpdates() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the app has location permission again (just in case)
                // Check if the app has location permission again (just in case)
                if (ContextCompat.checkSelfPermission(fragmentContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationUpdates();
            }
            else{
                //user denied the permission
            }
        }
    }

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