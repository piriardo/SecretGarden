package com.example.andres.secretgarden;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Principal extends AppCompatActivity {

    Button btnNueva;
    String url="";
    String parametros="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        btnNueva= (Button) findViewById(R.id.btnNueva);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abrePrincipal = new Intent(Principal.this, Log.class);
                startActivity(abrePrincipal);
            }
        });
    }
}
