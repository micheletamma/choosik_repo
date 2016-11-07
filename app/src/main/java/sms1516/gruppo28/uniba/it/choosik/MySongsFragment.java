package sms1516.gruppo28.uniba.it.choosik;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Michele on 06/11/2016.
 */
public class MySongsFragment extends Fragment {

    ProgressDialog progress;

    public MySongsFragment(){}

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mysongs,container,false);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_songs);
        populateSongsList(listView, inflater);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.VISIBLE);
        return rootView;
    }

    ArrayList<String> titoliCanzoniList;
    ArrayList<Integer> idCanzoniList;

    private void populateSongsList(final ListView listView, final LayoutInflater inflater){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/canzone/?autore__username=" +
                SaveSharedPreference.getUserName(getContext()), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
                try {
                    JSONArray canzoniArrayJson = response.getJSONArray("objects");
                    titoliCanzoniList = new ArrayList();
                    idCanzoniList = new ArrayList();
                    for (int i = 0; i < canzoniArrayJson.length(); i++) {
                        titoliCanzoniList.add(canzoniArrayJson.getJSONObject(i).getString("titolo"));
                        idCanzoniList.add(canzoniArrayJson.getJSONObject(i).getInt("id"));
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titoliCanzoniList){

                        @NonNull
                        @Override
                        public View getView(final int position,View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = inflater.inflate(R.layout.list_item_delete, parent, false);

                            }

                            ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.delete_img);
                            TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                            textItem.setText(titoliCanzoniList.get(position));

                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/canzone/" + idCanzoniList.get(position)+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/canzone/" + idCanzoniList.get(position)+"/",
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete canzone ok id:",titoliCanzoniList.get(position));
                                                    remove(getItem(position));
                                                    notifyDataSetChanged();
                                                    idCanzoniList.remove(position);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.e("Delete canzone NO id",titoliCanzoniList.get(position));
                                                }
                                            });
                                }
                            });

                            return convertView;
                        }
                    };
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Errore", e.getMessage());
                }
            }

            @Override
            public void onStart() {
                progress = ProgressDialog.show(getActivity(), "I nostri criceti sono al lavoro...", "", true);
            }
        });
    }
}
