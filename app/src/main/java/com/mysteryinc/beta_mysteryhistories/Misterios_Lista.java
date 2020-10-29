package com.mysteryinc.beta_mysteryhistories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Misterios_Lista extends AppCompatActivity implements AdapterView.OnItemClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView lv;
    TextView tx;
    public List<String> lista;
    public ArrayAdapter <String> adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misterios__lista);

        lv = (ListView)findViewById(R.id.lv1);
        lv.setOnItemClickListener(this);
        tx = (TextView)findViewById(R.id.textView6);

        String ciudad = getIntent().getStringExtra("datos");

        db.collection("misterios")
                .whereEqualTo("ciudad", ciudad)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lista = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("TAG222222", document.getId() + " => " + document.getData());
                                lista.add(document.getData().get("titulo").toString());

                            }
                            if (lista.size() != 0){
                                Mostrar2(lista);
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void Mostrar2(List<String> lista){

        String[] lista2 = new String[lista.size()];
        for (int i =0; i < lista.size(); i++){
            lista2[i] = lista.get(i);
        }

        adaptador = new ArrayAdapter<String>(this,R.layout.list_item_perso, lista2);
        lv.setAdapter(adaptador);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Crear intent
        Intent act = new Intent(this, Misterios_Page.class);
        //Agregar datos al intent
        act.putExtra("titulo", lv.getItemAtPosition(position).toString());
        //Lanzar segunda activity
        startActivity(act);
    }
}