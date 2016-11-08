package sms1516.gruppo28.uniba.it.choosik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

/**
 * Created by Michele on 24/10/2016.
 */
public class InsertFragment extends Fragment {
    ProgressDialog progress;


    public InsertFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Inserisci Tour");

        final View rootView = inflater.inflate(R.layout.fragment_insert,container,false);
        final ListView listView =(ListView) rootView.findViewById(R.id.lista_tour_artista);

        populateTourList(listView, inflater);

        FloatingActionButton imgBtn = (FloatingActionButton) rootView.findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog insertTourDialog = new Dialog(getContext());
                insertTourDialog.setContentView(R.layout.tour_insert_dialog);
                insertTourDialog.setCancelable(true);
                insertTourDialog.show();

                Button insertBtn =(Button) insertTourDialog.findViewById(R.id.input_tour_button);
                final EditText inputTourEditText = (EditText) insertTourDialog.findViewById(R.id.input_tour_edit_text);
                insertBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String inputTourString = inputTourEditText.getText().toString();

                        AsyncHttpClient client = new AsyncHttpClient();
                        JSONObject tour2postJson = new JSONObject();
                        JSONObject artistaJson = new JSONObject();
                        StringEntity tour2postEntity = null;
                        try {
                            artistaJson.put("username", SaveSharedPreference.getUserName(getContext()));
                            tour2postJson.put("artista", artistaJson);
                            tour2postJson.put("nomeTour", inputTourString);
                            tour2postEntity= new StringEntity(tour2postJson.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        client.post(getContext(),"http://exrezzo.pythonanywhere.com/api/tour/",tour2postEntity,"application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.e("POST del tour ok", statusCode+"");
                                populateTourList(listView, inflater);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("POST del tour NOOOO", statusCode+" "+responseBody.toString()+ "" +
                                        error.getMessage());
                                Toast.makeText(getContext(), "Tour non inserito: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        insertTourDialog.cancel();

                    }
                });

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
    ArrayList<String> nomiTourList;
    ArrayList<Integer> idsTourList;

    private void populateTourList(final ListView listView,final LayoutInflater inflater){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/tour/?artista__username="+
                SaveSharedPreference.getUserName(getContext()), new JsonHttpResponseHandler() {
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
                    JSONArray tourArrayJson = response.getJSONArray("objects");
                    nomiTourList = new ArrayList();
                    idsTourList = new ArrayList();
                    for (int i=0; i < tourArrayJson.length(); i++){
                        nomiTourList.add(tourArrayJson.getJSONObject(i).getString("nomeTour"));
                        idsTourList.add(tourArrayJson.getJSONObject(i).getInt("id"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomiTourList){
                        @NonNull
                        @Override
                        public View getView(final int position, View convertView, ViewGroup parent) {
                            if (convertView==null){
                                convertView = inflater.inflate(R.layout.list_item_delete, parent, false);
                            }
                            ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.delete_img);
                            TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                            textItem.setText(nomiTourList.get(position));

                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/tour/" + idsTourList.get(position)+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/tour/" + idsTourList.get(position)+"/",
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete tour ok id:",nomiTourList.get(position));
                                                    remove(getItem(position));
                                                    notifyDataSetChanged();
                                                    idsTourList.remove(position);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.e("Delete tour NO id",nomiTourList.get(position));
                                                }
                                            });
                                }
                            });
                            textItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle bundleTappe = new Bundle();
                                    bundleTappe.putInt("idTour",idsTourList.get(position));
                                    bundleTappe.putString("nomeTour", nomiTourList.get(position));
                                    FragmentManager fm = getFragmentManager();
                                    TourDetailFragment tourDetail = new TourDetailFragment();

                                    tourDetail.setArguments(bundleTappe);
                                    fm.beginTransaction()
                                            .replace(R.id.relativelayoutforfragment,tourDetail)
                                            .addToBackStack("tourDetail")
                                            .commit();
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


        });

    }
}
