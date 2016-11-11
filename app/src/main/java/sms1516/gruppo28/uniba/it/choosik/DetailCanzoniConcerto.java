package sms1516.gruppo28.uniba.it.choosik;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcerto extends Fragment {

    ArrayList<String> titoliCanzoniList;
    ArrayList<Integer> idCanzoniList;
    // ArrayList<String> votateCanzoniList;
    ArrayList<Float> mediaCanzoniList;
    ArrayList<Integer> votiCanzoniList;

    int idTappa;

    View rootView;
    ListView canzoniListView;
    LayoutInflater mInflater;
    Dialog rankDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String[] titoliCanzoniArray = bundle.getStringArray("titoloCanzone");
        int[] idCanzoniArray = bundle.getIntArray("idCanzoni");
        String[] votateCanzoniArray = bundle.getStringArray("canzoniVotate");
        float[] mediaCanzoniArray = bundle.getFloatArray("mediaCanzoni");
        int[] votiCanzoniArray = bundle.getIntArray("votoCanzone");

        titoliCanzoniList = new ArrayList<>();
        idCanzoniList = new ArrayList<>();
        //votateCanzoniList= new ArrayList<>();
        mediaCanzoniList = new ArrayList<>();
        votiCanzoniList = new ArrayList<>();

        for (int i = 0; i < titoliCanzoniArray.length; i++) {
            titoliCanzoniList.add(titoliCanzoniArray[i]);
            idCanzoniList.add(idCanzoniArray[i]);
            //votateCanzoniList.add(votateCanzoniArray[i]);
            mediaCanzoniList.add(mediaCanzoniArray[i]);
            votiCanzoniList.add(votiCanzoniArray[i]);
        }
        idTappa = bundle.getInt("idTappa");

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        canzoniListView = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
        mInflater = inflater;
        rankDialog = new Dialog(getContext());
        TextView titoloTappaTextView = (TextView) rootView.findViewById(R.id.tappa);

        titoloTappaTextView.setText(bundle.getString("nomeTappa"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_rating, R.id.text_item, titoliCanzoniList) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                convertView = mInflater.inflate(R.layout.list_item_rating, parent, false);


                TextView titoloCanzoneTextView = (TextView) convertView.findViewById(R.id.text_item);
                TextView mediaCanzoneTextView = (TextView) convertView.findViewById(R.id.text_media);
                RatingBar canzoneRatingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);

                titoloCanzoneTextView.setText(titoliCanzoniList.get(position));
                mediaCanzoneTextView.setText((Float.toString(mediaCanzoniList.get(position))));
                canzoneRatingBar.setRating(votiCanzoniList.get(position));

                Log.d("Voto riga", position + "numvoto" + votiCanzoniList.get(position));
                if (votiCanzoniList.get(position) == 0) {
                    Log.d("Voto diverso da 0", votiCanzoniList.get(position) + " titolo " + titoliCanzoniList.get(position));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rankDialog.setContentView(R.layout.rank_dialog);
                            rankDialog.setCancelable(true);
                            rankDialog.show();

                            final RatingBar ratingBarDialog = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
                            final Button votaButton = (Button) rankDialog.findViewById(R.id.bottone_vota);
                            votaButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("voto espresso", ratingBarDialog.getRating() + "");
                                    if (ratingBarDialog.getRating() == 0.0) {
                                        Toast.makeText(getContext(), "Devi esprimere un voto!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    JSONObject voto2post = new JSONObject();
                                    StringEntity voto2postEntity = null;
                                    try {
                                        voto2post.put("utente", new JSONObject().put("username", SaveSharedPreference.getUserName(getContext())));
                                        voto2post.put("votoNum", ratingBarDialog.getRating());
                                        voto2post.put("canzoneInTappa", new JSONObject()
                                                .put("resource_uri", "/api/canzoneintappa/" + idCanzoniList.get(position) + "/"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        voto2postEntity = new StringEntity(voto2post.toString().replace("\\", ""));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    client.post(getContext(), "http://exrezzo.pythonanywhere.com/api/votocanzoneintappa/",
                                            voto2postEntity, "application/json", new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onStart() {
                                                    votaButton.setEnabled(false);
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    votiCanzoniList.set(position, (int) ratingBarDialog.getRating());
                                                    rankDialog.cancel();
                                                    notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    votaButton.setEnabled(true);
                                                    Toast.makeText(getContext(), "Voto non espresso, riprova!", Toast.LENGTH_SHORT);
                                                    Log.e("errore post voto", "brutt");
                                                }
                                            });
                                }
                            });
                        }
                    });
                }

                Log.d("voto canzone", titoliCanzoniList.get(position) + votiCanzoniList.get(position));

                return convertView;
            }
        };

        canzoniListView.setAdapter(adapter);
        String nomeActivity = getActivity().getClass().getSimpleName();
        if (nomeActivity.equals("MainActivity")) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dettagli canzone");
        } else if (nomeActivity.equals("SearchActivity")) {
            ((SearchActivity) getActivity()).getSupportActionBar().setTitle("Dettagli canzone");
        }
        return rootView;
    }





    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    Fragment fragment = getFragmentManager().findFragmentByTag("concertListFragment");
                    //vuol dire che ho premuto il pulsante back
                    SaveSharedPreference.setContatore(getContext(),SaveSharedPreference.getContatore(getContext())-1);
                    }

                return false;
            }
        });
    }
}
