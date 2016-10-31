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


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {
    LayoutInflater upperInflater;
    ViewGroup upperContainer;
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
        upperContainer=container;

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
            final String nomeTappe []  = new String [listaTappe.length()];
            for (int i=0; i <= listaTappe.length()-1;i++){
                JSONObject temp = listaTappe.getJSONObject(i);

                nomeTappe[i] = temp.getJSONObject("tour").getString("nomeTour") +
                        " a " + temp.getString("citta")
                        + " il " +
                temp.getString("data");
                //creazione arrayadapter per trasformare i dati dell'array sottoforma di lista
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nomeTappe);
                //la listview riceve i dati sotto forma di lista
                listview.setAdapter(adapter);
                /**
                 * Settaggio listener per click.
                 * Nel momento in cui una tappa viene selezionata viene aperta la lista delle canzoni
                 * presenti in quella tappa.
                 */
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String tappaSelezionata = nomeTappe[position];
                        StringBuilder sb = new StringBuilder(tappaSelezionata);
                        sb.deleteCharAt(0);
                        sb.deleteCharAt(sb.length()-1);
                        tappaSelezionata = sb.toString();
                        //posso cercare le canzoni della tappa selezionata


                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        //la view con tutti i dati
        return rootView;
    }


}
