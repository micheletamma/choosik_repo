package sms1516.gruppo28.uniba.it.choosik;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

public class SplashActivity extends ActionBarActivity {
    private static int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Metodo per nascondere la action bar dalla splash screen, per fare cio ho dovuto fare
        // ereditare questa classe da ActionBarActivity, che e' una classe deprecata.. non so se
        // in futuro potrebbe dare errore. (prima ereditava da AppCompatActivity)
        getSupportActionBar().hide();

        // Mostra la schermata splash per un tot di secondi.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Questo metodo è eseguito allo scadere del timeout, allo scadere del timer.
                // Avvia la main activity
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

                // Chiude questa activity.
                finish();

            }
        }, SPLASH_TIMEOUT);
    }
}