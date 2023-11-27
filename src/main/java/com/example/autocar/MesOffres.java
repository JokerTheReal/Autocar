package com.example.autocar;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MesOffres extends AppCompatActivity {
    ListView offresListView;
    ArrayAdapter<String> offresListAdapter;
    List<String> offresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_offres);

        offresListView = findViewById(R.id.offres_list_view);

        offresList = new ArrayList<>();


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference announcementsRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("announcements");

        announcementsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
                    Object announcementValue = announcementSnapshot.getValue();
                    if (announcementValue instanceof Map) {
                        Map<String, Object> announcementMap = (Map<String, Object>) announcementValue;
                        if (announcementMap.containsKey("offers")) {
                            Map<String, Map<String, Object>> offersMap = (Map<String, Map<String, Object>>) announcementMap.get("offers");
                            for (Map.Entry<String, Map<String, Object>> entry : offersMap.entrySet()) {
                                Map<String, Object> offerMap = entry.getValue();
                                String offerUserId = (String) offerMap.get("userId");
                                if (offerUserId.equals(userId)) {
                                    String offre = "Annonce : " + announcementMap.get("PieceName") + " - offre faite : " + offerMap.get("offreText") + " €" + offerMap.get("status");
                                    offresList.add(offre);
                                    // break;
                                }
                            }
                        }
                    }
                }

                offresListAdapter = new ArrayAdapter<>(MesOffres.this, android.R.layout.simple_list_item_1, offresList);
                offresListView.setAdapter(offresListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
