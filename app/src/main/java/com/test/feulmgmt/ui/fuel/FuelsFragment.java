package com.test.feulmgmt.ui.fuel;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
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
import com.test.feulmgmt.pojos.fuel.FuelResponse;
import com.test.feulmgmt.pojos.fuel.FuelsItem;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.FuelPrefs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FuelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FuelsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatTextView txtTitle;
    private ProgressBar progress_bar;
    private AppCompatImageView imgBack, imgDrawer;
    private RecyclerView feedbackList;
    private FuelPrefs prefs;
    private FuelResponseAdapter adapter;
    private FuelResponse fuelResponse;
    private AppCompatButton addFuel;

    public FuelsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FuelsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FuelsFragment newInstance(String param1, String param2) {
        FuelsFragment fragment = new FuelsFragment();
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
        return inflater.inflate(R.layout.fragment_fuels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new FuelPrefs();
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Fuels");
        progress_bar = view.findViewById(R.id.progress_bar);

        imgBack = view.findViewById(R.id.imgBack);
        imgDrawer = view.findViewById(R.id.imgDrawer);

        imgBack.setOnClickListener(view12 -> {
            ((MainActivity) getActivity()).onBackPressed();
        });

        imgDrawer.setOnClickListener(view1 -> ((MainActivity) getActivity()).showDrawer());

        feedbackList = view.findViewById(R.id.feedbackList);
        feedbackList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        fetchFuelPrice();

        addFuel = view.findViewById(R.id.addFuel);
        addFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                View view1 = LayoutInflater.from(requireContext()).inflate(R.layout.layout_edit_fuel, null);
                builder.setView(view1);
                AppCompatEditText edtName = view1.findViewById(R.id.edtName);
                AppCompatEditText edtPrice = view1.findViewById(R.id.edtPrice);
                edtPrice.setVisibility(View.VISIBLE);
                AppCompatButton btnSubmit = view1.findViewById(R.id.btnSubmit);
                AlertDialog dialog = builder.create();
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtName.getText().toString().isEmpty() && edtPrice.getText().toString().isEmpty()) {
                            Toast.makeText(requireContext(), "Please enter the required fields", Toast.LENGTH_SHORT).show();
                        } else {
                            progress_bar.setVisibility(View.VISIBLE);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("name", edtName.getText().toString());
                            jsonObject.addProperty("pricePerGallon", edtPrice.getText().toString());
                            Ion.with(requireContext()).
                                    load(ApiUrls.CREATE_FUEL_TYPE).setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                                    setJsonObjectBody(jsonObject).
                                    asJsonObject().
                                    setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            progress_bar.setVisibility(View.GONE);
                                            dialog.dismiss();
                                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                                fetchFuelPrice();
                                            } else {
                                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });


                dialog.show();
            }
        });
    }

    private void fetchFuelPrice() {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(requireContext()).load(ApiUrls.FUEL).asJsonObject().setCallback((e, result) -> {
            progress_bar.setVisibility(View.GONE);
            if (result != null) {
                if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                    fuelResponse = new Gson().fromJson(result, FuelResponse.class);
                    adapter = new FuelResponseAdapter(fuelResponse.getData().getFuels(), (id, check, pos) -> {
                        if (check) {
                            deleteFuelItem(id, pos);
                        } else {
                            editfuelItem(id, pos);
                        }
                    });
                    feedbackList.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void editfuelItem(int id, int pos) {
        showAlertDialogForEdit(id, pos);
    }

    private void showAlertDialogForEdit(int id, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_edit_fuel, null);
        builder.setView(view);
        AppCompatEditText edtName = view.findViewById(R.id.edtName);
        AppCompatEditText edtPrice = view.findViewById(R.id.edtPrice);

        AppCompatButton btnSubmit = view.findViewById(R.id.btnSubmit);
        String name = fuelResponse.getData().getFuels().get(pos).getName();
        Double price = fuelResponse.getData().getFuels().get(pos).getPricePerGallon();
        edtName.setText(Editable.Factory.getInstance().newEditable(name));
        edtPrice.setText(Editable.Factory.getInstance().newEditable(String.valueOf(price)));
        AlertDialog dialog = builder.create();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                JsonObject jsonObject = new JsonObject();
                if (edtName.getText().length() <= 0 || edtPrice.getText().length() <= 0){
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("name", edtName.getText().toString());
                jsonObject.addProperty("pricePerGallon", edtPrice.getText().toString());
                Ion.with(requireContext()).load("PUT", ApiUrls.EDIT_FUEL_ITEM).
                        setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                        setJsonObjectBody(jsonObject).
                        asJsonObject().
                        setCallback((e, result) -> {
                            progress_bar.setVisibility(View.GONE);
                            dialog.dismiss();
                            if (result == null){
                                Toast.makeText(requireContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                FuelsItem item = new Gson().fromJson(result.get("data").getAsJsonObject(), FuelsItem.class);
                                fuelResponse.getData().getFuels().add(pos, item);
                                fetchFuelPrice();
                            } else {
                                Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialog.show();

    }

    private void deleteFuelItem(int id, int pos) {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(requireContext()).load("DELETE", ApiUrls.DELETE_FUEL + id).
                setHeader("Authorization", "Bearer " + prefs.getUserData().getToken()).
                asJsonObject().
                setCallback((e, result) -> {
                    progress_bar.setVisibility(View.GONE);
                    if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                        Toast.makeText(requireContext(), result.get("meta").getAsJsonObject().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        fetchFuelPrice();
                    }
                });
    }
}