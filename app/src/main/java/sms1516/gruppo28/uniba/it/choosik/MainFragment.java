package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Michele on 02/11/2016.
 */
public class MainFragment extends Fragment {

    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        Bundle bundle = this.getArguments();
        String provincia = SaveSharedPreference.getProvincia(this.getContext());
        ImageView emptyImg = (ImageView) rootView.findViewById(R.id.imgEmpty);
        TextView emptyTxt = (TextView) rootView.findViewById(R.id.txtEmpty);
        TextView titolo = (TextView) rootView.findViewById(R.id.txtTitoloMainFragment);
        titolo.setText("Ecco i concerti nella provincia di \n" + provincia);
        String [] arrayConcertiVicini = bundle.getStringArray("nomeConcerto");
        ArrayAdapter<String> concertiVicini = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayConcertiVicini);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_vicini_view);
        listview.setAdapter(concertiVicini);
        if (concertiVicini.isEmpty()){
            emptyImg.setVisibility(View.VISIBLE);
            emptyTxt.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


}
