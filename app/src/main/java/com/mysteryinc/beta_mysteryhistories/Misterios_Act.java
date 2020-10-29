package com.mysteryinc.beta_mysteryhistories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Misterios_Act extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner sp, sp2;
    int Cont = 0;
    String valor;
    Button boton;
    public List<String> lista;
    public String[] lista2;
    public ArrayAdapter <String> adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misterios_);

        sp = (Spinner)findViewById(R.id.spinner);
        sp.setOnItemSelectedListener(this);
        sp2 = (Spinner)findViewById(R.id.spinner2);
        sp2.setOnItemSelectedListener(this);
        sp2.setVisibility(View.GONE);
        boton = (Button)findViewById(R.id.button);
        boton.setVisibility(View.GONE);

        db.collection("provincias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lista = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData().get("nombre"));
                                lista.add(document.getData().get("nombre").toString());
                            }
                            Mostrar(lista);

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    public void Mostrar(List<String> lista){

        String[] lista2 = new String[lista.size()];
        for (int i =0; i < lista.size(); i++){
            lista2[i] = lista.get(i);
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista2);
        sp.setAdapter(adaptador);

    }
    public void Mostrar2(List<String> lista){
        sp2.setVisibility(View.VISIBLE);
        boton.setVisibility(View.VISIBLE);
        String[] lista2 = new String[lista.size()];
        for (int i =0; i < lista.size(); i++){
            lista2[i] = lista.get(i);
        }

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista2);
        sp2.setAdapter(adaptador);

    }
    public void AbrirLista(View v){
        //Crear intent
        Intent act = new Intent(this, Misterios_Lista.class);
        //Agregar datos al intent
        act.putExtra("datos", sp2.getSelectedItem().toString());
        //Lanzar segunda activity
        startActivity(act);
    }
    public void CargarCiudades(String prov){

        db.collection("ciudades")
                .whereEqualTo("provincia", prov)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lista = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("TAG222222", document.getId() + " => " + document.getData());
                                lista.add(document.getData().get("nombre").toString());
                            }
                            if (lista.size() != 0){
                                Mostrar2(lista);
                            }else{
                                boton.setVisibility(View.GONE);
                                sp2.setVisibility(View.GONE);
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()){
            case R.id.spinner:
                CargarCiudades(parent.getSelectedItem().toString());
                break;

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}