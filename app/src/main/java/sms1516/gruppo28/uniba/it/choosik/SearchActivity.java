package sms1516.gruppo28.uniba.it.choosik;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;


public class SearchActivity extends AppCompatActivity
{
    /**
     * Si implementa un Date Picker per gestire le date in input
     */

    TextView mDateDisplay;
    Button mPickDate;
    JSONObject concertResult;
    Bundle receiver;
    public static int contatore=0;
    int mYear;
    int mMonth;
    int mDay;
    Boolean isBtnDatePressed;

    private class JsonTask extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute() {
            if (isOnline()){
                super.onPreExecute();}
            else {
                checkConnections();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
                concertResult = new JSONObject(buffer.toString());
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray arrayTappe = concertResult.getJSONArray("objects");
                if (arrayTappe.length()==0){
                    //vuol dire che non esistono concerti per questa ricerca
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                            dialog.setTitle("Ricerca errata")
                            .setMessage("Non esiste alcun concerto con questi parametri di ricerca.\nProva ad usare i nostri suggerimenti!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })

                            .setIcon(R.drawable.metal_music_48px)
                            .show();
                } else {
                    String tappe []  = new String [arrayTappe.length()];
                    Bundle JsonTappa = new Bundle();
                    JsonTappa.putString("JsonTappaString",arrayTappe.toString());
                    ConcertListFragment concertListFragment = new ConcertListFragment();
                    concertListFragment.setArguments(JsonTappa);
                    SaveSharedPreference.setContatore(getApplicationContext(),SaveSharedPreference.getContatore(getApplicationContext())+1);
                    setContentView(R.layout.search_container);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().add(R.id.search_container, concertListFragment,"concertListFragment")
                            .commit();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("Errore:",e.getMessage());
            }

            super.onPostExecute(s);
        }



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Dizionario dizionario = new Dizionario();
        HashMap x = dizionario.dizionarioProvicia();


        ArrayAdapter<String> provincia = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dizionario.getNomi()
        );
        AutoCompleteTextView luogoTxtView = (AutoCompleteTextView) findViewById(R.id.txtViewLuogo);
        luogoTxtView.setAdapter(provincia);
        TextView data = (TextView) findViewById(R.id.dateDisplay);

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBtnDatePressed=true;
                showDialog(0);
            }
        });



        AutoCompleteTextView autocomplete = (AutoCompleteTextView) findViewById(R.id.txtViewArtista);
        if (getIntent().getExtras()!=null){
            Bundle extras;
            extras = getIntent().getExtras();
            String [] nomiArtisti=extras.getStringArray("nomiArtisti");
            SaveSharedPreference.setArtisti(getApplicationContext(), extras.getStringArray("nomiArtisti"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nomiArtisti
        );
        autocomplete.setAdapter(adapter);
        } else {
            String [] nomiArtisti = SaveSharedPreference.getArtisti(getApplicationContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    nomiArtisti
            );
            autocomplete.setAdapter(adapter);
        }

        isBtnDatePressed = false;

        String utente=SaveSharedPreference.getUserName(getApplicationContext());
        String mail=SaveSharedPreference.getEmail(getApplicationContext());
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append(mYear).append("-")
                        .append(mMonth + 1).append("-")
                        .append(mDay).append("")
                        );
    }
    protected DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this,
                mDateSetListener,
                mYear, mMonth, mDay);
    }


    /**
     *Il metodo e' interrogato alla pressione del button Ricerca
     */
    public void search (View v){
        String req="";
        AutoCompleteTextView artistaAutoComplete = (AutoCompleteTextView) findViewById(R.id.txtViewArtista);
        assert artistaAutoComplete != null;
        String artista = artistaAutoComplete.getText().toString();
        AutoCompleteTextView luogoAutoComplete = (AutoCompleteTextView) findViewById(R.id.txtViewLuogo);
        String luogo = luogoAutoComplete.getText().toString();
        TextView data =(TextView) findViewById(R.id.dateDisplay);

//        artista = artista.replace(" ", "");

        req = "http://exrezzo.pythonanywhere.com/api/tappa/?format=json";
        if (isBtnDatePressed){
            //devo includere la data nella ricerca
            req += "&data=" + data.getText().toString();
            if (!luogo.equals("")){
                req+= "&citta=" + luogo;
            }
            if (!artista.equals("")){
                req+= "&tour__artista__nome=" + artista;
            }
        } else {
            if (!luogo.equals("")){
                req+= "&citta=" + luogo;
            }
            if (!artista.equals("")){
                req+= "&tour__artista__nome=" + artista;
            }
        }
        //posso eseguire il task di ricerca
        if (isOnline()){
        JsonTask searchTask = new JsonTask();
        searchTask.execute(req);}
        else {checkConnections();}

    }


    @Override
    public void onResume() {
        checkConnections();
        super.onResume();

        findViewById(android.R.id.content).setFocusableInTouchMode(true);
        findViewById(android.R.id.content).requestFocus();
        findViewById(android.R.id.content).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    Fragment fragment = getFragmentManager().findFragmentByTag("concertListFragment");
                    //vuol dire che ho premuto il pulsante back
                    if (SaveSharedPreference.getContatore(getApplicationContext()) <= 0){
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);

                    } else {

                        SaveSharedPreference.setContatore(getApplicationContext(),SaveSharedPreference.getContatore(getApplicationContext())-1);
                    }
                    // handle back button


                    return true;

                }

                return false;
            }
        });
    }


    @Nullable
    @Override
    public android.support.v7.app.ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void checkConnections (){
        if (isOnline()) {
            //devo mostrare alert e uscire dall'app
            Log.d("Uscita","internet ok");
        } else {
            Log.d("Uscita","no internet");
            showExitAlert();
        }

    }
    public void showExitAlert(){
        android.support.v7.app.AlertDialog alertbox = new android.support.v7.app.AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Ooops! Non sei connesso a Internet!")
                .setPositiveButton("Esci", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);
//                        finish();
                        //close();


                    }
                })
                .setNegativeButton("Ricarica", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        checkConnections();
                    }
                })
                .show();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

