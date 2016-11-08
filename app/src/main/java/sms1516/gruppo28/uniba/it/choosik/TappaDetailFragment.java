package sms1516.gruppo28.uniba.it.choosik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class TappaDetailFragment extends Fragment {

    ProgressDialog progress;


    public TappaDetailFragment() {
        // Required empty public constructor
    }


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

        FloatingActionButton imgBtn = (FloatingActionButton) rootView.findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Dialog insertCanzoneDialog = new Dialog(getContext());
                insertCanzoneDialog.setContentView(R.layout.canzone_insert_dialog);
                insertCanzoneDialog.setCancelable(true);
                insertCanzoneDialog.show();

                Button insertBtn = (Button) insertCanzoneDialog.findViewById(R.id.input_canzone_button);
                final EditText inputCanzoneEditText = (EditText) insertCanzoneDialog.findViewById(R.id.input_canzone_text);







                /**
                 * Click nel dialog per INSERIRE tappa
                 */

                insertBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String inputCanzoneString = inputCanzoneEditText.getText().toString();

                        AsyncHttpClient client = new AsyncHttpClient();
                        JSONObject canzone2postJson = new JSONObject();

                        JSONObject tappaJson = new JSONObject();
                        StringEntity canzone2postEntity = null;
                        try {
                            canzone2postJson.put("tappa", tappaJson.put("id", idTappa));
                            canzone2postJson.put("titolo", inputCanzoneString);
                            canzone2postEntity= new StringEntity(canzone2postJson.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        client.post(getContext(),"http://exrezzo.pythonanywhere.com/api/canzoneintappa/",canzone2postEntity,"application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.e("POST della canzone ok", statusCode+"");
                                populateCanzoniList(listView,inflater,idTappa);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("POST canzone NOOOO", statusCode+" "+responseBody.toString()+ "" +
                                        error.getMessage());
                                Toast.makeText(getContext(), "Canzone non inserita: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        insertCanzoneDialog.cancel();
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
                    nomeCanzoniList = new ArrayList();
                    idCanzoniList = new ArrayList();
                    for (int i = 0; i < canzoneArrayJson.length(); i++) {
                        nomeCanzoniList.add(canzoneArrayJson.getJSONObject(i).getJSONObject("canzone").getString("titolo"));
                        idCanzoniList.add(canzoneArrayJson.getJSONObject(i).getInt("id"));
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
                            textItem.setText(nomeCanzoniList.get(position));

                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/canzoneintappa/" + idCanzoniList.get(position)+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/canzoneintappa/" + idCanzoniList.get(position)+"/",
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete canzone ok id:",nomeCanzoniList.get(position));
                                                    remove(getItem(position));
                                                    notifyDataSetChanged();
                                                    idCanzoniList.remove(position);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.e("Delete canzone NO id",nomeCanzoniList.get(position));
                                                }
                                            });
                                }
                            });
//                            textItem.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Bundle bundleTappe = new Bundle();
//                                    bundleTappe.putInt("idCanzone",idCanzoniList.get(position));
//                                    bundleTappe.putString("nomeCanzone", nomeCanzoniList.get(position));
//                                    FragmentManager fm = getFragmentManager();
//
//                                    TappaDetailFragment tappaDetail = new TappaDetailFragment();
//
//                                    tappaDetail.setArguments(bundleTappe);
//                                    fm.beginTransaction()
//                                            .replace(R.id.relativelayoutforfragment,tappaDetail)
//                                            .addToBackStack("tappaDetail")
//                                            .commit();
//                                }
//                            });

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
