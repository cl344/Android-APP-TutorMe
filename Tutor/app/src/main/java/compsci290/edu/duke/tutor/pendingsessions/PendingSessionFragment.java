package compsci290.edu.duke.tutor.pendingsessions;


import android.app.DownloadManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.avos.avoscloud.GetCallback;

import java.util.ArrayList;
import java.util.List;

import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Session;

public class PendingSessionFragment extends Fragment {

    private final String TAG = "PendingSessionFragment";
    private RecyclerView mRecyclerView;
    private PendingSessionAdapter mAdapter;
    public List<Session> pendingSessionList = new ArrayList<>();
    private LoginType mMode;
    private String mUserInfoId;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_session, container, false);
        rootView.setTag(TAG);

        mMode = (LoginType) getArguments().getSerializable("mode");
        mUserInfoId = getArguments().getString("userInfoId");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.pending_session_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pending_session_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSessions();
            }
        });


        mAdapter = new PendingSessionAdapter(getContext(), pendingSessionList, mMode);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadSessions();

        return rootView;

    }

    /*
    * load tutor based on tutorInfoId
    * */
    private void loadSessions() {
        AVObject userInfo = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo),mUserInfoId);
        AVQuery<AVObject> newQuery = new AVQuery<>("Tutor");
        newQuery.whereEqualTo("user", userInfo);
        newQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "incorrect list size");
                    } else {
                        AVObject tutor = list.get(0);
                        loadSession(tutor);
                    }
                } else {
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    /*
    * load session based on tutor info
    * */
    private void loadSession(AVObject tutor) {
        AVQuery<AVObject> query = new AVQuery<>("Session");
        query.whereEqualTo("tutorID", tutor);
        query.include(getResources().getString(R.string.database_table_session_courseID));              // include additional information for column courseID
        query.include("request");               // include additional information for column request
        query.include("tutorID");               // include additional information for column tutorID
        query.include("tutorID.user");          // include additional information for column tutorID.user
        query.include("tuteeID");               // include additional information for column tuteeID
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "session list is empty");
                    } else {
                        for (AVObject avObject : list) {
                            Log.d(TAG, "hah" + avObject.getAVObject("tutorID").getAVObject("user").getObjectId());
                            Session session = (Session) avObject;
                            if (!pendingSessionList.contains(session)) {
                                pendingSessionList.add(session);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                // Repeated Session so do nothing
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "fetch sessions fail with exception " + e.toString());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
