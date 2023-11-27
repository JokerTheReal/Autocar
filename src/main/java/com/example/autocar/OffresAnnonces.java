package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OffresAnnonces extends AppCompatActivity {

    ListView listViewOffres;
    ArrayList<Button> boutonsOffres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offres_annonces);

        listViewOffres = findViewById(R.id.ListViewOffrePerAnnonce);
        boutonsOffres = new ArrayList<>();

        String annonceId = getIntent().getStringExtra("annonceId");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        DatabaseReference offresRef = databaseRef.child("announcements").child(annonceId).child("offers");

        offresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> offres = new ArrayList<>();
                String offreId = null;
                for (DataSnapshot offreSnapshot : dataSnapshot.getChildren()) {
                    offreId = offreSnapshot.getKey();
                    final DatabaseReference offreRef = offresRef.child(offreId);
                    Object offreObj = offreSnapshot.getValue();
                    if (offreObj instanceof Map) {
                        Map<String, String> offreMap = (Map<String, String>) offreObj;
                        String texteOffre = "Prix proposé : " + offreMap.get("offreText") + " " + offreMap.get("status") + "\n" +
                                "Commentaire : " + offreMap.get("userId");
                        offres.add(texteOffre);
                    }
                }

                OffresAdapter offresAdapter = new OffresAdapter(OffresAnnonces.this, offres, offresRef, offreId);
                listViewOffres.setAdapter(offresAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("OffresAnnonces", "Erreur lors de la récupération des offres : " + databaseError.getMessage());
            }
        });
    }
}