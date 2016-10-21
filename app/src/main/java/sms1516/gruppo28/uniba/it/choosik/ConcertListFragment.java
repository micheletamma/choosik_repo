package sms1516.gruppo28.uniba.it.choosik;


import android.content.Intent;
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

    String[] arrayNomiConcerti = {"Ciolplay", "Vasco Rotti", "Le luci della centrale siderurgica","Sfera epotrebbebastare"};
    String[] arrayNomiCanzoniConcerto = {"La bella lavanderina", "che lava i fazzoletti"};




    public ConcertListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
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


        //Creazione della view da resituire che avrà una listview
        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        //definizione della listview collegata alla listview del fragment_concert
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        //creazione adapter per array che andrà a mettere gli elementi di quest'ultimo nella listview
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayNomiConcerti);

        //setta l'adapter per la nostra listview
        listview.setAdapter(adapter);

        //queste righe di codice servono per fare in modo che gli elementi della listview siano cliccabili
        //in questa maniera creamo successivamente il fragment del dettaglio. (prima non potevo perchè
        //essendo questa classe un fragment e non un listfragment non esiste il metodo onclickitem
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Bundle bundle = new Bundle();
//                bundle.putStringArray("arrayCanzoni",arrayNomiCanzoniConcerto);
//                DetailCanzoniConcertoActivity.setArguments(bundle);




                final ArrayAdapter<String> adapterCanzoni =
                        new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayNomiCanzoniConcerto);


                View dettaglioView = inflater.inflate(R.layout.fragment_detail,container,false);
                ListView listViewCanzoni = (ListView) dettaglioView.findViewById(R.id.lista_canzoni_view);
                listViewCanzoni.setAdapter(adapterCanzoni);

//                Intent intent = new Intent(getActivity(),DetailCanzoniConcertoActivity.class);
//                intent.putExtra("arrayNomeCanzoniConcerto", arrayNomiCanzoniConcerto);
//                startActivity(intent);





            }
        });


        return rootView;


    }








}
