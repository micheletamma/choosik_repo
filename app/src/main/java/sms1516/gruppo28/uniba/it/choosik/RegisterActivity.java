package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText usernameField, passwordField, emailField, provField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Dizionario dizionario = new Dizionario();
        HashMap x = dizionario.dizionarioProvicia();

        ArrayAdapter<String> provincia = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dizionario.getNomi()
        );
        AutoCompleteTextView luogoTxtView = (AutoCompleteTextView) findViewById(R.id.txtProvincia);
        luogoTxtView.setAdapter(provincia);
        TextView avvisoField = (TextView) findViewById(R.id.txtAvviso);
        avvisoField.setText("");
        Button button = (Button) findViewById(R.id.btnRegister);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                usernameField = (EditText) findViewById(R.id.txtUsername);
                passwordField = (EditText) findViewById(R.id.txtPassword);
                emailField = (EditText) findViewById(R.id.txtEmail);
                provField = (EditText) findViewById(R.id.txtProvincia);
                TextView avvisoField = (TextView) findViewById(R.id.txtAvviso);
                avvisoField.setText("");
                String usr = usernameField.getText().toString();
                user = usr;
                String psw = passwordField.getText().toString();
                password = psw;
                String mail = emailField.getText().toString();
                email = mail;
                String prov = provField.getText().toString();

                Map registrazione = new HashMap();
                if (usr.contains(" ")){
                    usr=usr.replace(" ","");
                }
                registrazione.put("username", usr);
                registrazione.put("password", psw);
                registrazione.put("email", mail);
                registrazione.put("provincia", prov);

                params = registrazione;
                if (mail.contains("@") & mail.contains(".")) {
                    try {
                        SimpleTask task = new SimpleTask();
                        task.execute("http://exrezzo.pythonanywhere.com/api/utente/");


                    } catch (Exception e) {
                        e.printStackTrace();
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

        public JSONObject getRisultato() {
            return risultato;
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


}
