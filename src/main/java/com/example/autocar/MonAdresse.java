package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;

public class MonAdresse extends AppCompatActivity implements TextView.OnEditorActionListener {

    EditText adresseUser;
    MapView map;

    Button buttonChercher;
    private IMapController mMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_adresse);

        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));

        buttonChercher = findViewById(R.id.buttonChercher);
        adresseUser = findViewById(R.id.editTextAdresse);
        adresseUser.setOnEditorActionListener(this);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("login-register-firebase-30ccd")
                .setApiKey(" AIzaSyCv60GdoIlqqYc9tyGqEq4yF1Hb8UC-6aE")
                .setDatabaseUrl("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("UserNewAnnInstance"));
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        mMapController = map.getController();

        String userId = currentUser.getUid();

        DatabaseReference userAddressRef = databaseReference.child("Users").child(userId).child("Adresse");

        userAddressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String address = dataSnapshot.getValue(String.class);
                    adresseUser.setText(address);

                    // Afficher l'adresse sur la carte
                    showAddressOnMap(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        buttonChercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditorAction(adresseUser, EditorInfo.IME_ACTION_SEARCH, null);

                String userId = currentUser.getUid();

                String location = adresseUser.getText().toString();

                DatabaseReference userRef = databaseReference.child("Users").child(userId);
                userRef.child("Adresse").setValue(location);

                Toast.makeText(MonAdresse.this, "Adresse enregistrée avec succès", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String location = adresseUser.getText().toString();

            List<Address> addressList = null;
            if (location != null && !location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    GeoPoint point = new GeoPoint(address.getLatitude(), address.getLongitude());
                    mMapController.setZoom(10);
                    mMapController.setCenter(point);

                    Marker marker = new Marker(map);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(marker);
                }
            }
            return true;
        }
        return false;
    }

    private void showAddressOnMap(String address) {
        List<Address> addressList = null;
        if (address != null && !address.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList != null && addressList.size() > 0) {
                Address addressObj = addressList.get(0);
                GeoPoint point = new GeoPoint(addressObj.getLatitude(), addressObj.getLongitude());
                mMapController.setZoom(10);
                mMapController.setCenter(point);

                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(marker);
            }
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        map.onPause();
    }
}
