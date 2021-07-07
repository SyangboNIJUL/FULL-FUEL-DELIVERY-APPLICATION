package com.test.feulmgmt.ui.booking;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.orders.OrderResponse;
import com.test.feulmgmt.pojos.orders.OrdersItem;
import com.test.feulmgmt.pojos.users.UserData;
import com.test.feulmgmt.utils.FuelPrefs;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class BookingsAdapter extends RecyclerView.Adapter {

    private final List<OrdersItem> orders;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private final FragmentActivity activity;
    private final UserData user;
    private FuelPrefs prefs;

    //constructor
    public BookingsAdapter(List<OrdersItem> orderResponse, FragmentActivity fragmentActivity) {
        this.orders = orderResponse;
        df2.setRoundingMode(RoundingMode.DOWN);
        prefs = new FuelPrefs();
        user = prefs.getUserData();
        this.activity = fragmentActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_item, parent, false);
        return (new BookingsHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OrdersItem item = orders.get(position);
        if (item.getStatusId() == 2) {
            Geocoder geocoder = new Geocoder(FuelApplication.getContext(), Locale.ENGLISH);
            try {
                List<Address> address = geocoder.getFromLocation(item.getLattitude(), item.getLongitude(), 1);
                txtAddress.setText(address.get(0).getFeatureName() + "," + address.get(0).getLocality() + ", " + address.get(0).getSubAdminArea());
                if(user.getUserDetail().getRole().equalsIgnoreCase("user")){
                    txtName.setText(user.getUserDetail().getFullName());
                    txtPhone.setText((user.getUserDetail().getPhone() == null ? "N.A": user.getUserDetail().getPhone()));
                    txtEmail.setText(user.getUserDetail().getEmail());
                }else{
                    txtName.setText(item.getUser().getFullName());
                    txtPhone.setText((item.getUser().getPhone() == null ? "N.A": item.getUser().getPhone()));
                    txtEmail.setText(item.getUser().getEmail());
                }
                gasName.setText(item.getFuelType().getName());
                liters.setText(item.getQuantity() + " " + item.getUnit());
                txtAmt.setText("$ " + df2.format(item.getQuantity() * item.getFuelType().getPricePerGallon()));
                if(prefs.getUserData().getUserDetail().getRole().equalsIgnoreCase("Admin")){
                    layoutConfirm.setVisibility(View.VISIBLE);
                    btnAccept.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnLocate.setOnClickListener(view -> {
                        Uri gmmIntentUri = Uri.parse("geo:"+item.getLattitude()+","+item.getLongitude()+"?q="+item.getLattitude()+","+item.getLongitude()+"("+item.getUser().getFullName()+")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(FuelApplication.getContext().getPackageManager()) != null) {
                            activity.startActivity(mapIntent);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private AppCompatTextView gasName, txtName, txtAddress, txtAmt,txtPhone,txtEmail,btnAccept,btnCancel,btnLocate,liters;
    private LinearLayoutCompat layoutConfirm;

    private class BookingsHolder extends RecyclerView.ViewHolder {
        //constructor
        public BookingsHolder(View view) {
            super(view);

            gasName = view.findViewById(R.id.gasName);
            txtName = view.findViewById(R.id.txtName);
            txtPhone = view.findViewById(R.id.txtPhone);
            txtEmail = view.findViewById(R.id.txtEmail);
            txtAddress = view.findViewById(R.id.txtAddress);
            txtAmt = view.findViewById(R.id.txtAmt);
            liters = view.findViewById(R.id.liters);

            layoutConfirm = view.findViewById(R.id.layoutConfirm);
            btnAccept = view.findViewById(R.id.btnAccept);
            btnCancel = view.findViewById(R.id.btnCancel);
            btnLocate = view.findViewById(R.id.btnLocate);
        }
    }
}
