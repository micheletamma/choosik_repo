package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



    public ConcertListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        QueryTask concerti = new QueryTask(this.getContext());
//        concerti.execute("SELECT%20NomeEvento%20FROM%20Tappa;");
//        ArrayList<String> arrayListNomiConcerti=concerti.risultato;
//        String[] arrayNomiConcerti = new String[arrayListNomiConcerti.size()];
//
//        for (int i = 0; i<=arrayListNomiConcerti.size()-1;i++) {
//            arrayNomiConcerti[i] = arrayListNomiConcerti.get(i);
//            Log.d("Concerto in posizione"+i, arrayNomiConcerti[i]);
//        }
//
//        ArrayAdapter<String> adapter= new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, arrayNomiConcerti);
//        setListAdapter(adapter);

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        upperInflater = inflater;
        upperContainer=container;

        /**
         * effettuo richiesta al database per ottenere dati
         */


        Bundle bundle = this.getArguments();

        ArrayList <String> concerti = bundle.getStringArrayList("res");
        String [] myConcerti = new String [concerti.size()];
        if (bundle != null) {
          for (int i=0; i < concerti.size(); i++){
              myConcerti[i] = concerti.get(i);
          }
        }


        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myConcerti);
        listview.setAdapter(adapter);
        return rootView;


    }


}
