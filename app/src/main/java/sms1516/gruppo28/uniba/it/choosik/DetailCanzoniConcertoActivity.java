package sms1516.gruppo28.uniba.it.choosik;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcertoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String[] array = getIntent().getStringArrayExtra("arrayNomeCanzoniConcerto");
//
//        ArrayAdapter<String> adapterCanzoni =
//                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
//        View dettaglioView = inflater.inflate(R.layout.fragment_detail,container,false);
//        ListView listViewCanzoni = (ListView) findViewById(R.id.lista_canzoni_view);
//        listViewCanzoni.setAdapter(adapterCanzoni);


        setContentView(R.layout.fragment_detail);

    }
}
