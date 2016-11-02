package sms1516.gruppo28.uniba.it.choosik;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Michele on 02/11/2016.
 */
public class MainFragment extends Fragment {

    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_fragment, container, false);



        return rootView;
    }


}
