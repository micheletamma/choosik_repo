package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField,passwordField, emailField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameField = (EditText)findViewById(R.id.txtUsername);
        passwordField = (EditText)findViewById(R.id.txtPassword);
        emailField = (EditText)findViewById(R.id.txtEmail);
        Button button = (Button) findViewById(R.id.btnRegister);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                QueryTask checkUsr = new QueryTask(RegisterActivity.this.getApplicationContext());

                String usr = usernameField.getText().toString();
                checkUsr.execute("Select%20Username%20FROM%20Utente%20WHERE%20Username%20=%20%27"+usr+"%27;");
                ArrayList<String> res = checkUsr.risultato;
                if (res.get(0).equals(usr)){
                    //messaggio d'errore

                }
                else {
                    //registrazione ok
                }
            }
        });

    }


    public void task(){

    }

}
