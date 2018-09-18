package com.meeting.binary.android.binarymeeting.other;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.contact.document_photo.DocumentActivity;
import com.meeting.binary.android.binarymeeting.contact.document_photo.DocumentFragment;
import com.meeting.binary.android.binarymeeting.event.MyEventListActivity;
import com.meeting.binary.android.binarymeeting.message.ChatListActivity;
import com.meeting.binary.android.binarymeeting.message.MessageActivity;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.model.TodoItem;
import com.meeting.binary.android.binarymeeting.profile.ProfileActivity;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.ContactActivity;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.realm_labs.ContactLab;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.start.HomeActivity;
import com.meeting.binary.android.binarymeeting.start.LoginActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by loyck_daryl on 2017/10/11.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private RealmList<Contact> items;
    private Realm myRealm;
    private View mHeaderLayout;
    private BottomNavigationView mNavigationView;
    private MyContactPage mMyContactPage;

    private static final String TAG = "getname";
    private List<Contact> mContacts;
    private List<Event> mEvents;

    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private ImageView headerview;
    private TextView name;
    private TextView email;

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment_master);

        setFragment();
        loadContactProfile();
        myRealm = Realm.getDefaultInstance();
        /**get the contact data from realm*/

        RealmResults<Contact> contactResults = myRealm.where(Contact.class).findAll();
        items = getItems(contactResults);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /** getting the navigation drawer header and set the listener*/
        mHeaderLayout = navigationView.getHeaderView(0);
        headerview = mHeaderLayout.findViewById(R.id.photo_header);
        name = mHeaderLayout.findViewById(R.id.nameHeader);

        headerview.setOnClickListener((e) -> {
            Intent intent = ProfileActivity.newIntent(getApplicationContext(), (Serializable) mMyContactPage);
            startActivity(intent);
        });


        progressBar = findViewById(R.id.progressbar);



        /**getting the bottom navigation and set up the lister*/
        mNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(mNavigationView);
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_home : {
                        Intent intent = HomeActivity.newIntent(getApplicationContext());
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_contacts : {
                        requestContactDataLoader();
                        break;
                    }
                    case R.id.action_messages : {
                        Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_more : {
                        drawer.openDrawer(GravityCompat.START);
                        break;
                    }
                }
                return true;
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        drawer.requestLayout();
        loadImageProfile();
        loadProfileName();
    }

    public RealmList<Contact> getItems(RealmResults<Contact> results){
        RealmList<Contact> contacts = new RealmList<>();
        for (Contact item : results){
            contacts.add(0, item);
        }
        return contacts;
    }





    private void loadContactProfile() {
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<MyContactPage> contactProfile = requestWebServiceInterface.getContactProfile(CookiePreferences.getStoredName(SingleFragmentActivity.this));
        contactProfile.enqueue(new Callback<MyContactPage>() {
            @Override
            public void onResponse(Call<MyContactPage> call, Response<MyContactPage> response) {
                if (response.isSuccessful() && response.body() != null){
                    mMyContactPage = response.body();
                }
            }

            @Override
            public void onFailure(Call<MyContactPage> call, Throwable t) {
                Log.e(TAG, "onFailure: loadContactProfile failed");
            }
        });
    }










    private void loadProfileName() {
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<Map<String, String>> profileName = requestWebServiceInterface.getProfileName();
        profileName.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null){
                    Map<String, String> mapName = response.body();
                    SingleFragmentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(mapName.get("name"));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {

            }
        });
    }



    private void loadImageProfile() {
        String recupCookie = CookiePreferences.getStoredCookie(SingleFragmentActivity.this);
        Log.i(TAG, "intercept: " + recupCookie);
        LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + CookiePreferences.getStoredName(SingleFragmentActivity.this), builder.build());

        Glide.with(SingleFragmentActivity.this)
                .load(glideUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                .into(new BitmapImageViewTarget(headerview) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(SingleFragmentActivity.this.getResources(),
                                Bitmap.createScaledBitmap(resource, 75, 75, false));
                        drawable.setCircular(true);
                        headerview.setImageDrawable(drawable);
                    }
                });
    }




    public void requestContactDataLoader() {
//        progressBar.setVisibility(View.VISIBLE);
        Toast toast = Toast.makeText(SingleFragmentActivity.this, "loading your contacts....", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<List<Contact>> listCall = requestWebServiceInterface.getContacts();
        listCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful()){
//                    progressBar.setVisibility(View.GONE);
                    mContacts = response.body();
                    Log.i(TAG, "onResponse: " + mContacts.get(0).getName());
                    Log.i(TAG, "onResponse: response successfully operated");
                    Intent intent = ContactActivity.newIntent(getApplicationContext(), mContacts);
                    startActivity(intent);
                } else {
//                    progressBar.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(SingleFragmentActivity.this, "failed to load contacts", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(SingleFragmentActivity.this, "failed to load data", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure: failed to request data");
            }
        });
    }




//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_events) {
            requestDataEvent();
            drawer.closeDrawers();
        } else if (id == R.id.action_search) {
            drawer.closeDrawers();
            Toast.makeText(this, "get it", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_settings) {
            drawer.closeDrawers();
            Toast toast = Toast.makeText(this, "Go To Event", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        } else if (id == R.id.action_activities) {
            drawer.closeDrawers();
            Toast toast = Toast.makeText(this, "No Activity for now", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        } else if (id == R.id.action_logout) {
            drawer.closeDrawers();
            logoutRequest();
        } else if (id == R.id.action_file) {
            drawer.closeDrawers();
            Toast toast = Toast.makeText(this, "Go To Event to see the file", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
        return true;
    }



    /**
     * ==================================
     * load the data from the web service
     * ==================================
     */
    public void requestDataEvent(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<List<Event>> call = requestWebServiceInterface.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                mEvents = response.body();
//                Log.i(TAG, "onResponse: " + mEvents.size() + " " + mEvents.get(0).getTopic());
                Intent intent = MyEventListActivity.newIntent(getApplicationContext(), mEvents);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });

    }

    /**set corresponding fragment in the layout*/
    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    private void logoutRequest(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<ResponseBody> callLogout = requestWebServiceInterface.logout();
        callLogout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    CookiePreferences.deleteOnLogout(getApplicationContext());
                    Intent intent = LoginActivity.newIntent(getApplicationContext());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SingleFragmentActivity.this, "log out failed, check your internet connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: request failed");
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        myRealm.close();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }
}
