package sms1516.gruppo28.uniba.it.choosik;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
    //flag che servono per doInBackground e onPost execute
    boolean searchActivityFlag = false;
    boolean myConcertsFragmentFlag = false;
    boolean mainFragmentFlag = false;
    String provincia;
    String u = "";
    JSONObject artistResult;
    static boolean artista = false;
    SaveSharedPreference preferenza = new SaveSharedPreference();
    static String fragmentAttivo;


    private class JsonTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            if (isOnline()){
                super.onPreExecute();}
            else {
                checkConnections();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;


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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //se dobbiamo andare nella ricerca facciamo questo

            if (searchActivityFlag == true) {

                searchActivityFlag = false;


                try {
                    JSONArray arrayArtisti = artistResult.getJSONArray("objects");
                    String nomiArtisti[] = new String[arrayArtisti.length()];
                    for (int i = 0; i <= arrayArtisti.length() - 1; i++) {
                        JSONObject temp = arrayArtisti.getJSONObject(i);
                        nomiArtisti[i] = temp.getString("nome");

                    }
                    Intent anIntent = new Intent(getApplicationContext(), SearchActivity.class);
                    anIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    anIntent.putExtra("nomiArtisti", nomiArtisti);
                    startActivity(anIntent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //se andiamo in i miei concerti si fa questo
            } else if (myConcertsFragmentFlag == true) {
                myConcertsFragmentFlag=false;
                try {
                    JSONArray arrayMieiConcerti = artistResult.getJSONArray("objects");
                    Bundle jsonArrayTappe = new Bundle();
                    jsonArrayTappe.putString("jsonArrayTappe",arrayMieiConcerti.toString());
                    MyConcertsFragment myConcertsFragment = new MyConcertsFragment();
                    FragmentManager manager = getSupportFragmentManager();
                    myConcertsFragment.setArguments(jsonArrayTappe);
                    manager.beginTransaction().replace(R.id.relativelayoutforfragment, myConcertsFragment)
                            .addToBackStack(myConcertsFragment.getTag())
                            .commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (mainFragmentFlag){
                mainFragmentFlag=false;
                try {
                    JSONArray arrayConcertiVicini = artistResult.getJSONArray("objects");
                    String nomeConcerto[] = new String[arrayConcertiVicini.length()];
                    int[] arrayIdTappe = new int[arrayConcertiVicini.length()];
                    for (int i = 0; i <= arrayConcertiVicini.length() - 1; i++) {
                        JSONObject temp = arrayConcertiVicini.getJSONObject(i);
                        nomeConcerto[i] = temp.getJSONObject("tour").getString("nomeTour")
                                + " a " + temp.getString("citta")
                                + " il " + temp.getString("data");
                        arrayIdTappe[i] = Integer.parseInt(temp.getString("id"));

                    }
                    Bundle nomi = new Bundle();
                    nomi.putStringArray("nomeConcerto",nomeConcerto);
                    nomi.putIntArray("arrayIdTappe", arrayIdTappe);
                    FragmentManager manager = getSupportFragmentManager();
                    MainFragment mainFragment = new MainFragment();
                    mainFragment.setArguments(nomi);
                    manager.beginTransaction().replace(R.id.relativelayoutforfragment,mainFragment,"mainFragment")
                            .commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e("Errore:",e.getMessage());
                }
            }

            super.onPostExecute(s);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentAttivo="mainFragment";
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

        String utente = SaveSharedPreference.getUserName(this);
        artista = SaveSharedPreference.getIsArtist(this);
        u = utente; //estrapolo il nome utente al di fuori del metodo interno
        String postaelettronica = SaveSharedPreference.getEmail(this);
        provincia=SaveSharedPreference.getProvincia(this);
        View header = navigationView.getHeaderView(0);
        TextView nome = (TextView) header.findViewById(R.id.nome_utente);
        nome.setText(utente);
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(postaelettronica);
        /**
         * Se l'utente loggato e' un artista rendo visibile l'interfaccia utente per artisti
         */
        if (!artista) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_insert).setVisible(false);
            nav_Menu.findItem(R.id.nav_canzoni).setVisible(false);
        }

        mainFragmentFlag=true;
        JsonTask mainTask = new JsonTask();
        mainTask.execute("http://exrezzo.pythonanywhere.com/api/tappa/?format=json&citta="+provincia);

    }


    @Override
    protected void onResume() {
        updateNavigHome();
        checkConnections();
        super.onResume();


    }

    @Override
    protected void onRestart() {
        updateNavigHome();
        super.onRestart();

    }


    public void updateNavigHome(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView nome = (TextView) header.findViewById(R.id.nome_utente);
        nome.setText(SaveSharedPreference.getUserName(this));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fm = getSupportFragmentManager();
        MainFragment mainFragment = (MainFragment) fm.findFragmentByTag("mainFragment");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            if (mainFragment.isVisible()){
                showAlert();}

        } else {
            if (mainFragment.isVisible()){
                showAlert();
            } else {
            super.onBackPressed();}
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        String t= (String) getSupportActionBar().getTitle();
        if (id == R.id.nav_home) {
            fragmentAttivo="mainFragment";

            mainFragmentFlag = true;

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
            JsonTask mainTask = new JsonTask();
            mainTask.execute("http://exrezzo.pythonanywhere.com/api/tappa/?format=json&citta="+provincia);

        } else if (id == R.id.nav_search) {

            searchActivityFlag = true;
            JsonTask artistTask = new JsonTask();
            artistTask.execute("http://exrezzo.pythonanywhere.com/api/utente/?format=json&artista=true");


//
        } else if (id == R.id.nav_concert) {

            myConcertsFragmentFlag = true;

            if (!t.equals("I miei concerti")){
                JsonTask concertiVotatiTask = new JsonTask();
                //inserire url per prendere i concerti con le canzoni che l'utente ha votato
                concertiVotatiTask.execute("http://exrezzo.pythonanywhere.com/api/mieiconcerti/?username="+SaveSharedPreference.getUserName(this));

            }
            fragmentAttivo="iMieiConcerti";

        } else if (id == R.id.nav_about) {
            if (!t.equals("About us")){
                AboutFragment aboutFragment = new AboutFragment();
                inserisciFragment(aboutFragment);
                /*manager.beginTransaction().replace(R.id.relativelayoutforfragment, aboutFragment, aboutFragment.getTag())
                        .addToBackStack(aboutFragment.getTag())
                        .commit();*/

            }
            fragmentAttivo="About";


        } else if (id == R.id.nav_send) {
            fragmentAttivo="Contattaci";
            if (isOnline()){
            if (!t.equals("Contattaci")) {
                SendFragment sendFragment = new SendFragment();
                inserisciFragment(sendFragment);
             /*   manager.beginTransaction().replace(R.id.relativelayoutforfragment, sendFragment, sendFragment.getTag())
                        .addToBackStack(sendFragment.getTag())
                        .commit();*/
            }} else {checkConnections();}



        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Sei uscito!", Toast.LENGTH_SHORT).show();
            SaveSharedPreference.clearUserName(this);
            Intent i2login = new Intent(MainActivity.this, LoginActivity.class);
            // dopo aver fatto il logout, vengono rimosse tutte le activity nello stack
            i2login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i2login);
        } else if (id == R.id.nav_insert) {
            fragmentAttivo="inserisciTour";
            //inserimento delle tappe
            if (isOnline()){
            if (!t.equals("Inserisci Tour")){
                checkConnections();
                InsertFragment insertFragment = new InsertFragment();
                inserisciFragment(insertFragment);
               /* manager.beginTransaction().replace(R.id.relativelayoutforfragment, insertFragment, insertFragment.getTag())
                        .addToBackStack(insertFragment.getTag())
                        .commit();*/}
           }
            else {
                checkConnections();
            }
        } else if (id==R.id.nav_canzoni){
            fragmentAttivo="leMieCanzoni";
            if (isOnline()){
            if (!t.equals("Le mie canzoni")){
                MySongsFragment mySongsFragment = new MySongsFragment();
                inserisciFragment(mySongsFragment);
                /*manager.beginTransaction().replace(R.id.relativelayoutforfragment,mySongsFragment,mySongsFragment.getTag())
                        .addToBackStack(mySongsFragment.getTag())
                        .commit();*/}

        } else {
                checkConnections();
            }
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public void showAlert (){
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Vuoi davvero uscire dall'applicazione?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);
//                        finish();
                        //close();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }




    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Nullable
    @Override
    public android.support.v7.app.ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    public void checkConnections (){
        if (isOnline()) {
            //devo mostrare alert e uscire dall'app
            Log.d("Uscita","internet ok");
        } else {
            Log.d("Uscita","no internet");
            showExitAlert();
        }

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

                        if (isOnline()){
                            //cariconuovofragment
                            caricaFragment();
                        }
                        checkConnections();
                    }
                })
                .show();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    public void caricaFragment(){
        FragmentManager manager = getSupportFragmentManager();
        String titolo= (String) getSupportActionBar().getTitle();
        if (fragmentAttivo.equals("iMieiConcerti")){
            myConcertsFragmentFlag = true;
            JsonTask concertiVotatiTask = new JsonTask();
            //inserire url per prendere i concerti con le canzoni che l'utente ha votato
            concertiVotatiTask.execute("http://exrezzo.pythonanywhere.com/api/mieiconcerti/?username="+SaveSharedPreference.getUserName(this));
            fragmentAttivo="iMieiConcerti";
        } else if (fragmentAttivo.equals("About")){
            AboutFragment aboutFragment = new AboutFragment();
            inserisciFragment(aboutFragment);
            /*manager.beginTransaction().replace(R.id.relativelayoutforfragment, aboutFragment, aboutFragment.getTag())
                    .addToBackStack(aboutFragment.getTag())
                    .commit();*/
            fragmentAttivo="About";
        } else if (fragmentAttivo.equals("Contattaci")) {
            SendFragment sendFragment = new SendFragment();
            inserisciFragment(sendFragment);
          /*  manager.beginTransaction().replace(R.id.relativelayoutforfragment, sendFragment, sendFragment.getTag())
                    .addToBackStack(sendFragment.getTag())
                    .commit();*/
            fragmentAttivo="Contattaci";
        } else if (fragmentAttivo.equals("inserisciTour")){
            InsertFragment insertFragment = new InsertFragment();
            inserisciFragment(insertFragment);
            /*manager.beginTransaction().replace(R.id.relativelayoutforfragment, insertFragment, insertFragment.getTag())
                    .addToBackStack(insertFragment.getTag())
                    .commit();*/
            fragmentAttivo="inserisciTour";
        } else if (fragmentAttivo.equals("leMieCanzoni")){
            MySongsFragment mySongsFragment = new MySongsFragment();
            inserisciFragment(mySongsFragment);
            /*manager.beginTransaction().replace(R.id.relativelayoutforfragment,mySongsFragment,mySongsFragment.getTag())
                    .addToBackStack(mySongsFragment.getTag())
                    .commit();*/
            fragmentAttivo="leMieCanzoni";
        } else if (fragmentAttivo.equals("mainFragment")) {
            mainFragmentFlag=true;
            JsonTask mainTask = new JsonTask();
            mainTask.execute("http://exrezzo.pythonanywhere.com/api/tappa/?format=json&citta="+provincia);
        }



    }

    public void inserisciFragment(Fragment frag){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.relativelayoutforfragment,frag,frag.getTag())
                .addToBackStack(frag.getTag())
                .commit();
    }


}