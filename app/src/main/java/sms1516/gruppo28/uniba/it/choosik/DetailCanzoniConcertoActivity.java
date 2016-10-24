package sms1516.gruppo28.uniba.it.choosik;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcertoActivity extends Fragment {
    Bundle bundle;

    public class DetailClass extends QueryTask {
        public DetailClass() {

        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> listaCanzoniTappa = risultato;
            final String[] arrayCanzoniTappa = new String[listaCanzoniTappa.size()];
            for (int i=0; i < listaCanzoniTappa.size(); i++){
                arrayCanzoniTappa[i] = listaCanzoniTappa.get(i);
            }


            super.onPostExecute(result);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        bundle = this.getArguments();
        String tappaSelezionata = bundle.getString("concerto");
        tappaSelezionata = tappaSelezionata.replace(" ","");
        DetailClass detailClass = new DetailClass();
        String q = "SELECT Titolo FROM Canzone WHERE Id IN (SELECT IdCanzone FROM Tappa_Canzone WHERE IdTappa = (SELECT Id FROM Tappa WHERE NomeEvento = '"+ tappaSelezionata+"'));";
        detailClass.execute(q);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_canzoni_view);
        return rootView;

    }
}
