package com.test.feulmgmt.ui.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.test.feulmgmt.FuelApplication;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.userLists.UsersItem;
import com.test.feulmgmt.utils.ApiUrls;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter {
    private final List<UsersItem> datas;

    public UsersAdapter(List<UsersItem> users) {
        this.datas = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return (new UsersHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UsersItem item = datas.get(position);
        if (item.getProfilePicture() != null)
            Picasso.get().load(ApiUrls.USER_IMAGE + item.getProfilePicture()).into(imgUser);
        txtName.setText(item.getFullName());
        txtEmail.setText(item.getEmail());
        txtAddress.setText(item.getPhone());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private AppCompatImageView imgUser;
    private AppCompatTextView txtName, txtEmail, txtAddress;

    private class UsersHolder extends RecyclerView.ViewHolder {
        public UsersHolder(View view) {
            super(view);
            imgUser = view.findViewById(R.id.imgUser);
            txtAddress = view.findViewById(R.id.txtAddress);
            txtEmail = view.findViewById(R.id.txtEmail);
            txtName = view.findViewById(R.id.txtName);
        }
    }
}
