package com.example.andres.secretgarden;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CrearPerfil extends AppCompatActivity {

    String url="";
    String parametros="";
    String rut;
    TextView txtRut;
    EditText txtHobbie, txtUserName, txtDescripcion, txtExpertiencia;
    Button btnCrearPerfil, btnInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_perfil);

        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtExpertiencia = (EditText) findViewById(R.id.txtExpericencia);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtHobbie = (EditText) findViewById(R.id.txtHobbie);
        btnCrearPerfil = (Button) findViewById(R.id.btnCrearPerfil);
        txtRut=(TextView) findViewById(R.id.txtRutRegis);
        String rut=getIntent().getExtras().getString("rut");
        txtRut.setText(rut);
        btnInicio= (Button) findViewById(R.id.btnIrPrincipal);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent abrePrincipal = new Intent(CrearPerfil.this, Principal.class);
                startActivity(abrePrincipal);

            }
        });

        btnCrearPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager connMg = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMg.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    String rut=getIntent().getExtras().getString("rut");
                    String userName = txtUserName.getText().toString();
                    String experiencia = txtExpertiencia.getText().toString();
                    String descripcion = txtDescripcion.getText().toString();
                    String hobbie = txtHobbie.getText().toString();


                    url = "http://192.168.43.68/LifeCultivate/crearPerfil.php";

                    parametros = "rut=" + rut + "&userName=" + userName + "&experiencia=" + experiencia + "&descripcion="+descripcion+ "&hobbie="+hobbie;

                    new CrearPerfil.SolicitaDatos().execute(url);


                } else {
                    Toast.makeText(getApplicationContext(), "No hay conexion", Toast.LENGTH_SHORT).show();
                }
            }}
        );
    }
        private class SolicitaDatos extends AsyncTask<String,Void, String> {

            @Override
            protected void onPostExecute(String resultado) {

                if(resultado.contains("registro_ok")){
                    String [] datos= resultado.split(",");

                    Toast.makeText(getApplicationContext(),"Su perfil a sido creado", Toast.LENGTH_SHORT).show();

                    Intent abrePrincipal = new Intent(CrearPerfil.this, Principal.class);
                    startActivity(abrePrincipal);

                }else if (resultado.contains("registro_error")){

                    Toast.makeText(getApplicationContext(),"hay un error", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"es otra mierda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(String... urls) {

                return Conexion.postDatos(urls[0], parametros);
            }
            @Override
            protected void onProgressUpdate(Void... values) {

                super.onProgressUpdate(values);
            }
        }


    }

