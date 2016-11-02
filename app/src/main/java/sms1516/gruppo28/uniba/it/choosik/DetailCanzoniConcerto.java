package sms1516.gruppo28.uniba.it.choosik;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLUE;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcerto extends Fragment {
    Bundle bundle;

    Map params = new HashMap();
    //Classe interna
    public class SimpleTask extends AsyncTask<String, Void, String> {
        public Context context;
        JSONObject risultato;


        public SimpleTask(Context ctx) {

            this.context = ctx;

        }

        public SimpleTask() {
        }

        public JSONObject getRisultato() {
            return risultato;
        }

        @Override
        protected String doInBackground(String... strings) {
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();

            //url with the post data
            HttpPost httpost = new HttpPost(strings[0]);

            //convert parameters into JSON object
            JSONObject holder = new JSONObject(params);

            //passes the results to a string builder/entity
            StringEntity se = null;
            try {
                se = new StringEntity(holder.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //sets the post request as the resulting string
            httpost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");
            String message = "ok";
            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            try {
                httpclient.execute(httpost, responseHandler);
            } catch (ClientProtocolException e) {
                Log.e("errore", e.getMessage());
                message = "permesso negato";
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("errore", e.getMessage());


            }

            return message;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            if (result.equals("ok")) {
//                Toast.makeText(getApplicationContext(), "Registrazione completata, effettua il login!", Toast.LENGTH_SHORT).show();
//                getApplicationContext()
//                        .startActivity(new Intent(getApplicationContext(), LoginActivity.class)
//                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Username gia' esistente!", Toast.LENGTH_SHORT).show();
//
//            }

        }
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        bundle = this.getArguments();
        final String[] arrayCanzoniPassate = bundle.getStringArray("titoloCanzone");
        String nomeTappa = bundle.getString("nomeTappa");
        final int[] idCanzoniPassate = bundle.getIntArray("idCanzoni");
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        final ListView listview = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
        TextView tappa = (TextView) rootView.findViewById(R.id.tappa);
        tappa.setText(nomeTappa);
        //simple_list_item_1 deve essere cambiato con una nostra lista di item da creare
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_rating,R.id.text_item, arrayCanzoniPassate){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_rating,parent,false);
                }
                TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                textItem.setText(arrayCanzoniPassate[position]);

                convertView.setEnabled(false);
                return convertView;
            }
        };
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //prendo l'utente
                final String utente = SaveSharedPreference.getUserName(getContext());
                final String canzoneDaVotare = arrayCanzoniPassate[position];


                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.rank_dialog);
                dialog.setCancelable(true);
                final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.dialog_ratingbar);
                Button button = (Button) dialog.findViewById(R.id.bottone_vota);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap username = new HashMap();
                        username.put("username", utente);
                        HashMap canzoneInTappa = new HashMap();
                        canzoneInTappa.put("resource_uri","/api/canzoneintappa/"+ idCanzoniPassate[position]+ "/");
                        //da implementare
                        params.put("utente",username);
                        params.put("votoNum",ratingBar.getRating());
                        params.put("canzoneInTappa",canzoneInTappa);
                        SimpleTask task = new SimpleTask();
                        task.execute("http://exrezzo.pythonanywhere.com/api/votocanzoneintappa/?format=json");
                        dialog.cancel();
                    }
                });

                dialog.show();


            }
        });

        return rootView;


    }
}
