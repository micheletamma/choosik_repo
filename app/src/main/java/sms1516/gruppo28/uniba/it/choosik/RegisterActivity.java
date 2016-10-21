package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    String user,email,password;
    public class RegisterQueryTask extends QueryTask{
        public RegisterQueryTask(){

        }

        @Override
        protected void onPostExecute(String result){
            ArrayList<String> temp=getRisultato();
            //Nella prima posizione dell'array si trova l'username dell'utente
            //Controllo che l'username non sia gia' nel database

            if (temp.get(0).replace(" ","").equals(user)){
                //l'utente deve inserire un altro nome utente
                Toast.makeText(RegisterActivity.this,"Utente gia' esistente",Toast.LENGTH_SHORT).show();
            } else{
                //posso inserire i dati nel database
                InsertTask insert = new InsertTask((RegisterActivity.this));
                //creo la query di inserimento
                user = user.replace(" ","");
                password = password.replace(" ","");
                email = email.replace(" ","");
                String q="INSERT INTO Utente(Username, Password, Email) VALUES ('" + user + "', '" + password + "', '" + email + "');";
                q = q.replace(" ", "%20");
                q=q.replace("'","%27");
                insert.execute(q);

            }
            super.onPostExecute(result);

        }

    }

    private EditText usernameField,passwordField, emailField;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button button = (Button) findViewById(R.id.btnRegister);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                usernameField = (EditText)findViewById(R.id.txtUsername);
                passwordField = (EditText)findViewById(R.id.txtPassword);
                emailField = (EditText)findViewById(R.id.txtEmail);
                String usr = usernameField.getText().toString();
                user = usr;
                String psw= passwordField.getText().toString();
                password=psw;
                String mail = emailField.getText().toString();
                email=mail;
                RegisterQueryTask checkUsr = new RegisterQueryTask();
                checkUsr.execute("Select%20Username%20FROM%20Utente%20WHERE%20Username%20=%20%27"+usr+"%27;");

                ArrayList<String> amen = checkUsr.getRisultato();
//                String x = checkUsr.ciao;
//                AsyncTask.Status status = checkUsr.getStatus();
//                String test = status.name();
//                while (checkUsr.getStatus() != AsyncTask.Status.FINISHED);



//                ArrayList<String> res = checkUsr.risultato;
//
//                if (res.get(0).equals(usr)){
//                    //messaggio d'errore
//
//                }
//                else {
//                    //registrazione ok
//                }
            }
        });

    }


    public void task(){

    }

}
