package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Michele on 09/11/2016.
 */
public class CheckConnection extends AppCompatActivity {


    public void checkConnections (){
        if (!isOnline()) {
            //devo mostrare alert e uscire dall'app

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
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
                    }
                })
                .show();

    }


}
