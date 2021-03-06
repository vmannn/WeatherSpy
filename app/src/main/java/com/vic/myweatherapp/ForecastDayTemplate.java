//Victor Ochia. 2020 WeatherSpy


package com.vic.myweatherapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastDayTemplate#newInstance} factory method to
 * create an instance of this fragment.
 */

//Fragment class for a day within forecast.
public class ForecastDayTemplate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastDayTemplate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastDayTemplate newInstance(String param1, String param2) {
        ForecastDayTemplate fragment = new ForecastDayTemplate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     View view = inflater.inflate(R.layout.forecastdaytemplate, container, false);



        // Inflate the settingSize for this fragment
       return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(getArguments() != null) {
            String DailyHigh = getArguments().getString("dailyHigh");
            String DailyLow = getArguments().getString("dailyLow");
            String Icon = getArguments().getString("Icon");
            String Weekday = getArguments().getString("weekday");


        TextView high = Objects.requireNonNull(getView()).findViewById(R.id.high);
        high.setText(DailyHigh + "°F");
        TextView low = getView().findViewById(R.id.low);
        low.setText(DailyLow + "°F");
        TextView weekday = getView().findViewById(R.id.dayOfTheWeek);
        weekday.setText(Weekday);

        String url = MainActivity.GetIconUrl(Icon);
            ImageView IconPicture = getView().findViewById(R.id.TheIcon);

            Picasso.get().load(url).resize(100, 100).into(IconPicture);

        super.onViewCreated(view, savedInstanceState);
        }
    }
}