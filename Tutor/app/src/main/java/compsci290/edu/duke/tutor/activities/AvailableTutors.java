package compsci290.edu.duke.tutor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compsci290.edu.duke.tutor.adapters.AvailableTutorsRecyclerViewAdapter;
import compsci290.edu.duke.tutor.adapters.RecyclerSource;
import compsci290.edu.duke.tutor.R;


public class AvailableTutors extends AppCompatActivity {

    private final String TAG = "AvailableTutors";
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private AvailableTutorsRecyclerViewAdapter mAdapter;
    private List<RecyclerSource> RecyclerList = new ArrayList<>();
    private String courseID;
    private String currentUserInfoId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabletutors);

        Bundle bundle = getIntent().getExtras();
        String classname = bundle.getString("name");
        String[] peopleNames = bundle.getStringArray("people");
        courseID = bundle.getString("courseID");
        mRecyclerView= (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AvailableTutorsRecyclerViewAdapter(RecyclerList, this);
        mRecyclerView.setAdapter(mAdapter);

        loadCurrentUserInfo(classname, peopleNames);
    }

    /*
    * load current user info from data base to filter himself being a tutor
    * */
    private void loadCurrentUserInfo(final String classname, final String[] peopleNames) {

        AVQuery<AVObject> userInfo = new AVQuery<>("UserInfo");
        userInfo.whereEqualTo("uid", AVUser.getCurrentUser());
        userInfo.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "no current user matched in UserInfo");
                    } else {
                        currentUserInfoId = list.get(0).getObjectId();
                        Log.d(TAG, "!!! current userInfoId " + currentUserInfoId);
                    }
                    OnGenerate(classname,peopleNames);
                } else {
                    Log.d(TAG, "fetch current user info fails with " + e.toString());
                }
            }
        });
    }

    //Get course data from database including tutors available
    public void OnGenerate(final String classname, final String[] peopleNames){

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(classname);

        if (peopleNames != null || peopleNames.length > 0) {
            for(String id : Arrays.asList(peopleNames)) {
                if (!id.equals(currentUserInfoId)) {
                    Log.d(TAG, "current people id + " + id);
                    AVObject avObject = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), id);
                    avObject.fetchInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (e == null) {
                                String tutorId = avObject.getObjectId();
                                String name = avObject.getString(getResources().getString(R.string.database_table_user_username));
                                RecyclerSource person = new RecyclerSource(name, tutorId, courseID);
                                RecyclerList.add(person);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "fetching tutor error");
                            }
                        }
                    });
                }
            }
        }

    }

    //Override back animation
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in,
                R.anim.trans_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.trans_right_in,
                    R.anim.trans_right_out);
            return true;
        }
        return false;
    }

}
