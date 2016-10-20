package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {

    String[] arrayNomiConcerti = {"aiuto", "aaaaaaaaahhh"};




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

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayNomiConcerti);
        listview.setAdapter(adapter);




        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);



    }



}
