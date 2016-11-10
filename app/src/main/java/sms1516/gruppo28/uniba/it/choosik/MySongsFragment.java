package sms1516.gruppo28.uniba.it.choosik;

import android.app.Dialog;
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
import android.widget.Button;
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


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Michele on 06/11/2016.
 */
public class MySongsFragment extends Fragment {
    Utility utility= new Utility();



    public static class ViewHolder{
        ProgressBar progressBar;
        ImageButton imageButton;
        TextView textView;
    }

    ProgressDialog progress;

    public MySongsFragment(){}

    ListView listView;
    LayoutInflater inflater;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Le mie canzoni");

        View rootView = inflater.inflate(R.layout.fragment_mysongs,container,false);
        listView = (ListView) rootView.findViewById(R.id.list_songs);
        this.inflater = inflater;
        populateSongsList(listView, inflater);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertSong();

            }
        });
        fab.setVisibility(View.VISIBLE);
        return rootView;
    }

    Dialog addSongDialog;
    EditText songInput;
    Button insertSongBtn;
    ProgressBar progBar;
    private void insertSong(){
        addSongDialog = new Dialog(getContext());
        addSongDialog.setContentView(R.layout.mysong_insert_dialog);
        addSongDialog.setCancelable(true);
        addSongDialog.setTitle("Inserisci nuova canzone");
        addSongDialog.show();
        progBar = (ProgressBar) addSongDialog.findViewById(R.id.progressBar7);

        songInput = (EditText) addSongDialog.findViewById(R.id.input_song_text);
        insertSongBtn = (Button) addSongDialog.findViewById(R.id.insert_song_button);

        insertSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = songInput.getText().toString();
                String temp=utility.controlloInserimento(input);
                input=temp;
                if (input.isEmpty()){
                    Toast.makeText(getContext(), "Inserire un titolo!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AsyncHttpClient client = new AsyncHttpClient();
                StringEntity songEntity = null;
                JSONObject song2post = new JSONObject();
                try {
                    song2post.put("autore", new JSONObject().put("username", SaveSharedPreference.getUserName(getContext())));
                    song2post.put("titolo", input);
                    songEntity = new StringEntity(song2post.toString().replace("\\",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                client.post(getContext(),"http://exrezzo.pythonanywhere.com/api/canzone/",
                        songEntity,"application/json", new AsyncHttpResponseHandler(){
                            @Override
                            public void onStart() {
                                insertSongBtn.setEnabled(false);
                                progBar.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                populateSongsList(listView, inflater);
                                insertSongBtn.setEnabled(true);
                                progBar.setVisibility(View.GONE);
                                addSongDialog.cancel();
                                Toast.makeText(getContext(), "Canzone inserita!", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                insertSongBtn.setEnabled(true);
                                progBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Si è verificato un errore!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    ArrayList<String> titoliCanzoniList;
    ArrayList<Integer> idCanzoniList;

    int positionViewListSong;
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
                            //positionViewListSong = position;
                            final ViewHolder viewHolder;
                            if (convertView == null) {
                                convertView = inflater.inflate(R.layout.list_item_delete,parent,false);
                                ProgressBar progBarDelete = (ProgressBar) convertView.findViewById(R.id.progressBar7);
                                ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.delete_img);
                                TextView textItem = (TextView) convertView.findViewById(R.id.text_item);


                                viewHolder = new ViewHolder();
                                viewHolder.progressBar = progBarDelete;
                                viewHolder.imageButton = imgBtn;
                                viewHolder.textView = textItem;

                                convertView.setTag(viewHolder);
                            } else {
                                viewHolder = (ViewHolder) convertView.getTag();
                            }
                            viewHolder.textView.setText(titoliCanzoniList.get(position));

                            final int indexId2remove = titoliCanzoniList.indexOf(titoliCanzoniList.get(position));
                            final int id2remove = idCanzoniList.get(titoliCanzoniList.indexOf(titoliCanzoniList.get(position)));
                            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/canzone/" + id2remove+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/canzone/" + id2remove+"/",
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onStart() {
                                                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete canzone ok id:",titoliCanzoniList.indexOf(titoliCanzoniList.get(position))+"");
                                                    viewHolder.progressBar.setVisibility(View.GONE);
                                                    remove(getItem(titoliCanzoniList.indexOf(titoliCanzoniList.get(position))));
                                                    notifyDataSetChanged();
                                                    idCanzoniList.remove(indexId2remove);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    viewHolder.progressBar.setVisibility(View.GONE);
                                                    Log.e("Delete canzone NO id",titoliCanzoniList.indexOf(titoliCanzoniList.get(position))+"");
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
