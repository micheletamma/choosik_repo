package sms1516.gruppo28.uniba.it.choosik;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TourDetailFragment extends Fragment {

    Utility utility=new Utility();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dettagli Tour");
        Bundle bundleIdTour = getArguments();
        final int idTour = bundleIdTour.getInt("idTour");
        String nomeTour = bundleIdTour.getString("nomeTour");

        View rootView = inflater.inflate(R.layout.fragment_tour_detail, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.lista_tappe_view);
        TextView nomeTourTextView = (TextView) rootView.findViewById(R.id.nome_tour);
        nomeTourTextView.setText(nomeTour);

        populateTappeList(listView,inflater, idTour);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        FloatingActionButton imgBtn = (FloatingActionButton) rootView.findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Dialog insertTappaDialog = new Dialog(getContext());
                insertTappaDialog.setContentView(R.layout.tappa_insert_dialog);
                insertTappaDialog.setCancelable(true);
                insertTappaDialog.show();

                Button insertBtn = (Button) insertTappaDialog.findViewById(R.id.input_tappa_button);
                final EditText inputTappaCittaEditText = (EditText) insertTappaDialog.findViewById(R.id.input_tappa_citta_text);
                final TextView inputTappaDataViewText= (TextView) insertTappaDialog.findViewById(R.id.data_tappa_input_view);

                    /**
                 *  Calendario nel DIALOG
                 */
                final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(inputTappaDataViewText, myCalendar);
                    }

                };



                inputTappaDataViewText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(getContext(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });




                /**
                 * Click nel dialog per INSERIRE tappa
                 */

                insertBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String inputTappaCittaString = inputTappaCittaEditText.getText().toString();
                        String inputTappaDataString = inputTappaDataViewText.getText().toString();

                        AsyncHttpClient client = new AsyncHttpClient();
                        JSONObject tappa2postJson = new JSONObject();

                        JSONObject tourJson = new JSONObject();
                        StringEntity tappa2postEntity = null;
                        try {
                            tappa2postJson.put("tour", tourJson.put("id", idTour));
                            tappa2postJson.put("citta", inputTappaCittaString);
                            tappa2postJson.put("data", inputTappaDataString);
                            tappa2postEntity= new StringEntity(tappa2postJson.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        client.post(getContext(),"http://exrezzo.pythonanywhere.com/api/tappa/",tappa2postEntity,"application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.e("POST della tappa ok", statusCode+"");
                                populateTappeList(listView,inflater,idTour);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("POST del tour NOOOO", statusCode+" "+responseBody.toString()+ "" +
                                        error.getMessage());
                                Toast.makeText(getContext(), "Tappa non inserita: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        insertTappaDialog.cancel();
                    }
                });
            }
        });

        return rootView;
    }

    ArrayList<String> infoTappaList;
    ArrayList<Integer> idTappaList;
    private void populateTappeList(final ListView listView, final LayoutInflater inflater, int idTour) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://exrezzo.pythonanywhere.com/api/tappa/?tour__id=" +
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
                    infoTappaList = new ArrayList();
                    idTappaList = new ArrayList();
                    for (int i = 0; i < tappaArrayJson.length(); i++) {
                        infoTappaList.add("a " + tappaArrayJson.getJSONObject(i).getString("citta") +
                                " il " + tappaArrayJson.getJSONObject(i).getString("data"));
                        idTappaList.add(tappaArrayJson.getJSONObject(i).getInt("id"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, infoTappaList){
                        @NonNull
                        @Override
                        public View getView(final int position, View convertView, ViewGroup parent) {
                            if (convertView==null){
                                convertView = inflater.inflate(R.layout.list_item_delete,parent,false);
                            }

                            ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.delete_img);
                            TextView textItem = (TextView) convertView.findViewById(R.id.text_item);
                            textItem.setText(infoTappaList.get(position));
                            final ProgressBar progBar = (ProgressBar) convertView.findViewById(R.id.progressBar7);

                            imgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    Log.e("URL 2delete", "http://exrezzo.pythonanywhere.com/api/tappa/" + idTappaList.get(position)+"/");
                                    client.delete(getContext(), "http://exrezzo.pythonanywhere.com/api/tappa/" + idTappaList.get(position)+"/",
                                            new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onStart() {
                                                    progBar.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.e("Delete tappa ok id:",infoTappaList.get(position));
                                                    progBar.setVisibility(View.GONE);
                                                    remove(getItem(position));
                                                    notifyDataSetChanged();
                                                    idTappaList.remove(position);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.e("Delete tappa NO id",infoTappaList.get(position));
                                                }
                                            });
                                }
                            });
                            textItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle bundleTappe = new Bundle();
                                    bundleTappe.putInt("idTappa",idTappaList.get(position));
                                    bundleTappe.putString("infoTappa", infoTappaList.get(position));

                                    TappaDetailFragment tappaDetail = new TappaDetailFragment();

                                    tappaDetail.setArguments(bundleTappe);
                                    FragmentManager fm = getFragmentManager();
                                    utility.inserisciFragment(tappaDetail,fm);
                                }
                            });

                            return convertView;
                        }
                    };
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errore json", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST del tour NOOOO", statusCode + " " + responseString);
                Toast.makeText(getContext(), "Tour non inserito: si e' verificato un problema!", Toast.LENGTH_SHORT).show();
            }


        });



    }
    private void updateLabel (TextView textView, Calendar myCalendar) {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }
}
