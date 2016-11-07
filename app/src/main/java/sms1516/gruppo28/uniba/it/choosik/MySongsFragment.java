package sms1516.gruppo28.uniba.it.choosik;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Michele on 06/11/2016.
 */
public class MySongsFragment extends Fragment {

    ProgressDialog progress;

    public MySongsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Le mie canzoni");

        View rootView = inflater.inflate(R.layout.fragment_mysongs,container,false);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_songs);
        populateSongsList(listView);


        return rootView;
    }

    private void populateSongsList(final ListView listView){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/canzone/?autore__username=" +
                SaveSharedPreference.getUserName(getContext()), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
                try {
                    JSONArray canzoniArrayJson = response.getJSONArray("objects");
                    String[] titoliCanzoniArray = new String[canzoniArrayJson.length()];
                    for (int i = 0; i < canzoniArrayJson.length(); i++) {
                        titoliCanzoniArray[i] = canzoniArrayJson.getJSONObject(i).getString("titolo");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titoliCanzoniArray);
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
            public void onStart() {
                progress = ProgressDialog.show(getActivity(), "I nostri criceti sono al lavoro...", "", true);
            }
        });
    }
}
