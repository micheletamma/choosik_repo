package sms1516.gruppo28.uniba.it.choosik;


import android.app.Dialog;
import android.content.Context;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcerto extends Fragment {
    Bundle bundle;
    int idTappa;
    boolean toUpdate;
    int positionToUpdate;
    int votoUpdate;

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

    private class JsonTask extends AsyncTask<String, Void, String> {
        JSONObject detailCanzoni;


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
                detailCanzoni = new JSONObject(buffer.toString());
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray arrayCanzoni = detailCanzoni.getJSONArray("objects");
                String titoloCanzone[] = new String[arrayCanzoni.length()];
                int[] idCanzoni = new int[arrayCanzoni.length()];
                for (int i = 0; i < titoloCanzone.length; i++) {
                    JSONObject temp = arrayCanzoni.getJSONObject(i);
                    titoloCanzone[i] = temp.getJSONObject("canzone").getString("titolo");
                    idCanzoni[i] = Integer.parseInt(temp.getJSONObject("canzone").getString("id"));
                }
                //adesso l'array titoloCanzone e' popolato dai titoli della tappa cliccata
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                //qui dentro gestisco il caso in cui questo concerto non abbia canzoni
            }

            super.onPostExecute(s);
        }


    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        bundle = this.getArguments();
        final String[] arrayCanzoniPassate = bundle.getStringArray("titoloCanzone");
        String nomeTappa = bundle.getString("nomeTappa");
        final int[] idCanzoniPassate = bundle.getIntArray("idCanzoni");
        final float [] mediaCanzoni = bundle.getFloatArray("mediaCanzoni");
        final int[] votoCanzoni = bundle.getIntArray("votoCanzone");
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        final ListView listview = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
        TextView tappa = (TextView) rootView.findViewById(R.id.tappa);
        tappa.setText(nomeTappa);
        idTappa = bundle.getInt("idTappa");
        final String[] canzoniVotate = bundle.getStringArray("canzoniVotate");
//        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        //definizione dell'adapter con una nostra lista di item che contiene anche il rating
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_rating, R.id.text_item, arrayCanzoniPassate) {
            //override del metodo getview per fare in modo che se le canzoni della tappa sono state
            //già votate dall'utente, l'elemento deve essere non cliccabile e deve avvisare che la
            //tappa è stata già votata

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_rating, parent, false);

                }
                RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
                TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                TextView textMedia = (TextView) convertView.findViewById(R.id.text_media);
                textItem.setText(arrayCanzoniPassate[position]);
                textMedia.setText("Voto medio: " + Float.toString(mediaCanzoni[position]));
                float indice = (float)votoCanzoni[position];

                if (canzoniVotate[position].contains("true")) {
                    ratingBar.setRating(votoCanzoni[position]);
                    textItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_true, 0);
                }
                if (toUpdate){
                    if (position==positionToUpdate){
                        toUpdate=false;
                        votoCanzoni[positionToUpdate]= votoUpdate;
                        ratingBar.setRating(votoUpdate);
                        textItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_true, 0);
                        canzoniVotate[position]="true";
                    }

                }

                return convertView;

            }
        };


    listview.setAdapter(adapter);


    listview.setOnItemClickListener(new AdapterView.OnItemClickListener()

    {
        @Override
        public void onItemClick (AdapterView < ? > parent, View view,final int position, long id){
        //prendo l'utente
        final String utente = SaveSharedPreference.getUserName(getContext());
        final String canzoneDaVotare = arrayCanzoniPassate[position];
        final Dialog dialog = new Dialog(getContext());
            if (!canzoniVotate[position].contains("true")) {
                dialog.setContentView(R.layout.rank_dialog);
                dialog.setCancelable(true);
                final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.dialog_ratingbar);
                Button button = (Button) dialog.findViewById(R.id.bottone_vota);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ratingBar.getRating() == 0) {
                            Toast.makeText(getContext(), "Devi esprimere un voto!", Toast.LENGTH_SHORT).show();
                        } else {
                            HashMap username = new HashMap();
                            username.put("username", utente);
                            HashMap canzoneInTappa = new HashMap();
                            canzoneInTappa.put("resource_uri", "/api/canzoneintappa/" + idCanzoniPassate[position] + "/");
                            //da implementare
                            params.put("utente", username);
                            params.put("votoNum", ratingBar.getRating());
                            params.put("canzoneInTappa", canzoneInTappa);
                            SimpleTask task = new SimpleTask();
                            task.execute("http://exrezzo.pythonanywhere.com/api/votocanzoneintappa/?format=json");
                            dialog.cancel();
                            ListView listview = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
                            toUpdate = true;
                            positionToUpdate = position;
                            votoUpdate=(int)ratingBar.getRating();
                            listview.setAdapter(listview.getAdapter());


                        }
                    }
                });

                dialog.show();



            } else {
                Toast.makeText(getContext(), "Hai gia' votato questa canzone", Toast.LENGTH_SHORT).show();
            }


    }
    }

    );

    return rootView;


}
}
