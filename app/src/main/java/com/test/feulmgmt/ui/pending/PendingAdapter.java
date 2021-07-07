package com.test.feulmgmt.ui.pending;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.orders.OrdersItem;
import com.test.feulmgmt.pojos.users.UserData;
import com.test.feulmgmt.utils.FuelPrefs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class PendingAdapter extends RecyclerView.Adapter {
    private final PendingOrderCallback callback;
    private final List<OrdersItem> orders;
    private final UserData user;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    public PendingAdapter(List<OrdersItem> orderResponse, PendingOrderCallback pendingOrderCallback) {
        this.callback = pendingOrderCallback;
        this.orders = orderResponse;
        FuelPrefs prefs = new FuelPrefs();
        user = prefs.getUserData();
        df2.setRoundingMode(RoundingMode.DOWN);
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
        if (item.getStatusId() == 1) {
            Geocoder geocoder = new Geocoder(FuelApplication.getContext(), Locale.ENGLISH);
            try {
                List<Address> address = geocoder.getFromLocation(item.getLattitude(), item.getLongitude(), 1);
                txtAddress.setText(address.get(0).getFeatureName()+","+address.get(0).getLocality() + ", " + address.get(0).getSubAdminArea());
                if(user.getUserDetail().getRole().equalsIgnoreCase("user")){
                    txtName.setText(user.getUserDetail().getFullName());
                    txtPhone.setText((user.getUserDetail().getPhone() == null ? "N.A": user.getUserDetail().getPhone()));
                    txtEmail.setText(user.getUserDetail().getEmail());
                }else{
                txtName.setText(item.getUser().getFullName());
                txtPhone.setText((item.getUser().getPhone() == null ? "N.A": item.getUser().getPhone()));
                txtEmail.setText(item.getUser().getEmail());
                }
                liters.setText(item.getQuantity() + " " + item.getUnit());
                if (user.getUserDetail().getRole().equalsIgnoreCase("Admin")) {
                    liters.setVisibility(View.VISIBLE);
                    layoutConfirm.setVisibility(View.VISIBLE);
                }
                gasName.setText(item.getFuelType().getName());
                txtAmt.setText("$ " + df2.format(item.getQuantity() * item.getFuelType().getPricePerGallon()));
                btnAccept.setOnClickListener(view -> callback.onStatusUpdate(item.getId(),2,position));
                btnCancel.setOnClickListener(view -> callback.onStatusUpdate(item.getId(),3,position));
                btnLocate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onLocateUser(item);
                    }
                });
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

    private AppCompatTextView gasName, txtName,txtPhone,txtEmail, txtAddress, txtAmt, liters, btnAccept, btnCancel,btnLocate;
    private LinearLayoutCompat layoutConfirm;

    private class BookingsHolder extends RecyclerView.ViewHolder {
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

    public interface PendingOrderCallback {

        void onStatusUpdate(int id, int status, int position);

        void onLocateUser(OrdersItem item);
    }
}
