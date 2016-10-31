package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {
    LayoutInflater upperInflater;
    ViewGroup upperContainer;
    String[] upperConcertiDaQuery;

    public ConcertListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //creazione di una view collegata a fragment_concert e di una listview collegata a
        //lista_concerti_view

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        upperInflater = inflater;
        upperContainer = container;

        /**
         * effettuo richiesta al database per ottenere dati
         */

        //bundle passato da MainActivity per avere i dati dal database
        Bundle bundle = this.getArguments();
        String receive = bundle.getString("JsonTappaString");
        JSONArray listaTappe;
        try {
            listaTappe = new JSONArray(receive);
            //listaTappe e' un array di oggetti Json
            String nomeTappe[] = new String[listaTappe.length()];
            for (int i = 0; i <= listaTappe.length() - 1; i++) {
                JSONObject temp = listaTappe.getJSONObject(i);
                nomeTappe[i] = temp.getString("nome") +
                        " a " + temp.getString("citta")
                        + " il " +
                        temp.getString("data");
                //creazione arrayadapter per trasformare i dati dell'array sottoforma di lista
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomeTappe);
                //la listview riceve i dati sotto forma di lista
                listview.setAdapter(adapter);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        String[] myConcerti = null;
        final String[] myConcertiToPass;
        if (bundle != null) {
            ArrayList<String> concerti = bundle.getStringArrayList("res");
            myConcerti = new String[concerti.size()];
            for (int i = 0; i < concerti.size(); i++) {
                myConcerti[i] = concerti.get(i);
            }
            //creazione arrayadapter per trasformare i dati dell'array sottoforma di lista
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myConcerti);
            //la listview riceve i dati sotto forma di lista
            listview.setAdapter(adapter);
            myConcertiToPass = myConcerti;
        } else {
            myConcertiToPass = null;
        }


        /**
         * Settaggio listener per click.
         * Nel momento in cui una tappa viene selezionata viene aperta la lista delle canzoni
         * presenti in quella tappa.
         */
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tappaSelezionata = myConcertiToPass[position];
                StringBuilder sb = new StringBuilder(tappaSelezionata);
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length() - 1);
                tappaSelezionata = sb.toString();

                DetailClass detailClass = new DetailClass();
                String q = "SELECT Titolo FROM Canzone WHERE Id IN (SELECT IdCanzone FROM Tappa_Canzone WHERE IdTappa = (SELECT Id FROM Tappa WHERE NomeEvento = '" + tappaSelezionata + "'));";
                detailClass.execute(q);


            }
        });


        //la view con tutti i dati
        return rootView;


    }

    public class ConcertQueryTask extends QueryTask {
        public ConcertQueryTask() {

        }

        /**
         * Qui posso effettuare la richiesta dei concerti al database
         */
        @Override
        protected void onPostExecute(String result) {

//           ArrayList<String> temp=getRisultato();
//            String concertiDaQuery [] = new String[temp.size()-1];
//            for (int i=0; i < temp.size()-1; i++){
//                concertiDaQuery[i]=temp.get(i);
//            }
//            upperConcertiDaQuery = concertiDaQuery;
            View littleRootView = upperInflater.inflate(R.layout.fragment_concert, upperContainer, false);
            ListView listView = (ListView) littleRootView.findViewById(R.id.lista_concerti_view);
//            ArrayAdapter <String> adapterConcerti = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,concertiDaQuery);
//            listView.setAdapter(adapterConcerti);

            super.onPostExecute(result);

        }


    }

    public class DetailClass extends QueryTask {
        Bundle bundle = new Bundle();

        public DetailClass() {

        }

        @Override
        protected void onPostExecute(String result) {
//            ArrayList<String> listaCanzoniTappa = getRisultato();
//            final String[] arrayCanzoniTappa = new String[listaCanzoniTappa.size()];
//            for (int i=0; i < listaCanzoniTappa.size(); i++){
//                arrayCanzoniTappa[i] = listaCanzoniTappa.get(i);
        }

//            DetailCanzoniConcertoActivity nextFragment = new DetailCanzoniConcertoActivity();
//            bundle.putStringArray("canzoniTappa",arrayCanzoniTappa);
//            nextFragment.setArguments(bundle);
//
//            FragmentManager fm = getFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.relativelayoutforfragment, nextFragment);
//            ft.commit();
//
//
//
//
//            super.onPostExecute(result);
//        }
    }


}
