package com.test.feulmgmt.ui.fuel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.test.feulmgmt.R;
import com.test.feulmgmt.pojos.fuel.FuelsItem;

import java.util.List;

public class FuelResponseAdapter extends RecyclerView.Adapter {
    private final List<FuelsItem> datas;
    private final FuelListCallback callback;

    public FuelResponseAdapter(List<FuelsItem> fuels, FuelListCallback callback) {
        this.datas = fuels;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fuels_item, parent, false);
        return (new FuelsHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FuelsItem item = datas.get(position);
        gasName.setText(item.getName());
        txtGasName.setText(item.getName());
        txtPrice.setText("$" + item.getPricePerGallon() + " /Gallon");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onitemClicked(item.getId(), true, position);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onitemClicked(item.getId(), false, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private AppCompatTextView gasName, txtGasName, txtPrice, btnDelete, btnEdit;

    private class FuelsHolder extends RecyclerView.ViewHolder {
        public FuelsHolder(View view) {
            super(view);
            gasName = view.findViewById(R.id.gasName);
            txtGasName = view.findViewById(R.id.txtGasName);
            txtPrice = view.findViewById(R.id.txtPrice);
            btnDelete = view.findViewById(R.id.btnDelete);
            btnEdit = view.findViewById(R.id.btnEdit);
        }
    }

    public interface FuelListCallback {
        void onitemClicked(int id, boolean check, int pos);
    }
}
