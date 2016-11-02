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
 * Created by marcouva on 31/10/16.
 */

public class MyConcertsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String [] nomeTappa = bundle.getStringArray("nomeTour");
        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);
        ArrayAdapter <String> nomiTappe = new ArrayAdapter <String>(getActivity(), android.R.layout.simple_list_item_1, nomeTappa);
        listview.setAdapter(nomiTappe);
        ImageView emptyImg = (ImageView) rootView.findViewById(R.id.imgEmpty);
        TextView emptyTxt = (TextView) rootView.findViewById(R.id.txtEmpty);
        if (nomiTappe.isEmpty()){
            emptyImg.setVisibility(View.VISIBLE);
            emptyTxt.setVisibility(View.VISIBLE);
        }

        return rootView;
    }
}
