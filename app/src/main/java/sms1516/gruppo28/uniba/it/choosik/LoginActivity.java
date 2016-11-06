package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Questo controllo serve a stabilire se un utente e' gia' loggato nella app.
         * Controlla nelle preferenze della app se l'username e' salvato tra le preferenze, se si
         * avvia direttamente la main activity, evitando di avviare la login activity.
         */
        if (SaveSharedPreference.getUserName(LoginActivity.this).length() == 0) {
            // prosegui nella crazione della login activity
        } else {
            // avvia direttamente la main activity, prelevando i dati username e email da inserire
            // nella navbar dalle preferenze salvate.
            Intent i = new Intent(LoginActivity.this, MainActivity.class)
                    .putExtra("Username", SaveSharedPreference.getUserName(this)).
                            putExtra("Email", SaveSharedPreference.getEmail(this));
            startActivity(i);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameField = (EditText) findViewById(R.id.txtUser);
        passwordField = (EditText) findViewById(R.id.txtPassword);
        final Button loginbtn = (Button) findViewById(R.id.loginbtn);
    }

    /**
     * @param view Il metodo è invocato dal button di "activity_login"
     */
    public void login(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        new JsonLoginTask().execute("http://exrezzo.pythonanywhere.com/api/utente/?username=" +
                username +
                "&password=" +
                password);

    }

    public void register(View view) {
        Context context = this;
        context.startActivity(new Intent(context, RegisterActivity.class));
    }


    private class JsonLoginTask extends AsyncTask<String, Void, String> {
        JSONObject loginresult;
        String username;
        String email;
        String prov;
        boolean artista;


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
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
                // salva in loginresult gli oggetti in formato json ricevuti dalla url
                loginresult = new JSONObject(s);
                Log.d("login result", loginresult.getJSONArray("objects").get(0).toString());

                // prende come array di oggetti json tutti i dizionari della risposta, se
                // ce n'e' 1, vuol dire che la combinazione username psw e' corretta
                if (loginresult.getJSONArray("objects").length() == 1) {
                    JSONObject datiutente = loginresult.getJSONArray("objects").getJSONObject(0);
                    // preleva username e psw dell'utente loggato correttamente
                    username = datiutente.getString("username");
                    artista = datiutente.getBoolean("artista");
                    email = datiutente.getString("email");
                    prov=datiutente.getString("provincia");
                    // salva i dati nelle preferenze essendo corretti per evitare in seguito di
                    // rifare la login
                    SaveSharedPreference.setUserName(getApplicationContext(), username);
                    SaveSharedPreference.setIsArtist(getApplicationContext(), artista);
                    SaveSharedPreference.setEmail(getApplicationContext(), email);
                    SaveSharedPreference.setProvincia(getApplicationContext(),prov);
                    // avvia la mainActivity
                    getApplicationContext()
                            .startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .putExtra("Username", username)
                                    .putExtra("artista", artista)
                                    .putExtra("Email", email));
                }
            } catch (Exception e) {
                Log.e("errore", e.getMessage());
                Toast.makeText(getApplicationContext(), "Autenticazione fallita", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Vuoi davvero uscire dall'applicazione?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

}