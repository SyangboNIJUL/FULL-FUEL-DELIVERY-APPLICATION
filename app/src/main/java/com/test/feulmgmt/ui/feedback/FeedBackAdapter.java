package com.test.feulmgmt.ui.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.feedback.FeedbacksItem;
import com.test.feulmgmt.utils.ApiUrls;

import java.util.List;

public class FeedBackAdapter extends RecyclerView.Adapter {
    private final List<FeedbacksItem> datas;

    public FeedBackAdapter(List<FeedbacksItem> feedbacks) {
        this.datas = feedbacks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedback_item, parent, false);
        return (new FeedbackHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedbacksItem item = datas.get(position);
        txtFeed.setText(item.getMessage());
        txtUser.setText(item.getUser().getFullName());
        txtUser.setText(item.getUser().getEmail() + ", " + item.getUser().getAddress());
        if (item.getUser().getProfilePicture() != null)
            Picasso.get().load(ApiUrls.USER_IMAGE + item.getUser().getProfilePicture()).fit().into(imgUser);
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
    private AppCompatTextView txtFeed, txtUser, txtAddress;

    private class FeedbackHolder extends RecyclerView.ViewHolder {
        public FeedbackHolder(View view) {
            super(view);
            imgUser = view.findViewById(R.id.imgUser);
            txtUser = view.findViewById(R.id.txtUser);
            txtAddress = view.findViewById(R.id.txtAddress);
            txtFeed = view.findViewById(R.id.txtFeedback);
        }
    }
}
