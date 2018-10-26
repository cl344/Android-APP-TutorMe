package compsci290.edu.duke.tutor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.activity.LCIMConversationListFragment;
import cn.leancloud.chatkit.utils.LCIMConstants;
import compsci290.edu.duke.tutor.chat.CustomUserProvider;
import compsci290.edu.duke.tutor.loginregistration.LoginActivity;
import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.mainfragments.CoursesFragment;
import compsci290.edu.duke.tutor.mainfragments.TuteeProfileFragment;
import compsci290.edu.duke.tutor.mainfragments.TutorProfileFragment;
import compsci290.edu.duke.tutor.mainfragments.TutorsFragment;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.adapters.RequestSent;
import compsci290.edu.duke.tutor.pendingrequests.PendingRequests;
import compsci290.edu.duke.tutor.pendingsessions.PendingSessionFragment;

//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoginType mMODE;
    protected String mUserInfoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMODE = LoginType.detachFrom(getIntent());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        loadUserInfo();
    }

    //Set menu depending on login type
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mMODE == LoginType.TUTEE) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            //Has request page
        } else {
            getMenuInflater().inflate(R.menu.menu_tutor, menu);
    }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //start about page for this app
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutPage.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_signout) {
            onSignOut();
        }

        if (id == R.id.action_request_sent) {
            Intent intent = new Intent(MainActivity.this, RequestSent.class);
            intent.putExtra("userInfoId", mUserInfoId);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_pending_request) {
            Intent intent = new Intent(MainActivity.this, PendingRequests.class);
            intent.putExtra("userInfoId", mUserInfoId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // pass user mode to fragment to load different elements
        Bundle bundle = new Bundle();
        bundle.putSerializable("mode", mMODE);
        bundle.putString("userInfoId", mUserInfoId);

        // determine user mode and load different fragment. reuse activity

        if (mMODE == LoginType.TUTOR) {
            TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
            tutorProfileFragment.setArguments(bundle);
            PendingSessionFragment pendingSessionFragment = new PendingSessionFragment();
            pendingSessionFragment.setArguments(bundle);
            adapter.addFragment(pendingSessionFragment, "Pending Sessions");
            adapter.addFragment(new LCIMConversationListFragment(),"Messages");
            adapter.addFragment(tutorProfileFragment, "Me");

        } else if (mMODE == LoginType.TUTEE) {
            adapter.addFragment(new CoursesFragment(), "Courses");
            TutorsFragment tutorsFragment = new TutorsFragment();
            tutorsFragment.setArguments(bundle);
            adapter.addFragment(tutorsFragment, "My Tutors");

            adapter.addFragment(new LCIMConversationListFragment(),"Messages");
            TuteeProfileFragment tuteeProfileFragment = new TuteeProfileFragment();
            tuteeProfileFragment.setArguments(bundle);
            adapter.addFragment(tuteeProfileFragment, "Me");

        } else {
            Log.d(TAG, "mMode returns wrong mode");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    protected OnBackPressedListener onBackPressedListener;

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    //Set listener to back
    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }

    //When signout is pressed
    public void onSignOut() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent RegisterIntent = new Intent(MainActivity.this,LoginActivity.class);
                        MainActivity.this.startActivity(RegisterIntent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    //Load info about current user from database
    private void loadUserInfo() {

        AVObject avObject = AVObject.createWithoutData(getResources().getString(R.string.database_table_user), AVUser.getCurrentUser().getObjectId());
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_userinfo));
        query.whereEqualTo("uid", avObject);
        query.include("uid");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0 || list.size() > 1) {
                        Log.d(TAG, "user info size incorrect");
                    } else {
                        mUserInfoId = list.get(0).getObjectId();
                        setupViewPager(viewPager);
                    }
                } else {
                    Log.d(TAG, "fetch user info " + e.toString());
                }
            }
        });
    }

    private void gotoSquareConversation() {
        List<LCChatKitUser> userList = CustomUserProvider.getInstance().getAllUsers();
        List<String> idList = new ArrayList<>();
        for (LCChatKitUser user : userList) {
            idList.add(user.getUserId());
        }
        LCChatKit.getInstance().getClient().createConversation(
                idList, "square", null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        Intent intent = new Intent(MainActivity.this, LCIMConversationActivity.class);
                        intent.putExtra(LCIMConstants.CONVERSATION_ID, avimConversation.getConversationId());
                        startActivity(intent);
                    }
                });
    }
}