package com.example.autocar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingAnnouncement extends AppCompatActivity {

    EditText nomDeLaPiece;
    EditText descriptionDeLaPiece;
    EditText prixAMettre;
    EditText produitNumero;
    Button ajouterAnnonce;

    private DatabaseReference dbFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_announcement);

        nomDeLaPiece = findViewById(R.id.editTextNomDeLaPiece);
        descriptionDeLaPiece = findViewById(R.id.editTextDescription);
        prixAMettre = findViewById(R.id.editTextPrixAMettre);
        produitNumero = findViewById(R.id.editTextProduitNumero);
        ajouterAnnonce = findViewById(R.id.buttonAjouterAnnonce);

        String firebaseAppName = "UserNewAnnInstance";
        FirebaseApp firebaseApp = FirebaseApp.getInstance(firebaseAppName);

        if (FirebaseApp.getApps(getApplicationContext()).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("login-register-firebase-30ccd")
                    .setApiKey("AIzaSyCv60GdoIlqqYc9tyGqEq4yF1Hb8UC-6aE")
                    .setDatabaseUrl("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            firebaseApp = FirebaseApp.initializeApp(getApplicationContext(), options, firebaseAppName);
        } else {
            for (FirebaseApp app : FirebaseApp.getApps(getApplicationContext())) {
                if (app.getName().equals(firebaseAppName)) {
                    firebaseApp = app;
                    break;
                }
            }
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance(firebaseApp);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        DatabaseReference dbFirebase = firebaseDatabase.getReference();


        //dbFirebase = FirebaseDatabase.getInstance().getReference();

        ajouterAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namePiece = nomDeLaPiece.getText().toString().trim();
                String descriptionPiece = descriptionDeLaPiece.getText().toString().trim();
                String pricePiece = prixAMettre.getText().toString().trim();
                String productNumber = produitNumero.getText().toString().trim();

                if (!TextUtils.isEmpty(namePiece) && !TextUtils.isEmpty(descriptionPiece) && !TextUtils.isEmpty(pricePiece)) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference newAnnouncementRef = dbFirebase.child("announcements").push();
                    newAnnouncementRef.child("userId").setValue(userId);
                    newAnnouncementRef.child("PieceName").setValue(namePiece);
                    newAnnouncementRef.child("PieceDescription").setValue(descriptionPiece);
                    newAnnouncementRef.child("PiecePrice").setValue(pricePiece);
                    newAnnouncementRef.child("ProductNumber").setValue(productNumber);

                    Toast.makeText(AddingAnnouncement.this, "Announcement added", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddingAnnouncement.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }
        });




    }
}