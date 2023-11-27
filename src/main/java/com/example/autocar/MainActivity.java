package com.example.autocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity         {

    FirebaseAuth auth;
    FirebaseUser user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    LinearLayout annonceLayout;
    DatabaseReference dbFirebase;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("login-register-firebase-30ccd")
                .setApiKey("AIzaSyCv60GdoIlqqYc9tyGqEq4yF1Hb8UC-6aE")
                .setDatabaseUrl("https://login-register-firebase-30ccd-default-rtdb.europe-west1.firebasedatabase.app/")
                .build();

        FirebaseApp app = null;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(getApplicationContext());
        for (FirebaseApp firebaseApp : firebaseApps) {
            if (firebaseApp.getName().equals("UserNewAnnInstance")) {
                app = firebaseApp;
                break;
            }
        }

        if (app == null) {
            app = FirebaseApp.initializeApp(getApplicationContext(), options, "UserNewAnnInstance");
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(app);
        dbFirebase = firebaseDatabase.getReference("announcements");

        showFirstTimeHelpDialog();

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#94BDF2"));

        actionBar.setBackgroundDrawable(colorDrawable);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        //headerTextView = findViewById(R.id.textViewUserName);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        annonceLayout = findViewById(R.id.linearLayout);

        dbFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    annonceLayout.removeAllViews();

                    String currentUser = auth.getCurrentUser().getUid();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String pieceName = snapshot.child("PieceName").getValue(String.class);
                        String pieceDescription = snapshot.child("PieceDescription").getValue(String.class);
                        String piecePrice = snapshot.child("PiecePrice").getValue(String.class);
                        String productNumber = snapshot.child("ProductNumber").getValue(String.class);
                        String announcementId = snapshot.getKey();
                        String userId = snapshot.child("userId").getValue(String.class);

                        if (currentUser != null && !currentUser.equals(userId)) {
                            TextView annonceView = new TextView(MainActivity.this);
                            annonceView.setText(pieceName + "\n" + piecePrice);

                            Button buttonDetail = new Button(MainActivity.this);
                            buttonDetail.setText("Details");
                            buttonDetail.setBackgroundResource(R.drawable.button);
                            buttonDetail.setTextColor(Color.WHITE);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT);
                            buttonDetail.setLayoutParams(layoutParams);

                            String tag = announcementId + "|" + pieceName + "|" + piecePrice + "|" + productNumber + "|" + pieceDescription + "|" + userId;
                            buttonDetail.setTag(tag);
                            buttonDetail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String tag = (String) view.getTag();
                                    String[] parts = tag.split("\\|");
                                    String announcementIdStr = parts[0];
                                    String announcementTitle = parts[1];
                                    String announcementPrice = parts[2];
                                    String announcementProductNumber = parts[3];
                                    String announcementPieceDescription = parts[4];
                                    String announcementUserId = parts[5];

                                    Intent intent = new Intent(MainActivity.this, AnnouncementDetails.class);
                                    intent.putExtra("announcementId", announcementIdStr);
                                    intent.putExtra("announcementTitle", announcementTitle);
                                    intent.putExtra("announcementPrice", announcementPrice);
                                    intent.putExtra("announcementProductNumber", announcementProductNumber);
                                    intent.putExtra("announcementPieceDescription", announcementPieceDescription);
                                    intent.putExtra("announcementUserId", announcementUserId);

                                    startActivity(intent);
                                }
                            });

                            annonceLayout.addView(annonceView);
                            annonceLayout.addView(buttonDetail);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle error
            }
        });



        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.textViewUserName);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/" + userId);
        DatabaseReference vinRef = userRef.child("VIN");

        vinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userVIN = dataSnapshot.getValue(String.class);
                    emailTextView.setText(userVIN);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.d(TAG, databaseError.getMessage());
            }
        });



        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                    {
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.contact:
                    {
                        Toast.makeText(MainActivity.this, "Mes annonces", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MesAnnonces.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.galery:
                    {
                        Toast.makeText(MainActivity.this, "Mon adresse", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MonAdresse.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.about:
                    {
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), profil.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.logout:
                    {
                        Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                        break;
                    }

                }

                return false;
            }
        });

        user = auth.getCurrentUser();
        if(user == null)
        {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            //headerTextView.setText(user.getEmail());
        }

    }

    private void showFirstTimeHelpDialog() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Voici quelques instructions pour utiliser l'application :")
                .append("\n\n")
                .append("- A l'aide des trois barres en haut à gauche, vous pouvez naviguer dans l'application plus facilement.")
                .append("\n\n")
                .append("- En cliquant sur 'Détails' des annonces, vous aurez accès à plus d'informations.")
                .append("\n\n")
                .append("- Sur le profil, vous pourrez ajouter votre voiture à l'aide du VIN.")
                .append("\n\n")
                .append("- Vous pouvez également ajouter des annonces, votre adresse, voir vos annonces et vos offres depuis le profil.");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bienvenue sur Autocar")
                .setMessage(messageBuilder.toString())
                .setPositiveButton("Compris", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Fermez la boîte de dialogue
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}