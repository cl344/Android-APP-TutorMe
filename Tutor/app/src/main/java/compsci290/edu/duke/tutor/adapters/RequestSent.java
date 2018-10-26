package compsci290.edu.duke.tutor.adapters;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
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


public class RequestSent extends AppCompatActivity {

    private final String TAG = "RequestSent Activity";
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected List<Request> requestList = new ArrayList<>();
    protected RequestSentRecyclerViewAdapter mAdapter;
    protected String userInfoId;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_sent);

        userInfoId = getIntent().getStringExtra("userInfoId");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.request_sent_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.request_sent_recyclerview);
        mAdapter = new RequestSentRecyclerViewAdapter(this, requestList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Requests Sent");

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequestSend();
            }
        });
        loadRequestSend();

    }

    protected void loadRequestSend() {
        AVObject user = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), userInfoId);

        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_request));
        query.whereEqualTo(getResources().getString(R.string.database_table_request_tuteeID), user);
        query.include("tutorID.user");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "no requests to show");
                    } else {
                        for(AVObject avObject : list) {
                            Request request = (Request) avObject;
                            boolean isUpdated = request.isUpdatedRequestSent();
                            if (!requestList.contains(request)) {
                                requestList.add(request);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if (isUpdated) {
                                    requestList.remove(request);
                                    requestList.add(request);
                                    request.setUpdatedRequestSent(false);
                                    request.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                Log.d(TAG, "mark UpdatedRequestSent to success");
                                            } else {
                                                Log.d(TAG, "mark UpdatedRequestSent to False fail with " + e.toString());
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
}
