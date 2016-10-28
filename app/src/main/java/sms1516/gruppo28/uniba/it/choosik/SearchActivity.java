package sms1516.gruppo28.uniba.it.choosik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

/**
 * La classe Search Activity e' estesa a MainActivity in modo tale da implementare
 * il nav_menu
 */
public class SearchActivity extends AppCompatActivity {
    /**
     * Nel metodo onCreate carichiamo lo stesso menu della Main Activity
     * Si implementa un Date Picker per gestire le date in input
     */

    TextView mDateDisplay;
    Button mPickDate;
    int mYear;
    int mMonth;
    int mDay;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        String utente = SaveSharedPreference.getUserName(getApplicationContext());
        String mail = SaveSharedPreference.getEmail(getApplicationContext());
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        HashMap diz = Dizionario.dizionarioProvicia();
        mPickDate = (Button) findViewById(R.id.pickDate);
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append(mMonth + 1).append("-")
                        .append(mDay).append("-")
                        .append(mYear).append(" "));
    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this,
                mDateSetListener,
                mYear, mMonth, mDay);
    }


}

