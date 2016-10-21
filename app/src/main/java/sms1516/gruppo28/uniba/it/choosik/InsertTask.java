package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Michele on 19/10/2016.
 */
public class InsertTask extends AsyncTask<String,Void,String> {
    public Context context;
    public static ArrayList<String> risultato = new ArrayList<String>();



    public InsertTask(Context ctx){

        this.context=ctx;

    }

    public InsertTask() {
    }

    public ArrayList<String> getRisultato() {
        return risultato;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String query = (String)arg0[0];
            String link = "http://gruppotamma.esy.es/insert.php?sql=" + query;

            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));


            StringBuffer sb = new StringBuffer("");
            String line="";
            while ((line = in.readLine()) != null) {
                sb.append(line);
                risultato.add(line);
            }


            in.close();
//            if (risultato.size()!=1){
//            risultato.remove(risultato.size()-1);}
            return sb.toString();
        }

        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
    @Override
    protected void onPostExecute(String result){
        //Registro i dati utente nel database
        super.onPostExecute(result);
        if (risultato.get(0).equals("Query ok")){
            context.startActivity(new Intent(context, LoginActivity.class));
            Toast.makeText(context,"Nuovo utente registrato. Effettua il login!",Toast.LENGTH_SHORT).show();
        }

    }


}
