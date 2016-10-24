package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {
    LayoutInflater upperInflater;
    ViewGroup upperContainer;
    String [] upperConcertiDaQuery;

    String[] arrayNomiConcerti = {"aiuto", "aaaaaaaaahhh"};
//    public class ConcertQueryTask extends QueryTask{
//        public ConcertQueryTask(){
//
//        }
//        /**
//         * Qui posso effettuare la richiesta dei concerti al database
//         */
//        @Override
//        protected void onPostExecute(String result){
//
//            ArrayList<String> temp=getRisultato();
//            String concertiDaQuery [] = new String[temp.size()-1];
//            for (int i=0; i < temp.size()-1; i++){
//                concertiDaQuery[i]=temp.get(i);
//            }
//            upperConcertiDaQuery = concertiDaQuery;
//            View littleRootView = upperInflater.inflate(R.layout.fragment_concert,upperContainer,false);
//            ListView listView = (ListView) littleRootView.findViewById(R.id.lista_concerti_view);
//            ArrayAdapter <String> adapterConcerti = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,concertiDaQuery);
//            listView.setAdapter(adapterConcerti);
//
//            super.onPostExecute(result);
//
//        }
//
//
//
//
//
//
//    }

    public class DetailClass extends QueryTask {
        public DetailClass() {

        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> listaCanzoniTappa = getRisultato();
            final String[] arrayCanzoniTappa = new String[listaCanzoniTappa.size()];
            for (int i=0; i < listaCanzoniTappa.size(); i++){
                arrayCanzoniTappa[i] = listaCanzoniTappa.get(i);
            }

            Bundle bundle = new Bundle();

            DetailCanzoniConcertoActivity nextFragment = new DetailCanzoniConcertoActivity();
            bundle.putStringArray("canzoniTappa",arrayCanzoniTappa);
            nextFragment.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.relativelayoutforfragment, nextFragment);
            ft.commit();




            super.onPostExecute(result);
        }
    }



    public ConcertListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        upperInflater = inflater;
        upperContainer=container;

        /**
         * effettuo richiesta al database per ottenere dati
         */


        Bundle bundle = this.getArguments();

        String [] myConcerti = null;
        final String[] myConcertiToPass;
        if (bundle != null) {
            ArrayList <String> concerti = bundle.getStringArrayList("res");
            myConcerti = new String [concerti.size()];
          for (int i=0; i < concerti.size(); i++){
              myConcerti[i] = concerti.get(i);
          }
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myConcerti);
            listview.setAdapter(adapter);
            myConcertiToPass = myConcerti;
        } else {
            myConcertiToPass = null;
        }



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tappaSelezionata = myConcertiToPass[position];
                StringBuilder sb = new StringBuilder(tappaSelezionata);
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length()-1);
                tappaSelezionata = sb.toString();

                DetailClass detailClass = new DetailClass();
                String q = "SELECT Titolo FROM Canzone WHERE Id IN (SELECT IdCanzone FROM Tappa_Canzone WHERE IdTappa = (SELECT Id FROM Tappa WHERE NomeEvento = '"+ tappaSelezionata+"'));";
                detailClass.execute(q);






            }
        });



        return rootView;


    }


}
