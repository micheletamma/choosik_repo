package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marcouva on 31/10/16.
 */

public class MyConcertsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String [] nomeTappa = bundle.getStringArray("nomeTour");
        String jsonArrayString = bundle.getString("jsonArrayTappe");

        View rootView = inflater.inflate(R.layout.fragment_concert, container, false);
        ListView listview = (ListView) rootView.findViewById(R.id.lista_concerti_view);

        try {
            JSONArray jsonArrayTappe = new JSONArray(jsonArrayString);
            final String[] nomeTappeArray = new String[jsonArrayTappe.length()];
            final String[] luogoTappeArray = new String[jsonArrayTappe.length()];
            final String[] dataTappeArray = new String[jsonArrayTappe.length()];
            for (int i = 0; i <= jsonArrayTappe.length() - 1; i++) {
                JSONObject temp = jsonArrayTappe.getJSONObject(i);
                nomeTappeArray[i]=temp.getJSONObject("tour").getString("nomeTour");
                luogoTappeArray[i] = temp.getString("citta");
                dataTappeArray[i] = temp.getString("data");

            }
            ArrayAdapter <String> nomiTappe = new ArrayAdapter <String>(getActivity(), R.layout.list_concert_item, R.id.nomeTour, nomeTappeArray){
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.list_concert_item,parent,false);
                    }

                    TextView nomeTour = (TextView) convertView.findViewById(R.id.nomeTour);
                    nomeTour.setText(nomeTappeArray[position]);
                    TextView luogoTour = (TextView) convertView.findViewById(R.id.luogoTour);
                    luogoTour.setText(luogoTappeArray[position]);
                    TextView dataTour = (TextView) convertView.findViewById(R.id.dataTour);
                    dataTour.setText(dataTappeArray[position]);

                    return convertView;
                }
            };

            listview.setAdapter(nomiTappe);
            ImageView emptyImg = (ImageView) rootView.findViewById(R.id.imgEmpty);
            TextView emptyTxt = (TextView) rootView.findViewById(R.id.txtEmpty);
            if (nomiTappe.isEmpty()){
                emptyImg.setVisibility(View.VISIBLE);
                emptyTxt.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return rootView;
    }
}
