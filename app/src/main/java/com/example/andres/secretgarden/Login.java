package com.example.andres.secretgarden;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {
    EditText txtUser, txtPass, txtPrueba;
    TextView btnRegistro;
    Button btnLogin ;
    ImageView img;
    ProgressBar barraProgreso;
    String url="";
    String parametros="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        añadirVistas();
        //txtPrueba=(EditText) findViewById(R.id.txtPrueba) ;
        txtUser=(EditText) findViewById(R.id.txtRut);
        txtPass=(EditText) findViewById(R.id.txtPass);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        img=(ImageView) findViewById(R.id.logo);
        btnRegistro=(TextView) findViewById(R.id.btnRegistro);
        //barraProgreso.setVisibility(View.INVISIBLE);

        btnRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              Intent abrirRegist = new Intent(Login.this, Registro.class);
              startActivity(abrirRegist);
            }

        });


        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                barraProgreso.setVisibility(View.VISIBLE);

                ConnectivityManager connMg= (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo= connMg.getActiveNetworkInfo();

                if(networkInfo!= null && networkInfo.isConnected()){

                    String rut= txtUser.getText().toString();
                    String pass= txtPass.getText().toString();

                    if(rut.isEmpty() || pass.isEmpty()) {
                        barraProgreso.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"campos vacios", Toast.LENGTH_SHORT).show();

                    }
                    url="http://192.168.43.68/LifeCultivate/login.php";

                    parametros= "rut="+ rut + "&pass="+ pass;

                    new SolicitaDatos().execute(url);

                }else {
                    Toast.makeText(getApplicationContext(),"No hay conexion", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void añadirVistas() {

        btnLogin = (Button)findViewById(R.id.btnLogin);
        barraProgreso = (ProgressBar)findViewById(R.id.barraProgreso);
        barraProgreso.setProgress(0);
    }


    private class SolicitaDatos extends AsyncTask<String,Void, String> {

        @Override
        protected void onPostExecute(String resultado) {
try {
    if (resultado.contains("login_ok")) {

        String[] dato = resultado.split(",");
        //txtPrueba.setText(dato[0]+"-"+dato[1]+"-"+dato[2]);
        barraProgreso.setVisibility(View.INVISIBLE);
        Intent abrePrincipal = new Intent(Login.this, CrearPerfil.class);
       //String da= dato[2];
        if(dato[2].contains("Aceptado")){
        abrePrincipal.putExtra("rut", dato[1]);
        abrePrincipal.putExtra("tipo_user", dato[2]);

        startActivity(abrePrincipal);
        Toast.makeText(getApplicationContext(), dato[2], Toast.LENGTH_SHORT).show();
        }else if(dato[2].contains("Postulante")){
           Toast.makeText(getApplicationContext(),"Su solicitud esta en tramite", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Solicitud rechazada, favor revisar email", Toast.LENGTH_LONG).show();
        }
    } else if (resultado.contains("login_error")) {
        barraProgreso.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();

    }else{
        Toast.makeText(getApplicationContext(), "hay un error", Toast.LENGTH_SHORT).show();
    }
}catch(java.lang.NumberFormatException e){
    Toast.makeText(getApplicationContext(), "otro error", Toast.LENGTH_SHORT).show();
}

        }
            @Override
            protected void onPreExecute () {

                barraProgreso.setVisibility(View.VISIBLE);
            }


            @Override
            protected String doInBackground (String...urls){

                return Conexion.postDatos(urls[0], parametros);
            }

            @Override
            protected void onProgressUpdate (Void...values){

                super.onProgressUpdate(values);
            }
        }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}
