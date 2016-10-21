package sms1516.gruppo28.uniba.it.choosik;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String u = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        Intent receive = getIntent();
        String utente = receive.getStringExtra("Username");
        u=utente; //estrapolo il nome utente al di fuori del metodo interno
        String postaelettronica = receive.getStringExtra("Email");
        View header=navigationView.getHeaderView(0);
        TextView nome = (TextView) header.findViewById(R.id.nome_utente);
        nome.setText(utente);
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(postaelettronica);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
            FragmentManager manager= getSupportFragmentManager();

            /**
             * Quando si seleziona home nella navbar, essendo l'unica activity di fatto, controlla
             * la pila dei fragment aperti; questa pila avrà sempre 1 solo fragment aperto, poiche'
             * ogni fragment che si attiva rimpiazza il precedente. Se non vi sono fragment aperti,
             * non fa nulla, perche' vorra' dire che si e' gia' nella home.
              */
            List frags = manager.getFragments();
            if (frags != null) {
                Fragment ultimofrag = (Fragment) frags.get(frags.size()-1);
                manager.beginTransaction().remove(ultimofrag).commit();
            }

//            Intent i=new Intent(MainActivity.this,MainActivity.class);
//            startActivity(i);


        } else if (id == R.id.nav_search) {
            Toast.makeText(this,"Ricerca Artisti",Toast.LENGTH_SHORT).show();
            SearchFragment searchFragment= new SearchFragment();
            FragmentManager manager= getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment,searchFragment,searchFragment.getTag()).commit();

        } else if (id == R.id.nav_concert) {
            Toast.makeText(this,"I miei concerti",Toast.LENGTH_SHORT).show();
            ConcertListFragment concertListFragment= new ConcertListFragment();
            FragmentManager manager= getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment,concertListFragment,concertListFragment.getTag()).commit();
            Bundle bundle = new Bundle();
            bundle.putString("1", u);
            concertListFragment.setArguments(bundle);
        } else if (id == R.id.nav_about) {
            Toast.makeText(this,"About us",Toast.LENGTH_SHORT).show();
            AboutFragment aboutFragment= new AboutFragment();
            FragmentManager manager= getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment,aboutFragment,aboutFragment.getTag()).commit();


        } else if (id == R.id.nav_send) {
            Toast.makeText(this,"Contattaci",Toast.LENGTH_SHORT).show();
            SendFragment sendFragment= new SendFragment();
            FragmentManager manager= getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment,sendFragment,sendFragment.getTag()).commit();


        } else if (id == R.id.nav_logout) {
            Toast.makeText(this,"Sei uscito!",Toast.LENGTH_SHORT).show();
            SaveSharedPreference.clearUserName(this);
            Intent i2login = new Intent(MainActivity.this, LoginActivity.class);
            // dopo aver fatto il logout, vengono rimosse tutte le activity nello stack
            i2login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i2login);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
