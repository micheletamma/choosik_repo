package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField,passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameField = (EditText)findViewById(R.id.txtUser);
        passwordField = (EditText)findViewById(R.id.txtPassword);
        final Button loginbtn = (Button) findViewById(R.id.loginbtn);
//        loginbtn.setOnClickListener(new View.OnClickListener()
//            {
//                public void onClick(View v)
//                {
//                    login(v);
//                    // Perform action on click
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(i);
//                    //controllo credenziali login
//                }
//            }
//             );


    }
    public void login(View view) {
        SigninTask st = new SigninTask(this);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        st.execute(username, password);

    }
}
