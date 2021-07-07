package com.test.feulmgmt.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.esewa.android.sdk.payment.ESewaConfiguration;
import com.esewa.android.sdk.payment.ESewaPayment;
import com.esewa.android.sdk.payment.ESewaPaymentActivity;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.Address;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;
import com.stripe.android.view.ShippingInfoWidget;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.fuel.FuelsItem;
import com.test.feulmgmt.stripe.AppShippingInformationValidator;
import com.test.feulmgmt.stripe.CheckoutActivityJava;
import com.test.feulmgmt.stripe.MyEphemeralKeyProvider;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.FuelPrefs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsFragment extends Fragment {

    private AppCompatTextView txtTitle, txtLtr, txtTotal;
    private AppCompatImageView imgBack, imgDrawer;
    private LinearLayoutCompat layoutGas;
    private AppCompatSeekBar seekbar;
    private AppCompatButton btnOrder;
    private FuelPrefs prefs;
    private ProgressBar progressBar;
    private boolean isFirstLaunched = true;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PaymentSession paymentSession;
    private List<FuelsItem> gasLists;
    private float litres;

    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private int selectedFuelid;
    private ESewaConfiguration eSewaConfiguration;

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailsFragment newInstance(String param1, String param2) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
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
        prefs = new FuelPrefs();
        eSewaConfiguration = new ESewaConfiguration()
                .clientId("JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R ")
                .secretKey("BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==")
                .environment(ESewaConfiguration.ENVIRONMENT_TEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Order Details");

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        txtTotal = view.findViewById(R.id.txtTotal);
        litres = FuelCaches.getFuelLiters();

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        layoutGas = view.findViewById(R.id.layoutGas);

        txtLtr = view.findViewById(R.id.txtLtr);
        seekbar = view.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                litres = (progress / 2.0f);
                txtLtr.setText(((float) litres) + " Gallons");
                FuelCaches.saveLitres(litres);
                for (FuelsItem item : gasLists) {
                    if (item.isSelected()) {
                        selectedFuelid = item.getId();
                        txtTotal.setText("Total Price $" + df2.format(item.getPricePerGallon() * litres));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnOrder = view.findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double selectedItemPrice = 0.0f;
                for (FuelsItem item : gasLists) {
                    if (item.isSelected()) {
                        selectedItemPrice = item.getPricePerGallon();
                    }
                }
                /*Intent intent = new Intent(getActivity(), CheckoutActivityJava.class);
                intent.putExtra("liters", litres);
                intent.putExtra("fuelTypeId", selectedFuelid);
                intent.putExtra("price", selectedItemPrice);*/
                ESewaPayment eSewaPayment = new ESewaPayment(String.valueOf(selectedItemPrice),
        "Fuel", String.valueOf(selectedFuelid),"<call_back_url >");

                Intent intent = new Intent(requireActivity(), ESewaPaymentActivity.class);
                intent.putExtra(ESewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration);

                intent.putExtra(ESewaPayment.ESEWA_PAYMENT, eSewaPayment);
                startActivityForResult(intent, 100);
                startActivity(intent);
                isFirstLaunched = false;
            }
        });

        loadGasItem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) return;
                String message = data.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE);
                Log.i("Proof of Payment " , message);
                Toast.makeText(requireContext(), "SUCCESSFUL PAYMENT", Toast.LENGTH_SHORT).show();
                submitOrders("esewa");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Canceled By User", Toast.LENGTH_SHORT).show();
            } else if (resultCode == ESewaPayment.RESULT_EXTRAS_INVALID) {
                if (data == null) return;
                String message = data.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE);
                Log.i( "Proof of Payment " , message);
            }
        }
    }

    private void loadGasItem() {
        gasLists = FuelCaches.getFuelsList();
        layoutGas.removeAllViews();
        if (gasLists != null) {
            for (FuelsItem item : gasLists) {
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_orders_details_gas_item, null);
                LinearLayoutCompat lgs = view.findViewById(R.id.layoutGasItem);
                LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                lgs.setWeightSum(0.33f);
                params.weight = 0.33f;
                lgs.setLayoutParams(params);
                AppCompatImageView imgGas = view.findViewById(R.id.imgGas);
                AppCompatTextView gasName = view.findViewById(R.id.gasName);
                gasName.setText(item.getName());
                if (item.isSelected()) {
                    selectedFuelid = item.getId();
                    lgs.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.red_rectangle));
                    imgGas.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                    gasName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    txtTotal.setText("Total Price $" + df2.format(item.getPricePerGallon() * litres));
                }
                lgs.setOnClickListener(view1 -> {
                    changeSelected(item.getId());
                });
                layoutGas.addView(view);
            }
        }
    }

    private void changeSelected(int id) {
        for (FuelsItem item : gasLists) {
            if (item.getId() == id) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        loadGasItem();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (FuelCaches.isToOrder())
            ((MainActivity) getActivity()).moveToOrderConfirm();
    }
    private void submitOrders(String paymentMethodId) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quantity", String.valueOf(litres));
        jsonObject.addProperty("lattitude", String.valueOf(FuelCaches.getSelectedLatLng().latitude));
        jsonObject.addProperty("longitude", String.valueOf(FuelCaches.getSelectedLatLng().longitude));
        jsonObject.addProperty("transactionToken", paymentMethodId);
        jsonObject.addProperty("fuelTypeId", String.valueOf(selectedFuelid));
        Ion.with(this).load(ApiUrls.SUBMIT_ORDERS).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                setJsonObjectBody(jsonObject).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.setVisibility(View.GONE);
                        if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                            FuelCaches.toOrderConfirm(true);
                            ((MainActivity)getActivity()).moveToOrderConfirm();
                        } else {
                            Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}


