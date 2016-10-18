package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by Michele on 17/10/2016.
 */
public class SigninTask extends AsyncTask<String,Void,String> {
    private Context context;
    String risultato="";
    public SigninTask (Context ctx){
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String username = (String)arg0[0];
            String password = (String)arg0[1];

            Log.d("usr",username);
            Log.d("psw",password);
            String link = "http://gruppotamma.esy.es/login.php?username='"+username+"'&password='"+password + "'";

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
                break;
            }
            in.close();
            risultato=sb.toString();
            return sb.toString();
        }

        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if (result.equals("Utente ok")){
            //utente correttamente autenticato
            context.startActivity(new Intent(context, MainActivity.class));
        } else {
            //utente autenticato in modo errato
        }
    }



}
