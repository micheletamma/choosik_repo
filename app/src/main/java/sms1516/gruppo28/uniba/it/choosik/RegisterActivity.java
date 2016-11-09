package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    String user, email, password;
    Map params = new HashMap();
    private EditText usernameField, passwordField, emailField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Dizionario dizionario = new Dizionario();
        HashMap x = dizionario.dizionarioProvicia();
        final Spinner sp = (Spinner) findViewById(R.id.spinnerProvince);
        ArrayAdapter<String> provincia = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                R.id.text1,
                dizionario.getNomi()
        );

        sp.setAdapter(provincia);

        TextView avvisoField = (TextView) findViewById(R.id.txtAvviso);
        avvisoField.setText("");
        Button button = (Button) findViewById(R.id.btnRegister);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                usernameField = (EditText) findViewById(R.id.txtUsername);
                passwordField = (EditText) findViewById(R.id.txtPassword);
                emailField = (EditText) findViewById(R.id.txtEmail);

                TextView avvisoField = (TextView) findViewById(R.id.txtAvviso);
                avvisoField.setText("");
                String usr = usernameField.getText().toString();
                user = usr;
                String psw = passwordField.getText().toString();
                password = psw;
                String mail = emailField.getText().toString();
                email = mail;
                String prov = sp.getSelectedItem().toString();

                Map registrazione = new HashMap();
                if (usr.contains(" ")){
                    usr=usr.replace(" ","");
                }
                registrazione.put("username", usr);
                registrazione.put("password", psw);
                registrazione.put("email", mail);
                registrazione.put("provincia", prov);
                checkConnections();
                params = registrazione;
                if (mail.contains("@") & mail.contains(".")) {
                    if (!password.equals("") & !user.equals("")){
                        try {
                            if (isOnline()) {
                                SimpleTask task = new SimpleTask();
                                task.execute("http://exrezzo.pythonanywhere.com/api/utente/");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        avvisoField.setText("Imposta un username e una password validi");
                    }


                } else {
                    avvisoField.setText("La tua mail non è valida");

                }
            }

        });
    }

    public class SimpleTask extends AsyncTask<String, Void, String> {
        public Context context;
        JSONObject risultato;


        public SimpleTask(Context ctx) {

            this.context = ctx;

        }

        public SimpleTask() {
        }




        @Override
        protected String doInBackground(String... strings) {
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();

            //url with the post data
            HttpPost httpost = new HttpPost(strings[0]);

            //convert parameters into JSON object
            JSONObject holder = new JSONObject(params);

            //passes the results to a string builder/entity
            StringEntity se = null;
            try {
                se = new StringEntity(holder.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //sets the post request as the resulting string
            httpost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");
            String message = "ok";
            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            try {
                httpclient.execute(httpost, responseHandler);
            } catch (ClientProtocolException e) {
                Log.e("errore", e.getMessage());
                message = "permesso negato";
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("errore", e.getMessage());


            } catch (NullPointerException e){
                e.printStackTrace();
                Log.e("errore",e.getMessage());
            }

            return message;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("ok")) {
                Toast.makeText(getApplicationContext(), "Registrazione completata, effettua il login!", Toast.LENGTH_SHORT).show();
                getApplicationContext()
                        .startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


            } else {
                Toast.makeText(getApplicationContext(), "Username gia' esistente!", Toast.LENGTH_SHORT).show();

            }

        }
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
        AlertDialog alertbox = new AlertDialog.Builder(this)
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
