package sms1516.gruppo28.uniba.it.choosik;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static boolean artista = false;
    //flag che servono per doInBackground e onPost execute
    boolean searchActivityFlag = false;
    boolean myConcertsFragmentFlag = false;
    String u = "";
    JSONObject artistResult;
    SaveSharedPreference preferenza = new SaveSharedPreference();

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
        artista = SaveSharedPreference.getIsArtist(this);
        u = utente; //estrapolo il nome utente al di fuori del metodo interno
        String postaelettronica = receive.getStringExtra("Email");
        View header = navigationView.getHeaderView(0);
        TextView nome = (TextView) header.findViewById(R.id.nome_utente);
        nome.setText(utente);
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(postaelettronica);
        if (!artista) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_insert).setVisible(false);
        }
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
            setTitle("Home");
            FragmentManager manager = getSupportFragmentManager();

            /**
             * Quando si seleziona home nella navbar, essendo l'unica activity di fatto, controlla
             * la pila dei fragment aperti; questa pila avrà sempre 1 solo fragment aperto, poiche'
             * ogni fragment che si attiva rimpiazza il precedente. Se non vi sono fragment aperti,
             * non fa nulla, perche' vorra' dire che si e' gia' nella home.
             */
            List frags = manager.getFragments();
            if (frags != null) {
                Fragment ultimofrag = (Fragment) frags.get(frags.size() - 1);
                if (ultimofrag != null) {
                    manager.beginTransaction().remove(ultimofrag).commit();
                }
            }

//            Intent i=new Intent(MainActivity.this,MainActivity.class);
//            startActivity(i);


        } else if (id == R.id.nav_search) {
            setTitle("Ricerca");
            searchActivityFlag = true;
            JsonTask artistTask = new JsonTask();
            artistTask.execute("http://exrezzo.pythonanywhere.com/api/utente/?format=json&artista=true");


//
        } else if (id == R.id.nav_concert) {
            setTitle("I miei concerti");
            myConcertsFragmentFlag = true;
            MyConcertsFragment myConcertsFragment = new MyConcertsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment, myConcertsFragment, myConcertsFragment.getTag()).commit();


        } else if (id == R.id.nav_about) {
            setTitle("About us");
            AboutFragment aboutFragment = new AboutFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment, aboutFragment, aboutFragment.getTag()).commit();


        } else if (id == R.id.nav_send) {
            setTitle("Contattaci");
            SendFragment sendFragment = new SendFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment, sendFragment, sendFragment.getTag()).commit();


        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Sei uscito!", Toast.LENGTH_SHORT).show();
            SaveSharedPreference.clearUserName(this);
            Intent i2login = new Intent(MainActivity.this, LoginActivity.class);
            // dopo aver fatto il logout, vengono rimosse tutte le activity nello stack
            i2login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i2login);
        } else if (id == R.id.nav_insert) {
            //inserimento delle tappe
            setTitle("Inserisci nuova tappa");
            InsertFragment insertFragment = new InsertFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayoutforfragment, insertFragment, insertFragment.getTag()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class JsonTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            // se si va in ricerca dal menu si fa questo tipo di richiesta
            if (searchActivityFlag == true) {
                try {
                    URL url = new URL(strings[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }

                    artistResult = new JSONObject(buffer.toString());
                    return buffer.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //se si va in i miei concerti si fa questo tipo di richiesta
            } else if (myConcertsFragmentFlag == true) {

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //se dobbiamo andare nella ricerca facciamo questo

            if (searchActivityFlag == true) {


                try {
                    JSONArray arrayArtisti = artistResult.getJSONArray("objects");
                    String nomiArtisti[] = new String[arrayArtisti.length()];
                    for (int i = 0; i <= arrayArtisti.length() - 1; i++) {
                        JSONObject temp = arrayArtisti.getJSONObject(i);
                        nomiArtisti[i] = temp.getString("nome");

                    }
                    Intent anIntent = new Intent(getApplicationContext(), SearchActivity.class);
                    anIntent.putExtra("nomiArtisti", nomiArtisti);
                    startActivity(anIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //se andiamo in i miei concerti si fa questo
            } else if (myConcertsFragmentFlag == true) {


            }


            super.onPostExecute(s);
        }


    }


}