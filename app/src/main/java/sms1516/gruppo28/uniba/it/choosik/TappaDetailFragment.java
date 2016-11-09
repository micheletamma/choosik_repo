package sms1516.gruppo28.uniba.it.choosik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class TappaDetailFragment extends Fragment {

    ProgressDialog progress;


    public TappaDetailFragment() {
        // Required empty public constructor
    }

    ArrayList<String> nomiCanzoni2add = new ArrayList<>();
    ArrayList<Integer> idCanzoni2add = new ArrayList<>();
    ListView canzoniArtista2addListView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dettagli Tappa");

        /**
         * Tramite bundle prendiamo le informazioni utili della tappa passata dal fragment
         * precedente
         */
        Bundle bundleTappa = getArguments();
        final int idTappa = bundleTappa.getInt("idTappa");
        String infoTappa = bundleTappa.getString("infoTappa");


        View rootView = inflater.inflate(R.layout.fragment_tappa_detail, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
        TextView infoTappaTextView = (TextView) rootView.findViewById(R.id.info_tappa);
        infoTappaTextView.setText(infoTappa);

        populateCanzoniList(listView,inflater,idTappa);

        ImageButton imgBtn = (ImageButton) rootView.findViewById(R.id.imageButton);

        imgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Dialog insertCanzoneDialog = new Dialog(getContext());
                insertCanzoneDialog.setContentView(R.layout.canzone_insert_dialog);
                insertCanzoneDialog.setCancelable(true);
                insertCanzoneDialog.setTitle("Inserisci canzone");
                insertCanzoneDialog.show();

                Button findBtn = (Button) insertCanzoneDialog.findViewById(R.id.input_canzone_button);
                final EditText inputCanzoneEditText = (EditText) insertCanzoneDialog.findViewById(R.id.input_canzone_text);
                /**
                 * Click nel dialog per INSERIRE canzoni in tappa
                 */

                findBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String inputCanzoneString = inputCanzoneEditText.getText().toString();
                        canzoniArtista2addListView = (ListView)insertCanzoneDialog.findViewById(R.id.lista_canzoni_artista);
                        final AsyncHttpClient client = new AsyncHttpClient();
                        Log.d("get canz2add", "http://exrezzo.pythonanywhere.com/api/canzone/?titoloContains="+
                                inputCanzoneString+"&autore__username="+
                                SaveSharedPreference.getUserName(getContext()));
                        client.get("http://exrezzo.pythonanywhere.com/api/canzone/?titoloContains="+
                                inputCanzoneString+"&autore__username="+
                                SaveSharedPreference.getUserName(getContext()), new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            JSONObject canzoneMeta = response.getJSONObject("meta");
                                            if (canzoneMeta.getInt("total_count") == 0){
                                                Toast.makeText(getContext(), "Nessun risultato!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            JSONArray canzoneArrayJson = response.getJSONArray("objects");

                                            if (!nomiCanzoni2add.isEmpty() && !idCanzoni2add.isEmpty()){
                                                nomiCanzoni2add.clear();
                                                idCanzoni2add.clear();
                                            }
                                            final ArrayList<Integer> idCanzoniAggiunteGia = new ArrayList<Integer>();
                                            for (int i = 0; i < canzoneArrayJson.length(); i++) {
                                                String titolo = canzoneArrayJson.getJSONObject(i).getString("titolo");
                                                int id = canzoneArrayJson.getJSONObject(i).getInt("id");
                                                if (idCanzoniOriginaliList.contains(id)){
                                                    Log.d("ahahahhahaa",titolo+" sta in tappa");
                                                    idCanzoniAggiunteGia.add(id);
                                                }
                                                nomiCanzoni2add.add(canzoneArrayJson.getJSONObject(i).getString("titolo"));
                                                idCanzoni2add.add(canzoneArrayJson.getJSONObject(i).getInt("id"));

                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomiCanzoni2add);
                                            canzoniArtista2addListView.setAdapter(adapter);
                                            canzoniArtista2addListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    if (idCanzoniOriginaliList.contains(idCanzoni2add.get(i))){
                                                        Toast.makeText(getContext(), "Canzone già inserita nella tappa!", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    JSONObject canzoneInTappa2post = new JSONObject();
                                                    StringEntity canzoneInTappa2postEntity = null;
                                                    try {
                                                        canzoneInTappa2post.put("canzone",new JSONObject().put("resource_uri", "/api/canzone/"+idCanzoni2add.get(i)+"/"));
                                                        canzoneInTappa2post.put("tappa", new JSONObject().put("resource_uri", "/api/tappa/"+idTappa+"/"));
                                                        canzoneInTappa2postEntity = new StringEntity(canzoneInTappa2post.toString().replace("\\",""));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    } catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                    Log.d("entity canzoneintappa", canzoneInTappa2postEntity.toString());
                                                    client.post(getContext(),"http://exrezzo.pythonanywhere.com/api/canzoneintappa/",
                                                            canzoneInTappa2postEntity,"application/json",
                                                            new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    Log.e("POST canzoneintappa ok", statusCode + "");
                                                                    populateCanzoniList(listView, inflater, idTappa);
                                                                    insertCanzoneDialog.cancel();
                                                                    Toast.makeText(getContext(), "Canzone inserita!", Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    Log.e("POST canzoneintappa no", statusCode + " " + responseBody.toString() + "" +
                                                                            error.getMessage());
                                                                    Toast.makeText(getContext(), "Canzone non inserita: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.e("Err listcanz2add", statusCode+"");
                                    }
                                }
                        );
                    }
                });
            }
        });


        return rootView;
    }

    /**
     * Metodo privato (simile a quello degli altri fragment) per il popolamento della listView
     * tramite ArrayList, ovviamente con get dal database
     */

    ArrayList<String> nomeCanzoniList;
    ArrayList<Integer> idCanzoniList;
    ArrayList<Integer> idCanzoniOriginaliList;
    private void populateCanzoniList(final ListView listView, final LayoutInflater inflater, int idTappa) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/canzoneintappa/?tappa__id=" +
                idTappa, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                progress = ProgressDialog.show(getActivity(), "I nostri criceti sono al lavoro...", "", true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                progress.dismiss();
                try {
                    JSONArray canzoneArrayJson = response.getJSONArray("objects");
                    nomeCanzoniList = new ArrayList<>();
                    idCanzoniList = new ArrayList<>();
                    idCanzoniOriginaliList= new ArrayList<>();
                    for (int i = 0; i < canzoneArrayJson.length(); i++) {
                        nomeCanzoniList.add(canzoneArrayJson.getJSONObject(i).getJSONObject("canzone").getString("titolo"));
                        idCanzoniList.add(canzoneArrayJson.getJSONObject(i).getInt("id"));
                        idCanzoniOriginaliList.add(canzoneArrayJson.getJSONObject(i).getJSONObject("canzone").getInt("id"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomeCanzoniList){
                        @NonNull
                        @Override
                        public View getView(final int position, View convertView, ViewGroup parent) {
                            if (convertView==null){
                                convertView = inflater.inflate(R.layout.list_item_delete,parent,false);
                            }

                            ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.delete_img);
                            TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                            final ProgressBar progBar = (ProgressBar) convertView.findViewById(R.id.progressBar7);
                            textItem.setText(nomeCanzoniList.get(position));

                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/canzoneintappa/" + idCanzoniList.get(position)+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/canzoneintappa/" + idCanzoniList.get(position)+"/",
                                            new AsyncHttpResponseHandler() {

                                                @Override
                                                public void onStart() {
                                                    progBar.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete canzone ok id:",nomeCanzoniList.get(position));
                                                    progBar.setVisibility(View.GONE);
                                                    remove(getItem(position));
                                                    notifyDataSetChanged();
                                                    idCanzoniList.remove(position);
                                                    //nomeCanzoniList.remove(position);
                                                    idCanzoniOriginaliList.remove(position);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.e("Delete canzone NO id",nomeCanzoniList.get(position));
                                                }
                                            });
                                }
                            });
                            return convertView;
                        }
                    };
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errore json", e.getMessage());
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e("errore", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST del tour NOOOO", statusCode + " " + responseString);
                Toast.makeText(getContext(), "Tour non inserito: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
            }


        });



    }

}
