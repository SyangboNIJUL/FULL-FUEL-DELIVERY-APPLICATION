package com.test.feulmgmt.ui.price;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagePriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagePriceFragment extends Fragment {

    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack,imgDrawer;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManagePriceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagePriceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagePriceFragment newInstance(String param1, String param2) {
        ManagePriceFragment fragment = new ManagePriceFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_price, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Manage Price");

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity)getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity)getActivity()).showDrawer());
    }
}