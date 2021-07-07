package com.test.feulmgmt.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import com.test.feulmgmt.utils.LocationUtils;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.List;

public class MapsFragment extends Fragment {
    private LocationUtils locationUtils;
    private AppCompatImageView imgDrawer;
    private AppCompatEditText edtSearch;
    private ProgressBar progress_bar;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            location = locationUtils.fetchLocation();
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.style_json));

            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setHint("Search Location");

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            try {
                googleMap.clear();
                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                currentLatLng = sydney;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                MarkerOptions mp = new MarkerOptions();

                mp.position(new LatLng(currentLatLng.latitude, currentLatLng.longitude));

                mp.title("My position");
                FuelCaches.saveCurrentLocation(currentLatLng);
                googleMap.addMarker(mp);
            } catch (Exception e) {

            }

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    currentLatLng = latLng;
                    FuelCaches.saveCurrentLocation(currentLatLng);
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }
            });

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    currentLatLng = place.getLatLng();
                    FuelCaches.saveCurrentLocation(currentLatLng);
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(String.valueOf(place.getName())));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                }

                @Override
                public void onError(@NonNull Status status) {

                }
            });
        }
    };
    private Location location;
    private PlacesClient placesClient;
    private StringBuilder mResult;
    private BreakIterator mSearchResult;
    private AppCompatButton btnDeliver;
    private LatLng currentLatLng;
    private AppCompatTextView fuelPrice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationUtils = new LocationUtils();
        progress_bar = view.findViewById(R.id.progress_bar);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);

            initializePlacesApi();
        }
        imgDrawer = view.findViewById(R.id.imgDrawer);
        imgDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showDrawer();
            }
        });

        btnDeliver = view.findViewById(R.id.btnDeliver);
        btnDeliver.setOnClickListener(view1 -> ((MainActivity) getActivity()).launchOrderDetails());
        fuelPrice = view.findViewById(R.id.fuelPrice);
        initializeFuelPrice();
    }

    private void initializeFuelPrice() {
        progress_bar.setVisibility(View.VISIBLE);
        Ion.with(requireContext()).load(ApiUrls.FUEL).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                progress_bar.setVisibility(View.GONE);
                if (result != null) {
                    if (result.get("meta").getAsJsonObject().get("success").getAsBoolean()) {
                        FuelResponse fuelResponse = new Gson().fromJson(result, FuelResponse.class);
                        for (int i = 0; i < fuelResponse.getData().getFuels().size(); i++) {
                            if (i == 0) {
                                fuelResponse.getData().getFuels().get(i).setSelected(true);
                            } else {
                                fuelResponse.getData().getFuels().get(i).setSelected(false);
                            }
                        }
                        FuelCaches.saveFuelResponse(fuelResponse.getData().getFuels());
                        loadFueldatas(fuelResponse.getData().getFuels());
                    }
                }
            }
        });
    }

    private void loadFueldatas(List<FuelsItem> fuels) {
        for (FuelsItem item : fuels) {
            if (item.isSelected())
                fuelPrice.setText("$ " + item.getPricePerGallon() + " /Gallons");
        }
    }

    private void initializePlacesApi() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(requireContext());


    }

    @Override
    public void onResume() {
        super.onResume();
        if (FuelCaches.getFuelsList() != null)
            loadFueldatas(FuelCaches.getFuelsList());
    }
}