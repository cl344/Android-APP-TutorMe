package compsci290.edu.duke.tutor.mainfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import compsci290.edu.duke.tutor.adapters.myTutorsAdapter;
import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Session;


public class TutorsFragment extends Fragment {

    private List<Session> sessionList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private myTutorsAdapter mAdapter;
    private static final String TAG = "MyTutorsFragment";
    private DividerItemDecoration mDivider;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoginType mMode;
    private String mUserInfoId;

    public TutorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mytutors, container, false);
        mMode = (LoginType) getArguments().getSerializable("mode");
        mUserInfoId = getArguments().getString("userInfoId");
        rootView.setTag(TAG);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_tutors_fragment_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSessions();
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));

        mAdapter = new myTutorsAdapter(sessionList, mContext);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        loadSessions();
        return rootView;
    }

    /*load user sessions from server using mUserInfoId for table UserInfo*/
    private void loadSessions() {

        AVObject userInfo = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), mUserInfoId);
        AVQuery<AVObject> query = new AVQuery<>("Session");
        query.whereEqualTo("tuteeID", userInfo);
        query.include(getResources().getString(R.string.database_table_session_courseID));
        query.include("request");                               // include pointer to table Request to get details
        query.include("tutorID");                               // include pointer to table Tutor to get details
        query.include("tutorID.user");                          // include pointer to table UserInfo from Tutor to get details
        query.include("tuteeID");                               // include pointer to table UserInfo to get tutee details
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "session list is empty");
                    } else {
                        for (AVObject avObject : list) {
                            Session session = (Session) avObject;
                            if (!sessionList.contains(session)) {
                                sessionList.add((Session) avObject);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "fetch sessions fail with exception " + e.toString());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}