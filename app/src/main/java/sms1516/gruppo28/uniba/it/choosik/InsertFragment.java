package sms1516.gruppo28.uniba.it.choosik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

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
                    String[] nomiTourArray = new String[tourArrayJson.length()];
                    for (int i=0; i < tourArrayJson.length(); i++){
                        nomiTourArray[i] = tourArrayJson.getJSONObject(i).getString("nomeTour");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomiTourArray);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("errore json", e.getMessage());
                }
            }


        });


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
                        RequestParams  tour2post = new RequestParams();
                        //HashMap<String,String> username = new HashMap();
                        try {
                            //username.put("username", SaveSharedPreference.getUserName(getContext()));
                            String username = "{\"username\":\"" + SaveSharedPreference.getUserName(getContext())+"\"},";
                            tour2post.put("artista", username);
                            tour2post.put("nomeTour", inputTourString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        client.post("http://exrezzo.pythonanywhere.com/api/tour/",tour2post, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.e("POST del tour ok", statusCode+"");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.e("POST del tour NOOOO", statusCode+" "+responseBody.toString()+ "" +
                                        error.getMessage());
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
}
