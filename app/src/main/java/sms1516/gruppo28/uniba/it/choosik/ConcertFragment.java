package sms1516.gruppo28.uniba.it.choosik;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConcertFragment extends Fragment {


    public ConcertFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_concert, container, false);
        /**
         *
         View rootView = inflater.inflate(R.layout.fragment_concert, container, false);

         ArrayList<ListviewContactItem> listContact = GetlistContact();
         ListView lv = (ListView)getActivity().findViewById(R.id.lv_contact);
         lv.setAdapter(new ListviewContactAdapter(getActivity(), listContact));

         return rootView;
         */


    }




}
