package com.juaracoding.contohpost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SqliteContoh extends AppCompatActivity {
Button btnSave, btnLoad;
EditText txtDari;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_contoh);

        txtDari = findViewById(R.id.txtDari);
       btnSave = findViewById(R.id.btnSave);
       btnSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ModelPost model = new ModelPost();
               Random rand = new Random();
               model.setId(rand.nextInt(1000));
               model.setDari(txtDari.getText().toString());
               model.setStatus("ok");
               model.setText("okdasdsd");
               model.setTime(new Date());
               model.save();
           }
       });


        btnLoad = findViewById(R.id.btnLoad);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ModelPost> model = SQLite.select()
                        .from(ModelPost.class)
                        .queryList();

                StringBuffer sb = new StringBuffer();
                for (int x = 0  ; x< model.size();x++){

                    sb.append(model.get(x).getDari() +"\n");
                }

                Toast.makeText(SqliteContoh.this,sb.toString(),Toast.LENGTH_LONG).show();
            }
        });







    }
}
