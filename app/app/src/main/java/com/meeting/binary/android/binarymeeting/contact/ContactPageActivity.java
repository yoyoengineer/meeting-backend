package com.meeting.binary.android.binarymeeting.contact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.upload_download.PagerAdapter;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadPhotoFragmentBar;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewPhotoUploadedFragment;
import com.meeting.binary.android.binarymeeting.message.ChatListActivity;
import com.meeting.binary.android.binarymeeting.message.MessageActivity;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.Message;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.other.BottomNavigationViewHelper;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;
import com.meeting.binary.android.binarymeeting.profile.ProfileActivity;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.start.HomeActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactPageActivity extends AppCompatActivity{

    private static final String MY_CONTACT_PAGE_EXTRA_MESSAGE = "com.binary.myContactPage";
    private static final String TAG = "contactpageactivity";

    private TabLayout  mContsct_page_tab;
    private ViewPager mViewPager;
    private ContactPager mPagerAdapter;


    private TextView mName;
    private ImageView messageContact;

    private Message mMessage;


    private ImageView mContactImage;
    private MyContactPage mMyContactPage;

    // @Override
//    public Fragment createFragment() {
//        MyContactPage contactPage = (MyContactPage)getIntent().getParcelableExtra(MY_CONTACT_PAGE_EXTRA_MESSAGE);
//        return ContactPageFragment.newInstance(contactPage);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mMyContactPage = (MyContactPage)getIntent().getParcelableExtra(MY_CONTACT_PAGE_EXTRA_MESSAGE);

        mName = (TextView) findViewById(R.id.name_contact_page_profile);
        mContactImage = (ImageView) findViewById(R.id.image_contact_page);
        messageContact = (ImageView) findViewById(R.id.message_contact);

        mContsct_page_tab = (TabLayout)findViewById(R.id.contact_tab_layout);
        mPagerAdapter = new ContactPager(getSupportFragmentManager());
         mViewPager = (ViewPager)findViewById(R.id.contact_pager);


        mName.setText(mMyContactPage.getName());

        requestLoadImag();

        String id = CookiePreferences.getStoredName(ContactPageActivity.this) + mMyContactPage.getUsername();
        mMessage = new Message();
        mMessage.setId(id);
        mMessage.setToUser(mMyContactPage.getUsername());

        messageContact.setOnClickListener(view -> {
            Intent intent = MessageActivity.newIntent(ContactPageActivity.this, mMessage);
            startActivity(intent);
        });



        mPagerAdapter.addFragment(ContactProfileFragment.newInstance(mMyContactPage), "My profile");
        mPagerAdapter.addFragment(ContactPageFragment.newInstance(mMyContactPage), "My Events");

        mViewPager.setAdapter(mPagerAdapter);
        mContsct_page_tab.setupWithViewPager(mViewPager);

    }

    public static Intent newIntent(Context context, MyContactPage myContactPage){
        Intent intent = new Intent(context, ContactPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MY_CONTACT_PAGE_EXTRA_MESSAGE, myContactPage);
        intent.putExtras(bundle);
        return intent;
    }





    /**
     * load the image of the contact
     */
    public void requestLoadImag(){
        String recupCookie = CookiePreferences.getStoredCookie(ContactPageActivity.this);
        LazyHeaders.Builder builder = new LazyHeaders.Builder()
                .addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/picture/"+mMyContactPage.getPhoto() , builder.build());
        Glide.with(this)
                .load(glideUrl)
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                .into(new BitmapImageViewTarget(mContactImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ContactPageActivity.this.getResources(),
                                Bitmap.createScaledBitmap(resource, 65, 65, false));
                        drawable.setCircular(true);
                        mContactImage.setImageDrawable(drawable);
                    }
                });
    }


}
