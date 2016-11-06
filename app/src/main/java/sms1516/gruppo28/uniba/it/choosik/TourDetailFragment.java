package sms1516.gruppo28.uniba.it.choosik;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TourDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProgressDialog progress;

    public TourDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundleIdTour = getArguments();
        int idTour = bundleIdTour.getInt("idTour");
        String nomeTour = bundleIdTour.getString("nomeTour");

        View rootView = inflater.inflate(R.layout.fragment_tour_detail, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.lista_tappe_view);
        TextView nomeTourTextView = (TextView) rootView.findViewById(R.id.nome_tour);
        nomeTourTextView.setText(nomeTour);

        populateTappeList(listView, idTour);

        return rootView;
    }


    private void populateTappeList(final ListView listView, int idTour){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/tappa/?tour__id="+
                idTour, new JsonHttpResponseHandler() {
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
                    JSONArray tappaArrayJson = response.getJSONArray("objects");
                    String[] infoTappaArray = new String[tappaArrayJson.length()];
                    for (int i=0; i < tappaArrayJson.length(); i++){
                        infoTappaArray[i] =  "a "+tappaArrayJson.getJSONObject(i).getString("citta") +
                                " il "+  tappaArrayJson.getJSONObject(i).getString("data");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, infoTappaArray);
                    listView.setAdapter(adapter);
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            Bundle bundleTappe = new Bundle();
//                            bundleTappe.putInt("idTour",idsTourArray[i]);
//
//                            TourDetailFragment tourDetail = new TourDetailFragment();
//                            getFragmentManager()
//                                    .beginTransaction()
//                                    .replace(R.id.relativelayoutforfragment,tourDetail, tourDetail.getTag())
//                                    .addToBackStack(tourDetail.getTag())
//                                    .commit();
//
//                        }
//                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errore json", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST del tour NOOOO", statusCode+" "+responseString);
                Toast.makeText(getContext(), "Tour non inserito: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
            }



        });


}}
