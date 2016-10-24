package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
 * Created by Michele on 17/10/2016.
 */
public class SigninTask extends AsyncTask<String,Void,String> {
    private Context context;
    String user="";
    String psw="";
    String mail="";
    boolean artista=false;
    int cont=0;
    public SigninTask (Context ctx){
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String username = (String)arg0[0];
            String password = (String)arg0[1];
            user=username;
            psw=password;
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
            cont=0; //azzero il contatore per prelevare mail al secondo giro
            ArrayList<String> risultati = new ArrayList<String>();
            while ((line = in.readLine()) != null) {
                sb.append(line);
                risultati.add(line);
            }
            mail = risultati.get(1);
            if (risultati.get(2).contains("1")){
                artista = true;
            }
            in.close();
            return sb.toString();
        }

        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if (result.contains("Utente ok")){
            //utente correttamente autenticato
            /**
             * questo metodo setUserName, dopo aver convalidato il login, salva nelle preferenze
             * il nome utente, in modo che in un successivo avvio della app, rimane loggato.
             */
            SaveSharedPreference.setUserName(this.context, user);
            SaveSharedPreference.setEmail(this.context, mail);
            SaveSharedPreference.setIsArtist(this.context,artista);
            context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("Username",user).putExtra("Email",mail).putExtra("artista",artista));

        } else {
            //utente autenticato in modo errato
            Toast.makeText(context,"Autenticazione fallita",Toast.LENGTH_SHORT).show();

        }
    }



}
