package com.test.feulmgmt;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.test.feulmgmt.pojos.fuel.FuelsItem;
import com.test.feulmgmt.pojos.orders.OrderResponse;
import com.test.feulmgmt.ui.auth.LoginActivity;
import com.test.feulmgmt.ui.changepassword.ChangePassword;
import com.test.feulmgmt.ui.dashboard.MapsFragment;
import com.test.feulmgmt.ui.order.OrderConfirmFragment;
import com.test.feulmgmt.ui.order.OrderDetailsFragment;
import com.test.feulmgmt.ui.pending.PendingOrdersFragment;
import com.test.feulmgmt.ui.profile.ProfileFragment;
import com.test.feulmgmt.utils.ApiUrls;
import com.test.feulmgmt.utils.FuelCaches;
import com.test.feulmgmt.utils.FuelPrefs;
import com.test.feulmgmt.utils.LocationUtils;
import com.test.feulmgmt.utils.NotificationSender;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ProfileFragment profileFragment;
    private FuelPrefs prefs;
    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        prefs = new FuelPrefs();
        locationUtils = new LocationUtils();
        locationUtils.fetchLocation();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);  // Hostfragment
        NavInflater inflater = navHostFragment.getNavController().getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.mobile_navigation);

        if (prefs.getUserData().getUserDetail().getRole().equalsIgnoreCase("user")) {
                graph.setStartDestination(R.id.nav_dashboard);
                hideMenus(false);
        } else {
            graph.setStartDestination(R.id.nav_pending_orders);
            hideMenus(true);
        }

        navHostFragment.getNavController().setGraph(graph);
        NavigationUI.setupWithNavController(navigationView, navHostFragment.getNavController());
        saveFcmToken();
    }

    private void hideMenus(boolean isAdmin) {
        Menu nav_Menu = navigationView.getMenu();
        if (isAdmin) {
            nav_Menu.findItem(R.id.nav_dashboard).setVisible(false);
            nav_Menu.findItem(R.id.nav_invite_friends).setVisible(false);
            nav_Menu.findItem(R.id.nav_feedback).setVisible(false);
            nav_Menu.findItem(R.id.nav_booking_history).setTitle("Payment Report");
            nav_Menu.findItem(R.id.nav_pending_orders).setTitle("Manage Bookings");
        } else {
            nav_Menu.findItem(R.id.nav_users).setVisible(false);
            nav_Menu.findItem(R.id.nav_feedback_list).setVisible(false);
            nav_Menu.findItem(R.id.nav_manage_fuels).setVisible(false);
        }
        nav_Menu.findItem(R.id.nav_logout).setOnMenuItemClickListener(this);
        nav_Menu.findItem(R.id.nav_invite_friends).setOnMenuItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showDrawer() {
        if (!drawer.isDrawerOpen(Gravity.START)) drawer.openDrawer(Gravity.START);
        else drawer.closeDrawer(Gravity.END);
    }

    public void launchOrderDetails() {
        Fragment fragment = OrderDetailsFragment.newInstance("string", "string");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logoutUser();
                break;
            case R.id.nav_invite_friends:
                inviteFriends();
                break;
            case R.id.nav_change_password:
                changePassword();
                break;
        }
        return false;
    }

    private void changePassword() {
        Fragment fragment = ChangePassword.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment).commit();
    }

    private void inviteFriends() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: www.google.com/" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    private void logoutUser() {
        prefs.saveUserResponse(null);
        prefs.setUserLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void launchCamera(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityLauncher.launch(cameraIntent);
    }

    //Profile picture update
    public void launchGallery(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                profileFragment.updateImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ActivityResultLauncher<Intent> cameraActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        profileFragment.updateImage(imageBitmap);
                    }
                }
            });

    public void moveToOrderConfirm() {
        FuelCaches.toOrderConfirm(false);
        Fragment fragment = OrderConfirmFragment.newInstance("string", "string");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment).commit();
    }

    public void gotoDashboard() {
        Fragment fragment = new MapsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment).commit();
    }

    private void saveFcmToken() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fcmToken", prefs.getFirebaseToken());
        jsonObject.addProperty("role", prefs.getUserData().getUserDetail().getRole());
        Ion.with(this).load("PATCH",ApiUrls.SAVE_FCM).setHeader("Authorization", "Bearer " + prefs.getUserData().getToken())
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null)
                            Log.e("fcm_token", result.get("meta").getAsJsonObject().get("message").getAsString());
                    }
                });
    }
}