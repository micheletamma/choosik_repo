package sms1516.gruppo28.uniba.it.choosik;

import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by Michele on 02/11/2016.
 */
public class MainFragment extends Fragment {

    JSONObject detailCanzoni;
    String nomeTappa;
    int idTappa;
    boolean flagListaCanzoniTask;
    Bundle titoli = new Bundle();

    public MainFragment(){}

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
                    for (int i=0;i<array.length();i++){
                        canzoniVotate[i] = array.getJSONObject(i).getString("votata");
                    }

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



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        Bundle bundle = this.getArguments();
        String provincia = SaveSharedPreference.getProvincia(this.getContext());
        ImageView emptyImg = (ImageView) rootView.findViewById(R.id.imgEmpty);
        TextView emptyTxt = (TextView) rootView.findViewById(R.id.txtEmpty);
        TextView titolo = (TextView) rootView.findViewById(R.id.txtTitoloMainFragment);
        titolo.setText("Ecco i concerti nella provincia di \n" + provincia);
        //prendiamo atraverso il bundle i dati passati dalla MainActivity
        final String [] arrayConcertiVicini = bundle.getStringArray("nomeConcerto");
        final int[] arrayIdTappe = bundle.getIntArray("arrayIdTappe");
        ArrayAdapter<String> concertiVicini = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayConcertiVicini);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_vicini_view);
        listview.setAdapter(concertiVicini);
        //permette di cliccare sui concerti nelle vicinanze per vedere le canzoni e votarle
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String idToSearch = Integer.toString(arrayIdTappe[position]);
                nomeTappa=arrayConcertiVicini[position];
                //ricerco le canzoni della tappa con l'id uguale a idToSearch
                JsonTask detailCanzoni = new JsonTask();
                flagListaCanzoniTask=true;
                idTappa=Integer.parseInt(idToSearch);
                detailCanzoni.execute("http://exrezzo.pythonanywhere.com/api/canzoneintappa/?format=json&tappa__id="+idToSearch);
            }


        });

        if (concertiVicini.isEmpty()){
            emptyImg.setVisibility(View.VISIBLE);
            emptyTxt.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


}
