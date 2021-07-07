package com.test.feulmgmt.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.fuel.FuelsItem;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.NotificationSender;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderConfirmFragment extends Fragment {

    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack, imgDrawer;
    private AppCompatButton btnSuccess;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderConfirmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderConfirmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderConfirmFragment newInstance(String param1, String param2) {
        OrderConfirmFragment fragment = new OrderConfirmFragment();
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

        df2.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_confirm, container, false);
    }

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Order Success");

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);
        btnSuccess = view.findViewById(R.id.btnSuccess);

        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderConfirmationNotification();
                ((MainActivity) getActivity()).gotoDashboard();
            }
        });

        imgBack.setOnClickListener(view12 -> {
            FuelCaches.toOrderConfirm(false);
            orderConfirmationNotification();
            ((MainActivity) getActivity()).gotoDashboard();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        AppCompatTextView txtLitres = view.findViewById(R.id.txtLiters);
        txtLitres.setText(FuelCaches.getFuelLiters() + " Liters");

        AppCompatTextView txtTotal = view.findViewById(R.id.txtTotal);
        for (FuelsItem item : FuelCaches.getFuelsList()) {
            if (item.isSelected()) {
                txtTotal.setText("Total Price $" + df2.format(item.getPricePerGallon() * FuelCaches.getFuelLiters()));
            }
        }
    }

    private void orderConfirmationNotification() {
        for (FuelsItem item : FuelCaches.getFuelsList()) {
            if (item.isSelected()) {
                NotificationSender.sendNotification("You have a new Fuel Order", "Fuel Name : " + item.getName() +
                        "\nTotal Price $" + df2.format(item.getPricePerGallon() * FuelCaches.getFuelLiters()));
            }
        }
    }

}