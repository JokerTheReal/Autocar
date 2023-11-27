package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class profil extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    EditText editTextVinRecherche;
    TextView resultatRecherche;
    TextView resultatMailUser;
    TextView resultatVinExistant;
    Button buttonRechercher;
    Button buttonSauver;
    Button buttonMesAnnonces;
    Button buttonMonAdresse;
    Button buttonMesOffres;
    Button buttonBack;
    LinearLayout linearLayoutInfosProfil;
    RelativeLayout relativeLayoutAffichageVoiture;
    ImageView imageViewCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("login-register-firebase-30ccd")
                .setApiKey(" AIzaSyCv60GdoIlqqYc9tyGqEq4yF1Hb8UC-6aE")
                .setDatabaseUrl("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();
        FirebaseApp.initializeApp(getApplicationContext(), options, "UserNewInstance");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("UserNewInstance"));
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        editTextVinRecherche = findViewById(R.id.editTextVinNew);
        resultatMailUser = findViewById(R.id.textViewMailUser);
        resultatRecherche = findViewById(R.id.textViewResultatRecherche);
        resultatVinExistant = findViewById(R.id.textViewVinExistant);
        buttonRechercher = findViewById(R.id.buttonChercher);
        buttonSauver = findViewById(R.id.buttonSauver);
        buttonMesAnnonces = findViewById(R.id.buttonMesAnnonces);
        buttonMonAdresse = findViewById(R.id.buttonMonAdresse);
        linearLayoutInfosProfil = findViewById(R.id.linearLayout2);
        relativeLayoutAffichageVoiture = findViewById(R.id.relativeLayout1);
        buttonBack = findViewById(R.id.RetourChercher);
        imageViewCar = (ImageView) findViewById(R.id.my_carimage);

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        linearLayoutInfosProfil.startAnimation(slideUp);
        relativeLayoutAffichageVoiture.startAnimation(slideUp);


        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            String userId = user.getUid();
            DatabaseReference userRef = databaseReference.child("Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.hasChild("username")) {
                        String username = snapshot.child("username").getValue().toString();
                        resultatMailUser.setText("Bienvenue, " + username);
                    } else {
                        resultatMailUser.setText("Bienvenue !");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Erreur lors de la récupération des données de l'utilisateur.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("Users").child(userId).child("VIN");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    StringBuilder vinListBuilder = new StringBuilder();
                    for (DataSnapshot vinSnapshot : snapshot.getChildren()) {
                        String vin = vinSnapshot.getKey();
                        if (vin != null && !vin.isEmpty()) {
                            vinListBuilder.append(vin).append("\n");
                        }
                    }
                    String vinList = vinListBuilder.toString().trim();
                    if (!vinList.isEmpty()) {
                        resultatVinExistant.setText(vinList);
                        if (vinList != null && !vinList.isEmpty()) {
                            vinListBuilder.append(vinList).append("\n");
                            if (vinList.equals("BMW")) {
                                imageViewCar.setImageResource(R.drawable.carpng);
                            } else if (vinList.equals("MERCEDES-BENZ")) {
                                imageViewCar.setImageResource(R.drawable.mercedes);
                            } // Ajoute autant de conditions que nécessaire pour chaque VIN/image
                            else if (vinList.equals("AUDI")) {
                                imageViewCar.setImageResource(R.drawable.audi);
                            }
                            else {
                                imageViewCar.setImageResource(R.drawable.uncar);
                            }
                        }
           }
                    }
                else
                {
                    resultatVinExistant.setText("Aucune voiture enregistrée");
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'erreur si la récupération des données échoue
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profil.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonRechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hey");
                String vin = editTextVinRecherche.getText().toString();
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://car-api2.p.rapidapi.com/api/vin/" + vin)
                        .get()
                        .addHeader("X-RapidAPI-Key", "11b003394dmshf994c65b8a44902p1be895jsnab607763c63d")
                        .addHeader("X-RapidAPI-Host", "car-api2.p.rapidapi.com")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Car car = new Gson().fromJson(response.body().string(), Car.class);

                        if (car != null) {
                            resultatRecherche.setText(car.getSpecs().getCode());
                            resultatRecherche.setText(car.getMake());
                        } else {
                            resultatRecherche.setText("Erreur API: " + response.code() + " " + response.message());
                        }
                    } else {
                        resultatRecherche.setText("Erreur API");
                        Log.d("API Response", "Code: " + response.code() + ", Message: " + response.message());
                    }
                } catch (IOException e) {
                    String errorMessage = e.getMessage();
                    Log.e("API Error", errorMessage);
                }
            }
        });

        buttonSauver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = mAuth.getCurrentUser().getUid();
                String getVin = resultatRecherche.getText().toString();

                DatabaseReference vinRef = databaseReference.child("Users").child(userId).child("VIN");
                vinRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            databaseReference.child("Users").child(userId).child("VIN").child(getVin).setValue("");
                        }

                        DatabaseReference newVinRef = databaseReference.child("Users").child(userId).child("VIN").child(getVin);
                        DatabaseReference userRef = databaseReference.child("Users").child(userId).child("VIN").child(getVin);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot vinSnapshot : snapshot.getChildren()) {
                                        String vin = vinSnapshot.getKey(); // Récupère la clé du noeud VIN
                                        String resultatRecherche = vinSnapshot.child("resultatRecherche").getValue(String.class); // Récupère la valeur du champ "resultatRecherche"
                                        Log.d("TAG", "VIN: " + vin + " Résultat de recherche: " + resultatRecherche);
                                    }
                                } else {
                                    Log.d("TAG", "Aucun VIN trouvé pour cet utilisateur");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(profil.this, "Erreur lors de la récupération des VIN de l'utilisateur.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(profil.this, "Erreur lors de la vérification de l'existence du noeud 'VIN' dans la base de données.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        buttonMonAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profil.this, MonAdresse.class);
                startActivity(intent);
            }
        });

        buttonMesAnnonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupérer l'ID de l'utilisateur connecté
                String userId = auth.getCurrentUser().getUid();

                DatabaseReference annoncesRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("announcements");

                annoncesRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Map<String, String>> annoncesList = new ArrayList<>();

                        String annonceId = null;
                        for (DataSnapshot annonceSnapshot : dataSnapshot.getChildren()) {
                            Map<String, String> annonceData = (Map<String, String>) annonceSnapshot.getValue();

                            annoncesList.add(annonceData);
                        }

                        Intent intent = new Intent(profil.this, MesAnnonces.class);

                        intent.putExtra("annonces", (Serializable) annoncesList);

                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Gérer les erreurs lors de la récupération des données Firebase

                    }
                });
            }
        });
    }}