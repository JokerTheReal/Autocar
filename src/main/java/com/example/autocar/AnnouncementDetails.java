package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.osmdroid.config.Configuration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.content.Context;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class AnnouncementDetails extends AppCompatActivity {

    TextView textViewTitle;
    Button faireUneOffre;
    EditText offre;
    TextView announceCreate;
    ArrayList<String> offersList = new ArrayList<>();
    ListView listViewOffers;

    private static final String CHANNEL_ID = "01";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_announcement_details);
        textViewTitle = findViewById(R.id.textViewTitle);
        faireUneOffre = findViewById(R.id.buttonFaireUneOffre);
        offre = findViewById(R.id.editTextOffre);
        announceCreate = findViewById(R.id.textViewAnnounceAuthor);
        //textViewOffre = findViewById(R.id.textViewLesOffres);
        listViewOffers = findViewById(R.id.listViewOffers);

        // Création du canal de notification (nécessaire pour les versions d'Android 8.0 et supérieures)
        createNotificationChannel();

        Intent intent = getIntent();
        String ID = intent.getStringExtra("announcementId");
        String title = intent.getStringExtra("announcementTitle");
        String price = intent.getStringExtra("announcementPrice");
        String number = intent.getStringExtra("announcementProductNumber");
        String description = intent.getStringExtra("announcementPieceDescription");
        String author = intent.getStringExtra("announcementUserId");
        textViewTitle.setText(title + "\n" + price + "\n" + number + "\n" + description + "\n");

        DatabaseReference offersRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("announcements").child(ID).child("offers");

        faireUneOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(AnnouncementDetails.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_offre)
                        .setContentTitle("Nouvelle offre")
                        .setContentText("Nouvelle offre sur l'annonce")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AnnouncementDetails.this);
                notificationManager.notify(0, builder.build());
                // Récupérer l'ID de l'utilisateur actuel
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userMail =  FirebaseAuth.getInstance().getCurrentUser().getEmail();

                // Récupérer l'ID de l'annonce
                Intent intent = getIntent();
                String announcementId = intent.getStringExtra("announcementId");

                // Créer une référence à l'emplacement de l'annonce dans la base de données
                DatabaseReference annonceRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("announcements").child(announcementId);

                // Récupérer l'offre faite par l'utilisateur
                String offreText = offre.getText().toString();

                // Ajouter la nouvelle offre à la base de données sous l'offre correspondante
                DatabaseReference offersRef = annonceRef.child("offers").push();
                offersRef.child("userId").setValue(userId);
                offersRef.child("userMail").setValue(userMail);
                offersRef.child("offreText").setValue(offreText);
                offersRef.child("status").setValue("en attente")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Afficher un message de succès
                                Toast.makeText(getApplicationContext(), "Offre ajoutée avec succès", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Afficher un message d'erreur
                                Toast.makeText(getApplicationContext(), "Erreur lors de l'ajout de l'offre : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                offersList.clear();
                // Parcourir les offres existantes
                for (DataSnapshot offerSnapshot : snapshot.getChildren()) {
                    String userId = offerSnapshot.child("userId").getValue(String.class);
                    String userMail = offerSnapshot.child("userMail").getValue(String.class);
                    String offreText = offerSnapshot.child("offreText").getValue(String.class);
                    String status = offerSnapshot.child("status").getValue(String.class);

                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String adresse = userSnapshot.child("Adresse").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);

                            // Maintenant, vous avez l'adresse et le nom d'utilisateur pour chaque offre.
                            Log.d("AnnouncementDetails", "Offre de " + userMail + " (" + adresse + ") : " + offreText + "€ (" + status + ")");
                            offersList.add("Offre de " + username + " (" + adresse + ") : " + offreText + "€ (" + status + ")");

                            // Mettez à jour l'adapter ici, dans la boucle, car Firebase est asynchrone.
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AnnouncementDetails.this, android.R.layout.simple_list_item_1, offersList);
                            listViewOffers.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("AnnouncementDetails", "Erreur lors de la récupération de l'adresse de l'utilisateur : " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Afficher un message d'erreur
                Log.e("AnnouncementDetails", "Erreur lors de la récupération des offres : " + error.getMessage());
            }
        });


        String userId = getIntent().getStringExtra("announcementUserId");

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(userId);

        // Récupérer les informations de l'utilisateur à partir de la base de données
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String adresse = snapshot.child("Adresse").getValue(String.class);

                announceCreate.setText("Annonce créée par " + username + " qui habite à " + adresse);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AnnouncementDetails", "Erreur lors de la récupération des informations de l'utilisateur : " + error.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nom du canal";
            String descriptionNotif = "Description du canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(descriptionNotif);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}