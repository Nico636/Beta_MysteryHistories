package com.mysteryinc.beta_mysteryhistories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Misterios_Page extends AppCompatActivity  {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView img,img2;
    TextView tx, tx2;

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misterios__page);

        String titulo = getIntent().getStringExtra("titulo");
        tx = (TextView)findViewById(R.id.textView5);
        tx2 = (TextView)findViewById(R.id.textView7);


        tx.setText(titulo);
        db.collection("misterios")
                .whereEqualTo("titulo", titulo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                CargarImagen(document.getData().get("img").toString(),document.getData().get("historia").toString());
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void CargarImagen(String url, String historia){

        img = (ImageView)findViewById(R.id.imageView4);
        img2 = (ImageView)findViewById(R.id.imageView3);
        Picasso.with(Misterios_Page.this).load(url).into(img);
        Picasso.with(Misterios_Page.this).load(url).into(img2);

        tx2.setText(historia);
    }

    public void AbrirMapa(View v){

        db.collection("misterios")
                .whereEqualTo("titulo", tx.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String coord = document.getData().get("ubicacion").toString();
                                AbrirMapa2(coord);
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    public void AbrirMapa2(String coord) {
        String Uno = coord.replace('{', ' ');
        String Dos = Uno.replace('}', ' ');
        String Tres = Dos.trim();
        String Cuatro = Tres.substring(20);
        String[] Cinco = Cuatro.split(",", 2);


        String Seis = Cinco[1].substring(11);
        String Siete = Seis.trim();
        //Toast.makeText(this,Siete+"    "+Cinco[0],Toast.LENGTH_SHORT).show();


        Intent act = new Intent(this, Misterios_Map.class);
        act.putExtra("Lat", Cinco[0]);
        act.putExtra("Lng", Siete);
        act.putExtra("Nombre", tx.getText().toString());
        startActivity(act);

    }
}