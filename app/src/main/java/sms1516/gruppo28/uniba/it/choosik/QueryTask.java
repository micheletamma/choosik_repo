package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.os.AsyncTask;

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
public class QueryTask extends AsyncTask<String,Void,String> {
    public Context context;
    public static ArrayList<String> risultato = new ArrayList<String>();



    public QueryTask(Context ctx){

        this.context=ctx;

    }

    public QueryTask() {
    }

    public ArrayList<String> getRisultato() {
        return risultato;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try{
            String query = (String)arg0[0];
            query = query.replace(" ", "%20");
            query=query.replace("'","%27");
            String link = "http://gruppotamma.esy.es/query.php?sql=" + query;

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
        super.onPostExecute(result);

    }


}
