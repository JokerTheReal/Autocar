package com.example.autocar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OffresAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> offres;
    private final DatabaseReference offreRef;
    private final String offreId;

    public OffresAdapter(Context context, ArrayList<String> offres, DatabaseReference offreRef, String offreId) {
        super(context, R.layout.offre_item, offres);
        this.context = context;
        this.offres = offres;
        this.offreRef = offreRef;
        this.offreId = offreId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.offre_item, parent, false);

        TextView offreTextView = rowView.findViewById(R.id.text_offre);
        offreTextView.setText(offres.get(position));



        Button btnAccepter = rowView.findViewById(R.id.btn_accepter);
        Button btnRefuser = rowView.findViewById(R.id.btn_refuser);


        btnAccepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offreRef.child("status").setValue("accepté")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Offre acceptée", Toast.LENGTH_SHORT).show();

                                DatabaseReference annonceRef = offreRef.getParent().getParent();
                                annonceRef.child("offersAllowed").setValue(false);

                                DatabaseReference offreDataRef = offreRef.child(offreId);
                                offreDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String status = dataSnapshot.child("status").getValue(String.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        btnRefuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offreRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                offres.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Offre refusée", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return rowView;
    }
}