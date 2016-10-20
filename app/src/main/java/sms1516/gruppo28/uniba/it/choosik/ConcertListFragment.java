package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertListFragment extends Fragment {


    public ConcertListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        QueryTask concerti = new QueryTask(getContext());
        concerti.execute("SELECT");
        ArrayList<String> array=concerti.risultato;
        return inflater.inflate(R.layout.fragment_concert, container, false);
        }




}
