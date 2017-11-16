package com.example.andres.secretgarden;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class Conexion {

    public static String postDatos(String urlClase, String parametros ){

        URL url;
        HttpURLConnection connection=null;

        try {

            url = new URL(urlClase);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length","" + Integer.toString(parametros.getBytes().length));

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);


            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
            outputStreamWriter.write(parametros);
            outputStreamWriter.flush();


            InputStream inputStream= connection.getInputStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8") );
            String linea;
            StringBuffer respuesta= new StringBuffer();

            while ((linea= bufferedReader.readLine())!=null){
                respuesta.append(linea);
                respuesta.append('\r');

            }

            bufferedReader.close();
            return respuesta.toString();

        }catch (Exception error){

            return null;

        }finally {

            if(connection !=null){
                connection.disconnect();
            }

        }
    }

}
