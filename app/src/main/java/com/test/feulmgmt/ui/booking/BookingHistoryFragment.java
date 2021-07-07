package com.test.feulmgmt.ui.booking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.MainActivity;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.orders.OrderResponse;
import com.test.feulmgmt.pojos.orders.OrdersItem;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.FuelPrefs;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingHistoryFragment extends Fragment {

    private AppCompatTextView txtTitle;
    private AppCompatImageView imgBack, imgDrawer;
    private RecyclerView bookingLists;
    private FuelPrefs prefs;
    private ProgressBar progress_bar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BookingsAdapter bookingAdapter;

    public BookingHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingHistoryFragment newInstance(String param1, String param2) {
        BookingHistoryFragment fragment = new BookingHistoryFragment();
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
        prefs = new FuelPrefs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTitle = view.findViewById(R.id.txtTitle);
        progress_bar = view.findViewById(R.id.progress_bar);
        if (prefs.getUserData().getUserDetail().getRole().equalsIgnoreCase("user")) {
            txtTitle.setText("Bookings");
            fetchAllOrdersUsers();
        } else {
            txtTitle.setText("Payment Report");
            fetchAllOrdersAdmin();
        }

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        bookingLists = view.findViewById(R.id.bookingLists);
        bookingLists.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void fetchAllOrdersAdmin() {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(this).load(ApiUrls.ALL_ORDERS_ADMIN).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progress_bar.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                OrderResponse orderResponse = new Gson().fromJson(result, OrderResponse.class);
                                setBookingsAdapter(orderResponse);
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    }
                });
    }

    private void setBookingsAdapter(OrderResponse orderResponse) {
        List<OrdersItem> ordersItems = new ArrayList<>();
        for (OrdersItem item : orderResponse.getData().getOrders()) {
            if (item.getStatusId() == 2)
                ordersItems.add(item);
        }
        if (ordersItems.size() == 0)
            Toast.makeText(requireContext(), "No current bookings", Toast.LENGTH_SHORT).show();
        bookingAdapter = new BookingsAdapter(ordersItems,requireActivity());
        bookingLists.setAdapter(bookingAdapter);
    }

    private void fetchAllOrdersUsers() {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(this).load(ApiUrls.ALL_ORDERS_USER).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progress_bar.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                OrderResponse orderResponse = new Gson().fromJson(result, OrderResponse.class);
                                setBookingsAdapter(orderResponse);
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}