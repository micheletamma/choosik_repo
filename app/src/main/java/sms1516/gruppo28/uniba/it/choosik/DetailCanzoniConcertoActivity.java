package sms1516.gruppo28.uniba.it.choosik;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;

/**
 * Created by marcouva on 20/10/16.
 */

public class DetailCanzoniConcertoActivity extends Fragment {
    Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        bundle = this.getArguments();
        String[] arrayCanzoniPassate = bundle.getStringArray("canzoniTappa");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        final ListView listview = (ListView) rootView.findViewById(R.id.lista_canzoni_view);

        //simple_list_item_1 deve essere cambiato con una nostra lista di item da creare
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayCanzoniPassate);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.rank_dialog);
                dialog.setCancelable(true);
                RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.dialog_ratingbar);
                ratingBar.setRating(3); // qui bisogna settare il rating preso in input dall'utente
                Button button = (Button) dialog.findViewById(R.id.bottone_vota);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //da implementare
                    }
                });

                dialog.show();


            }
        });

        return rootView;


    }
}
