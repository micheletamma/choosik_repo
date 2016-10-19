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

/**
 * Created by Michele on 17/10/2016.
 */
public class SigninTask extends AsyncTask<String,Void,String> {
    private Context context;
    String user="";
    String psw="";
    String mail="";
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
            while ((line = in.readLine()) != null) {
                sb.append(line);
                if (cont==1){mail=line;}
                cont+=1;
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
            context.startActivity(new Intent(context, MainActivity.class).putExtra("Username",user).putExtra("Email",mail));

        } else {
            //utente autenticato in modo errato
            Toast.makeText(context,"Autenticazione fallita",Toast.LENGTH_SHORT).show();

        }
    }



}
