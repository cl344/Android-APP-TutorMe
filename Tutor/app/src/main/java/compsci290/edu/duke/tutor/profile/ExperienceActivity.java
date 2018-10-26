package compsci290.edu.duke.tutor.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.model.Course;
import compsci290.edu.duke.tutor.model.CourseTaken;


public class ExperienceActivity extends AppCompatActivity {

    private final String WARNING = "Please enter correct class and code";
    private final String TAG = "ExperienceActivity";
    protected Toolbar mActionBarToolbar;
    private RecyclerView mRecyclerView;
    private CourseTakenAdapter mCourseTakenAdapter;
    protected List<CourseTakenDetail> mCoursesTaken = new ArrayList<>();
    private ImageView mAddCourseTaken;
    private LoginType mMode;
    private String mTutorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_course_taken);

        Bundle b = getIntent().getExtras();
        mTutorID = b.getString("userInfoId");
        loadTutorExperience();

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Background");

        mMode = (LoginType) getIntent().getSerializableExtra("mode");

        if (mMode == LoginType.TUTOR) {
            mAddCourseTaken = (ImageView) findViewById(R.id.course_taken_main_add);
            mAddCourseTaken.setVisibility(View.VISIBLE);
            mAddCourseTaken.setOnClickListener(new ProfileAddExperienceOnClickListener());
        }

        mCourseTakenAdapter = new CourseTakenAdapter(this, mCoursesTaken);
        mRecyclerView = (RecyclerView) findViewById(R.id.course_taken_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mCourseTakenAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                // move item in `fromPos` to `toPos` in adapter.
                return true;// true if moved, false otherwise
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                if (swipeDir == 1) {
                    int pos = viewHolder.getAdapterPosition();
                    mCoursesTaken.remove(pos);
                    mCourseTakenAdapter.notifyDataSetChanged();
                    //TODO: update database
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    // load tutor experience based on UserInfo objectId from table CourseTaken
    public void loadTutorExperience() {
        if (mTutorID == null) {
            Log.d(TAG, "mTutorID is not assigned");
            throw new NullPointerException();
        }
        Log.d(TAG, "loading Tutor Experience");

        AVObject tutor = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), mTutorID);
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_course_taken));
        query.whereEqualTo(getResources().getString(R.string.database_table_course_taken_uid), tutor);
        query.include(getResources().getString(R.string.database_table_course_taken_courseid));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null) {
                        for(AVObject object : list) {

                            String semester = object.getString(getResources().getString(R.string.database_table_course_taken_semester));
                            String isTA  = Boolean.toString(object.getBoolean(getResources().getString(R.string.database_table_course_taken_isTA)));
                            String grade = object.getString(getResources().getString(R.string.database_table_course_taken_grade));
                            String name = object.getAVObject(getResources().getString(R.string.database_table_course_taken_courseid)).getString(getResources().getString(R.string.database_table_course_code));

                            addCourse(grade, name, semester, isTA, true);
                            Log.d(TAG, "add course " + name);
                        }
                    }
                } else {
                    Log.d(TAG, "fetching experience error " + e.toString());
                }
            }
        });

    }

    class ProfileAddExperienceOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean wrapInScrollView = true;

            MaterialDialog.Builder dialog = new MaterialDialog.Builder(ExperienceActivity.this)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            View view = dialog.getCustomView();
                            final String userInputGrade = ((EditText) view.findViewById(R.id.txt_your_grade)).getText().toString();
                            final String userInputCourseSubject = ((EditText) view.findViewById(R.id.txt_your_class)).getText().toString() +
                                    ((EditText) view.findViewById(R.id.txt_your_class_code)).getText().toString();
                            final String userInputSemester = ((EditText) view.findViewById(R.id.txt_your_class_semester)).getText().toString();
                            final String userInputIsTA = ((EditText) view.findViewById(R.id.txt_your_TA)).getText().toString();

                            addCourse(userInputGrade, userInputCourseSubject, userInputSemester, userInputIsTA, false);

                            Log.d(TAG, "User took "+ userInputCourseSubject);
                            Toast.makeText(ExperienceActivity.this, "User took "+ userInputCourseSubject, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .title("Add CourseGroup")
                    .customView(R.layout.dialog_edit_course_taken, wrapInScrollView)
                    .negativeText("Cancel")
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(ExperienceActivity.this, "User cancel", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .positiveText("Submit");
            dialog.show();
        }
    }

    /*
    * display newly added course if course existed in the database
    * save new course to server
    * */
    public void addCourse(final String grade, final String className, final String semester, final String isTA, boolean ifFetched) {

        if (ifFetched) {
            // loaded info, render to recyclerview directly
            CourseTakenDetail courseTakenDetail = new CourseTakenDetail(className, semester, isTA, grade);
            mCoursesTaken.add(courseTakenDetail);
            mCourseTakenAdapter.notifyDataSetChanged();
        } else {
            AVQuery<AVObject> course = new AVQuery<>("Course");
            course.whereEqualTo("code", className);
            course.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        if (list == null || list.size() == 0) {
                            Toast.makeText(ExperienceActivity.this, WARNING, Toast.LENGTH_LONG).show();
                        } else {
                            // update recyclerview
                            CourseTakenDetail courseTakenDetail = new CourseTakenDetail(className, semester, isTA, grade);
                            mCoursesTaken.add(courseTakenDetail);
                            mCourseTakenAdapter.notifyDataSetChanged();

                            // update course avaiable tutors
                            Course thisCourse = (Course) list.get(0);
                            thisCourse.updateAvailableTutors(mTutorID);
                            thisCourse.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Log.d(TAG, "save course new tutor succcess");
                                    } else {
                                        Log.d(TAG, "save course new tutor fail " + e.toString());
                                    }
                                }
                            });

                            // save experience to remote server
                            saveExperience(thisCourse, courseTakenDetail);
                        }
                    } else {
                        Log.d(TAG, "fetch course error + " + e.toString());
                    }
                }
            });
        }
    }

    private void saveExperience(Course course, CourseTakenDetail courseTakenDetail) {
        CourseTaken courseTaken = new CourseTaken(course, courseTakenDetail, mTutorID);
        courseTaken.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d(TAG, "save courseTaken success");
                } else {
                    Log.d(TAG, "save courseTaken fails with " + e.toString());
                }
            }
        });
    }
}
