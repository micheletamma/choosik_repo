package sms1516.gruppo28.uniba.it.choosik;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
 * Created by marcouva on 31/10/16.
 */

public class MyConcertsFragment extends Fragment {

    JSONObject detailCanzoni;
    String nomeTappa;
    int idTappa;
    boolean flagListaCanzoniTask;
    Bundle titoli = new Bundle();

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
                if (flagListaCanzoniTask) {
                    flagListaCanzoniTask=false;
                    JSONArray arrayCanzoni = detailCanzoni.getJSONArray("objects");
                    String titoloCanzone[] = new String[arrayCanzoni.length()];
                    int[] idCanzoni = new int[arrayCanzoni.length()];
                    for (int i = 0; i < titoloCanzone.length; i++) {
                        JSONObject temp = arrayCanzoni.getJSONObject(i);
                        titoloCanzone[i] = temp.getJSONObject("canzone").getString("titolo");
                        idCanzoni[i] = Integer.parseInt(temp.getString("id"));
                    }
                    //adesso l'array titoloCanzone e' popolato dai titoli della tappa cliccata

                    titoli.putStringArray("titoloCanzone", titoloCanzone);
                    titoli.putString("nomeTappa", nomeTappa);
                    titoli.putIntArray("idCanzoni", idCanzoni);
                    titoli.putInt("idTappa", idTappa);
                    JsonTask taskVoto = new JsonTask();
                    taskVoto.execute("http://exrezzo.pythonanywhere.com/api/canzoniintappavotate/?format=json&idTappa="
                            + Integer.toString(idTappa) + "&username=" + SaveSharedPreference.getUserName(getContext()));
                } else {
                    JSONArray array = detailCanzoni.getJSONArray("objects");
                    String [] canzoniVotate = new String[array.length()];

                    int [] votoCanzone = new int [array.length()];

                    float [] media = new float [array.length()];

                    for (int i=0;i<array.length();i++){
                        canzoniVotate[i] = array.getJSONObject(i).getString("votata");

                        media[i] = Float.parseFloat(array.getJSONObject(i).getString("votoMedio"));
                        if (canzoniVotate[i].contains("true")){
                            votoCanzone[i] = Integer.parseInt(array.getJSONObject(i).getString("numVoto"));
                        } else {
                            votoCanzone[i]=0;
                        }
                    }

                    titoli.putFloatArray("mediaCanzoni",media);
                    titoli.putIntArray("votoCanzone",votoCanzone);

                    titoli.putStringArray("canzoniVotate",canzoniVotate);
                    FragmentManager manager = getFragmentManager();
                    DetailCanzoniConcerto detailCanzoniConcerto = new DetailCanzoniConcerto();
                    detailCanzoniConcerto.setArguments(titoli);
                    manager.beginTransaction().replace(R.id.relativelayoutforfragment,detailCanzoniConcerto).commit();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                //qui dentro gestisco il caso in cui questo concerto non abbia canzoni
            }

            super.onPostExecute(s);
        }



    }




    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String jsonArrayString = bundle.getString("jsonArrayTappe");

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);

        try {
            JSONArray jsonArrayTappe = new JSONArray(jsonArrayString);
            final String[] nomeTappeArray = new String[jsonArrayTappe.length()];
            final String[] luogoTappeArray = new String[jsonArrayTappe.length()];
            final String[] dataTappeArray = new String[jsonArrayTappe.length()];
            final int[] idTappe = new int[jsonArrayTappe.length()];
            for (int i = 0; i <= jsonArrayTappe.length() - 1; i++) {
                JSONObject temp = jsonArrayTappe.getJSONObject(i);
                idTappe[i] = Integer.parseInt(temp.getString("id"));
                nomeTappeArray[i]=temp.getJSONObject("tour").getString("nomeTour");
                luogoTappeArray[i] = temp.getString("citta");
                dataTappeArray[i] = temp.getString("data");

            }
            ArrayAdapter <String> nomiTappe = new ArrayAdapter <String>(getActivity(), R.layout.list_concert_item, R.id.nomeTour, nomeTappeArray){
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

            listview.setAdapter(nomiTappe);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //posso cercare le canzoni della tappa selezionata
                    String idToSearch = Integer.toString(idTappe[position]);
                    nomeTappa=nomeTappeArray[position];
                    //ricerco le canzoni della tappa con l'id uguale a idToSearch
                    MyConcertsFragment.JsonTask detailCanzoni = new MyConcertsFragment.JsonTask();
                    flagListaCanzoniTask=true;
                    idTappa=Integer.parseInt(idToSearch);
                    detailCanzoni.execute("http://exrezzo.pythonanywhere.com/api/canzoneintappa/?format=json&tappa__id="+idToSearch);
                }
            });




            ImageView emptyImg = (ImageView) rootView.findViewById(R.id.imgEmpty);
            TextView emptyTxt = (TextView) rootView.findViewById(R.id.txtEmpty);
            if (nomiTappe.isEmpty()){
                emptyImg.setVisibility(View.VISIBLE);
                emptyTxt.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return rootView;
    }
}
