package com.mysteryinc.beta_mysteryhistories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
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
    Switch Sw;
    public List<String> lista;
    public ArrayAdapter <String> adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misterios__lista);

        lv = (ListView)findViewById(R.id.lv1);
        lv.setOnItemClickListener(this);
        tx = (TextView)findViewById(R.id.textView6);
        Sw = (Switch)findViewById(R.id.switch1);


        Mostrar();
    }
    public void Mostrar(){
        String ciudad = getIntent().getStringExtra("datos");
        db.collection("misterios")
                .whereEqualTo("ciudad", ciudad)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        lista = new ArrayList<String>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("TAG222222", document.getId() + " => " + document.getData());
                                lista.add(document.getData().get("titulo").toString());

                            }
                            if (lista.size() != 0){
                                tx.setText("LISTA DE MISTERIOS");
                                Mostrar2(lista);
                            }

                        } else {
                            Sw.setChecked(false);
                            Mostrar2(CargarFav());
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
    public ArrayList<String> CargarFav (){
        //Conexion a BD
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "mysterydb", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String titulo = tx.getText().toString();

        Cursor fila = BaseDeDatos.rawQuery("select * from misterios" , null);
        ArrayList<String> lista = new ArrayList<String>();

        while(fila.moveToNext()){
            lista.add(fila.getString(0));
        }
        BaseDeDatos.close();
        tx.setText("LISTA DE FAVORITOS");
        return lista;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Crear intent
        Intent act = new Intent(this, Misterios_Page.class);
        //Agregar datos al intent
        act.putExtra("titulo", lv.getItemAtPosition(position).toString());
        //Lanzar segunda activity
        startActivity(act);
        //Toast.makeText(this, lv.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
    }

    public void AbrirFavs(View view) {
        if(view.getId() == R.id.switch1){
            if (Sw.isChecked()){
                Mostrar2(CargarFav());
            }else{
                Mostrar();
            }

        }
    }
}