package sms1516.gruppo28.uniba.it.choosik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Entity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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

        final View rootView = inflater.inflate(R.layout.fragment_insert,container,false);
        final ListView listView =(ListView) rootView.findViewById(R.id.lista_tour_artista);

        populateTourList(listView);

        ImageButton imgBtn = (ImageButton) rootView.findViewById(R.id.imageButton);
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
                                populateTourList(listView);
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

    private void populateTourList(final ListView listView){
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
                    final String[] nomiTourArray = new String[tourArrayJson.length()];
                    final int[] idsTourArray =new  int[tourArrayJson.length()];
                    for (int i=0; i < tourArrayJson.length(); i++){
                        nomiTourArray[i] = tourArrayJson.getJSONObject(i).getString("nomeTour");
                        idsTourArray[i] = tourArrayJson.getJSONObject(i).getInt("id");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomiTourArray);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Bundle bundleTappe = new Bundle();
                            bundleTappe.putInt("idTour",idsTourArray[i]);
                            bundleTappe.putString("nomeTour", nomiTourArray[i]);
                            FragmentManager fm = getFragmentManager();
                            TourDetailFragment tourDetail = new TourDetailFragment();

                            tourDetail.setArguments(bundleTappe);
                            fm.beginTransaction()
                            .replace(R.id.relativelayoutforfragment,tourDetail)
                            .addToBackStack("tourDetail")
                            .commit();

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errore json", e.getMessage());
                }
            }


        });

    }
}
