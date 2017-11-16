package com.example.andres.secretgarden;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    private String APP_DIRECTORY = "myPictureApp/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String TEMPORAL_PICTURE_NAME = "temporal.jpg";

    private final int PHOTO_CODE = 100;
    private final int SELECT_FOTO = 200;
    private Uri output;
    private String foto;

    private ImageView verImagen;
    private EditText txtpath;

    private Button btnImg;
    private String encoded_string, image_name;
    private Bitmap bitmap;


    private File file;
    private Uri file_uri;


    String url = "";
    String parametros = "";
    EditText txtRut;
    EditText txtPass;
    EditText txtPass2;
    EditText txtEmail;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);
        txtRut = (EditText) findViewById(R.id.txtRut);
        txtPass = (EditText) findViewById(R.id.txtPass);
        txtPass2 = (EditText) findViewById(R.id.txtPass2);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        verImagen = (ImageView) findViewById(R.id.verImg);
        txtpath = (EditText) findViewById(R.id.txtRuta);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        verImagen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                builder.setTitle("Elegir una opcion");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {

                        if (options[seleccion] == "Tomar foto") {
                            openCamara();

                        } else if (options[seleccion] == "Elegir de galeria") {

                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_FOTO);

                        } else if (options[seleccion] == "Cancelar") {
                            dialog.dismiss();
                        }
                    }
                });

                builder.show();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ConnectivityManager connMg = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMg.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    String rut = txtRut.getText().toString();

                    String pass = txtPass.getText().toString();
                    String pass2 = txtPass2.getText().toString();
                    String tipo = "Postulante";
                    String email = txtEmail.getText().toString();


                    if (rut.isEmpty() || pass.isEmpty() || pass2.isEmpty() || email.isEmpty()) {

                        Toast.makeText(getApplicationContext(), "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        if (validarRut(rut) == true) {
                            if (pass.equals(pass2) && pass.length()>8) {
                                if(validarEmail(email)) {
                                    url = "http://192.168.43.68/LifeCultivate/registro.php";

                                    parametros = "rut=" + rut + "&pass=" + pass + "&tipo=" + tipo + "&email=" + email;

                                    new Registro.SolicitaDatos().execute(url);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Ingrese un email verdadero ", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Las contraseñas NO coinciden ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Rut invalido ", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No hay conexion", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    private class SolicitaDatos extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String resultado) {

            if (resultado.contains("registro_ok")) {
                String[] datos = resultado.split(",");

                Toast.makeText(getApplicationContext(), "Solicitud entregada, se notificara a su email", Toast.LENGTH_LONG).show();

                Intent abrePrincipal = new Intent(Registro.this, Login.class);
                startActivity(abrePrincipal);

            } else if (resultado.contains("registro_error")) {

                Toast.makeText(getApplicationContext(), "El RUT YA ESTA REGISTRADO", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "ERROR! en BD", Toast.LENGTH_SHORT).show();
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


    private boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher= pattern.matcher(email);

        return matcher.matches();
    }


    //Seleccionar foto//

    private void openCamara() {

        File file= new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();

        String path= Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;

        File newFile= new File(path);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case PHOTO_CODE:
                    String dir = Environment.getExternalStorageDirectory() + File.separator+ MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;
                    //String b64img = decodeBitmap(dir);
                    break;

                case SELECT_FOTO:
                    Uri path = data.getData();
                    String b64img = decodeBitmap(path);
                    verImagen.setImageURI(path);
                    txtpath.setText(b64img);
                    Toast.makeText(getApplicationContext(),b64img, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

    public static boolean  validarRut(String rut){
        boolean validacion = false;
        try{
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() -1));

            char dv = rut .charAt(rut.length() -1);
            int m = 0, s = 1;
            for (; rutAux !=0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if(dv ==(char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        }catch (java.lang.NumberFormatException e){

        }
        return validacion;
    }

    private String decodeBitmap(Uri dir) {
        try {
            if(dir == null){
                Toast.makeText(getApplicationContext(),"URI Invalida", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),dir.toString(), Toast.LENGTH_LONG).show();
                System.out.println("decodeBitmap dir: "+dir);
            }
            InputStream imageStream = getContentResolver().openInputStream(dir);

            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] b = baos.toByteArray();

            verImagen.setImageBitmap(bitmap);

            String b64 = Base64.encodeToString(b, Base64.DEFAULT);

            System.out.println("decodeBitmap b64: "+b64);

            return b64;
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
