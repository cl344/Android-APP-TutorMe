package compsci290.edu.duke.tutor.mainfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compsci290.edu.duke.tutor.adapters.ClassAdapter;
import compsci290.edu.duke.tutor.adapters.ClassSource;
import compsci290.edu.duke.tutor.R;


public class CoursesFragment extends Fragment {

    private List<ClassSource> classList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ClassAdapter mAdapter;
    private static final String TAG = "CoursesFragment";
    private DividerItemDecoration mDivider;
    private Context mContext;

    private String course;
    private HashMap<String,Integer> CourseList= new HashMap<>();

    private JSONArray jarray;

    public CoursesFragment() {
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

    //Set adapter and recycler view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));

        mAdapter = new ClassAdapter(classList, mContext);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        prepareCourseData();

        return rootView;

    }

    /*
    * Get course data from database
    * */
    private void prepareCourseData() {

        //
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_course));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if(classList.isEmpty()){
                        for (AVObject avObject : list) {
                            jarray = avObject.getJSONArray(getResources().getString(R.string.database_table_course_availableTutors));
                            course = avObject.getString(getResources().getString(R.string.database_table_course_code));
                            if(jarray != null && jarray.length() > 0) {
                                ClassSource courser = new ClassSource(course,
                                        "Available Tutors: " + jarray.length(),
                                        jarray,
                                        avObject.getObjectId());
                                classList.add(courser);
                                mAdapter.notifyDataSetChanged();
                            }
                            }
                        }
                    }
                } else {
                    Log.d(TAG,"fetching course fails " + e.toString());
                }
            }
        });
    }

    //Turn JSONarray into String array
    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }
}