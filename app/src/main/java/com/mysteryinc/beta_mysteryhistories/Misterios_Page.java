package com.mysteryinc.beta_mysteryhistories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton btnFav;
    ImageView img,img2;
    TextView tx, tx2;
    Boolean fav = false;

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misterios__page);

        String titulo = getIntent().getStringExtra("titulo");
        init();

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
        RevisarFav();
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fav){
                    Borrar();
                }else{
                    Registrar();
                }
            }
        });

    }

    public void init(){
        btnFav = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        tx = (TextView)findViewById(R.id.textView5);
        tx2 = (TextView)findViewById(R.id.textView7);
        img = (ImageView)findViewById(R.id.imageView4);
        img2 = (ImageView)findViewById(R.id.imageView3);

    }
    public void RevisarFav (){
        //Conexion a BD
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "mysterydb", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String titulo = tx.getText().toString();

        Cursor fila = BaseDeDatos.rawQuery("select * from misterios where titulo='"+titulo+"'", null);


        if(fila.getCount() > 0){
            fila.moveToFirst();
            btnFav.setImageResource(R.drawable.favorito_on);
            fav = true;
        }else{
            btnFav.setImageResource(R.drawable.favorito_off);
            fav = false;
        }

        BaseDeDatos.close();

    }
    public void CargarImagen(String url, String historia){


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



        Intent act = new Intent(this, Misterios_Map.class);
        act.putExtra("Lat", Cinco[0]);
        act.putExtra("Lng", Siete);
        act.putExtra("Nombre", tx.getText().toString());
        startActivity(act);
    }
    public void Registrar(){
        //Conexion a BD
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "mysterydb", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        //Recuperar valores
        String titulo = tx.getText().toString();
        String historia = tx2.getText().toString();
        final String[] ubicacion = new String[1];
        final String[] ciudad = new String[1];

        db.collection("misterios")
                .whereEqualTo("titulo", tx.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ubicacion[0] = document.getData().get("ubicacion").toString();
                                ciudad[0] = document.getData().get("ciudad").toString();
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
        String imagen = BitMapToString(bitmap);

        //Insertar en BD
        ContentValues registro = new ContentValues();
        registro.put("titulo", titulo);
        registro.put("historia",historia);
        registro.put("img", imagen);
        registro.put("ubicacion", ubicacion[0]);
        registro.put("ciudad",ciudad[0]);

        BaseDeDatos.insert("misterios", null, registro);
        btnFav.setImageResource(R.drawable.favorito_on);
        fav = true;
        BaseDeDatos.close();

    }
    public void Borrar(){
        //Conexion a BD
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "mysterydb", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String titulo = tx.getText().toString();
        int cant = BaseDeDatos.delete("misterios", "titulo='"+titulo+"'",null);
        BaseDeDatos.close();

        if (cant>0) {
            btnFav.setImageResource(R.drawable.favorito_off);
            fav = false;
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}