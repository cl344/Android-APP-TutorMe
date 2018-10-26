package compsci290.edu.duke.tutor.pendingrequests;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Request;


public class PendingRequests extends AppCompatActivity {

    private final String TAG = "PendingRequests";

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PendingRequestsAdapter mAdapter;
    private List<Request> requestList = new ArrayList<>();

    private String mTutorID;
    private String mUserInfoId;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pending Requests");

        mUserInfoId = getIntent().getStringExtra("userInfoId");

        mRecyclerView = (RecyclerView) findViewById(R.id.pending_request_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pending_request_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequest();
            }
        });

        mAdapter = new PendingRequestsAdapter(requestList, this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadTutor();
    }

    private void loadRequest() {

        AVObject tutor = AVObject.createWithoutData(getResources().getString(R.string.database_table_tutor), mTutorID);
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_request));
        query.whereEqualTo(getResources().getString(R.string.database_table_request_tutorID), tutor);
        query.include(getResources().getString(R.string.database_table_request_tuteeID));
        query.include(getResources().getString(R.string.database_table_request_tutorID));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "no requests to show");
                    } else {
                        for(AVObject avObject : list) {
                            Request request = (Request) avObject;
                            boolean isUpdated = request.isUpdated();
                            if (!requestList.contains(request)) {
                                requestList.add(request);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (isUpdated) {
                                    requestList.remove(request);
                                    requestList.add(request);
                                    request.setIsUpdated(false);
                                    request.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                Log.d(TAG, "update request. mark isUpdated back to false");
                                            } else {
                                                Log.d(TAG, "update request fails. + " + e.toString());
                                            }

                                        }
                                    });
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Log.d(TAG, "fetching Requests failure " + e.toString());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadTutor() {

        AVObject avObject = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), mUserInfoId);
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_tutor));
        query.whereEqualTo(getResources().getString(R.string.database_table_tutor_user), avObject);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "no tutor to show");
                    } else {
                        mTutorID = list.get(0).getObjectId();
                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                loadRequest();
                            }
                        });
                        loadRequest();
                    }
                } else {
                    Log.d(TAG, "fetching tutor failure " + e.toString());
                }
            }
        });
    }
}
