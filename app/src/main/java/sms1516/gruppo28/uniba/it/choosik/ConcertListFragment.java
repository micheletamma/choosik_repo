package sms1516.gruppo28.uniba.it.choosik;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {
    LayoutInflater upperInflater;
    ViewGroup upperContainer;
    JSONObject detailCanzoni;
    String nomeTappa;

    public ConcertListFragment() {
        // Required empty public constructor
    }
    private class JsonTask extends AsyncTask<String,Void,String> {



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
                    buffer.append(line+"\n");
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
                String titoloCanzone []  = new String [arrayCanzoni.length()];
                int[] idCanzoni = new int[arrayCanzoni.length()];
               for (int i=0;i<titoloCanzone.length;i++){
                   JSONObject temp = arrayCanzoni.getJSONObject(i);
                   titoloCanzone[i]=temp.getJSONObject("canzone").getString("titolo");
                   idCanzoni[i] = Integer.parseInt(temp.getJSONObject("canzone").getString("id"));
               }
                //adesso l'array titoloCanzone e' popolato dai titoli della tappa cliccata
                Bundle titoli = new Bundle();
                titoli.putStringArray("titoloCanzone",titoloCanzone);
                titoli.putString("nomeTappa",nomeTappa);
                titoli.putIntArray("idCanzoni", idCanzoni);
                DetailCanzoniConcerto detailCanzoniConcerto = new DetailCanzoniConcerto();
                detailCanzoniConcerto.setArguments(titoli);





                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.search_container,detailCanzoniConcerto).commit();



            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                //qui dentro gestisco il caso in cui questo concerto non abbia canzoni
            }

            super.onPostExecute(s);
        }



    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //creazione di una view collegata a fragment_concert e di una listview collegata a
        //lista_concerti_view

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        upperInflater = inflater;
        upperContainer=container;
        TextView txtTitolo = (TextView) rootView.findViewById(R.id.txtTitoloConcertFragment);
        txtTitolo.setText("Lista concerti");
        /**
         * effettuo richiesta al database per ottenere dati
         */

        //bundle passato da MainActivity per avere i dati json dal database
        Bundle bundle = this.getArguments();
        String receive = bundle.getString("JsonTappaString");
        JSONArray listaTappe;
        try {
             listaTappe = new JSONArray(receive);
            //listaTappe e' un array di oggetti Json
            final String nomeTappe []  = new String [listaTappe.length()];

            final String[] nomeTappeArray = new String[listaTappe.length()];
            final String[] luogoTappeArray = new String[listaTappe.length()];
            final String[] dataTappeArray = new String[listaTappe.length()];


            final int idTappe [] = new int [listaTappe.length()];

            //ciclo per rendere gli oggetti json json come una array di stringhe
            for (int i=0; i <= listaTappe.length()-1;i++){
                JSONObject temp = listaTappe.getJSONObject(i);
                idTappe[i] = Integer.parseInt(temp.getString("id"));
                nomeTappe[i] = temp.getJSONObject("tour").getString("nomeTour") +
                        " a " + temp.getString("citta")
                        + " il " + temp.getString("data");
                //da json a stringa
                nomeTappeArray[i] = temp.getJSONObject("tour").getString("nomeTour");
                luogoTappeArray[i] = temp.getString("citta");
                dataTappeArray[i] = temp.getString("data");


                //creazione arrayadapter per trasformare i dati dell'array sottoforma di lista
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(), R.layout.list_concert_item,R.id.nomeTour, nomeTappe){
                    //override del metodo getview per fare in modo che nella view nome, luogo e
                    // data compaiano uno sotto l'altro
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = inflater.inflate(R.layout.list_concert_item,parent,false);
                        }

                        TextView nomeTour = (TextView) convertView.findViewById(R.id.nomeTour);
                        nomeTour.setText(nomeTappeArray[position]);
                        TextView luogoTour = (TextView) convertView.findViewById(R.id.luogoTour);
                        luogoTour.setText(luogoTappeArray[position]);
                        TextView dataTour = (TextView) convertView.findViewById(R.id.dataTour);
                        dataTour.setText(dataTappeArray[position]);

                        return convertView;
                    }
                };
                //la listview riceve i dati sotto forma di lista
                listview.setAdapter(adapter);

            }

            /**
             * Settaggio listener per click.
             * Nel momento in cui una tappa viene selezionata viene aperta la lista delle canzoni
             * presenti in quella tappa.
             */
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                    String tappaSelezionata = nomeTappe[position];
//                    StringBuilder sb = new StringBuilder(tappaSelezionata);
//                    sb.deleteCharAt(0);
//                    sb.deleteCharAt(sb.length()-1);
//                    tappaSelezionata = sb.toString();
                    //posso cercare le canzoni della tappa selezionata
                    String idToSearch = Integer.toString(idTappe[position]);
                    nomeTappa=nomeTappe[position];
                    //ricerco le canzoni della tappa con l'id uguale a idToSearch
                    JsonTask detailCanzoni = new JsonTask();
                    detailCanzoni.execute("http://exrezzo.pythonanywhere.com/api/canzoneintappa/?format=json&tappa__id="+idToSearch);
                    }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }





        //la view con tutti i dati
        return rootView;
    }


}
