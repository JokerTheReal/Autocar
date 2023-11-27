package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MesAnnonces extends AppCompatActivity {

    ListView listViewAnnonces;
    Button buttonAjouterAnnonce;
    Button buttonMesOffres;

    //List<Map<String, String>> annoncesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_annonces);
        listViewAnnonces = findViewById(R.id.annonces_list_view);
        buttonAjouterAnnonce = findViewById(R.id.buttonAjouterUneAnnonce);
        buttonMesOffres = findViewById(R.id.buttonMesOffres);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("login-register-firebase-30ccd")
                .setApiKey(" AIzaSyCv60GdoIlqqYc9tyGqEq4yF1Hb8UC-6aE")
                .setDatabaseUrl("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("UserNewAnnInstance"));
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        List<String> annoncesTextList = new ArrayList<>();

        List<Map<String, String>> annoncesList = (List<Map<String, String>>) getIntent().getSerializableExtra("annonces");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        Query annonceQuery = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference("announcements").orderByChild("userId").equalTo(currentUser.getUid());

        annonceQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                annoncesTextList.clear(); // Clear la liste pour éviter les doublons

                for (DataSnapshot annonceSnapshot : dataSnapshot.getChildren()) {
                    String annonceId = annonceSnapshot.getKey();
                    Map<String, String> annonce = (Map<String, String>) annonceSnapshot.getValue();

                    String texteAnnonce = "Titre : " + annonce.get("PieceName") + "\n" +
                            "Description : " + annonce.get("PieceDescription") + "\n" +
                            "Prix : " + annonce.get("PiecePrice") + " " + annonce.get("devise");

                    if (annonce.containsKey("offers")) {
                        Toast.makeText(MesAnnonces.this, "Test", Toast.LENGTH_SHORT).show();
                    }
                    annoncesTextList.add(texteAnnonce);

                    // Ajouter l'ID de l'annonce à la carte d'annonce
                    annonce.put("annonceId", annonceId);
                    // Gestion du clic sur "Voir les offres"
                    Button btnVoirOffres = new Button(MesAnnonces.this);
                    btnVoirOffres.setText("Voir les offres");
                    btnVoirOffres.setBackgroundResource(R.drawable.button);
                    btnVoirOffres.setTextColor(Color.WHITE);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT);
                    btnVoirOffres.setLayoutParams(layoutParams);

                    btnVoirOffres.setOnClickListener(v -> {
                        Intent intent = new Intent(MesAnnonces.this, OffresAnnonces.class);
                        intent.putExtra("annonceId", annonceId);
                        startActivity(intent);
                    });
                    listViewAnnonces.addFooterView(btnVoirOffres); // Ajouter le bouton à la liste d'annonces
                }

                ArrayAdapter<String> annoncesAdapter = new ArrayAdapter<>(MesAnnonces.this,
                        android.R.layout.simple_list_item_1, annoncesTextList);
                listViewAnnonces.setAdapter(annoncesAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MesAnnonces", "Erreur lors de la récupération des annonces : " + databaseError.getMessage());
            }
        });

        buttonMesOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MesAnnonces.this, MesOffres.class);
                    startActivity(intent);
            }
        });

        buttonAjouterAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = auth.getCurrentUser().getUid();
                DatabaseReference userRef = databaseReference.child("Users").child(userId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasVin = false;
                        for (DataSnapshot vinSnapshot : snapshot.getChildren()) {
                            String vin = vinSnapshot.getKey();
                            if (vin != null && !vin.isEmpty()) {
                                hasVin = true;
                                break;
                            }
                        }

                        if (hasVin) {
                            Intent intent = new Intent(getApplicationContext(), AddingAnnouncement.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Vous devez ajouter au moins un VIN avant de pouvoir ajouter une annonce.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

    }}
