package compsci290.edu.duke.tutor.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.Review;


public class ProfileReviewActivity extends AppCompatActivity {

    private final String TAG = "ProfileReviewActivity";
    protected Toolbar mActionBarToolbar;
    private RecyclerView mRecyclerView;
    private ProfileReviewAdapter mProfileReviewAdapter;
    protected List<ReviewViewModel> mReviewViewModelList = new ArrayList<>();
    private ImageView mAddCourseTaken;
    private LoginType mMode;
    protected String mTutorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_review);

        Bundle b = getIntent().getExtras();
        mTutorID = b.getString("userInfoId");
        loadTutorReview();

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Review");

        mMode = (LoginType) getIntent().getSerializableExtra("mode");

        if (mMode != LoginType.TUTOR) {
            mAddCourseTaken = (ImageView) findViewById(R.id.profile_review_layout_add);
            mAddCourseTaken.setVisibility(View.VISIBLE);
            mAddCourseTaken.setOnClickListener(new ProfileAddReviewOnClickListener());
        }

        mProfileReviewAdapter = new ProfileReviewAdapter(this, mReviewViewModelList);
        mRecyclerView = (RecyclerView) findViewById(R.id.profile_review_layout_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mProfileReviewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void loadTutorReview() {
        if (mTutorID == null) {
            Log.d(TAG, "mTutorID is not assigned");
            throw new NullPointerException();
        }
        Log.d(TAG, "loading Tutor Experience");

        AVObject tutor = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), mTutorID);
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_review));
        query.whereEqualTo(getResources().getString(R.string.database_table_review_revieweeid), tutor);
        query.include(getResources().getString(R.string.database_table_review_reviewerid));
        query.include(getResources().getString(R.string.database_table_review_revieweeid));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "review list size is zero");
                        Toast.makeText(ProfileReviewActivity.this, "This tutor currently has no experience to show. Sorry.", Toast.LENGTH_SHORT).show();
                    } else {
                        for(AVObject object : list) {
                            addReviewViewModel(object);
                        }
                    }
                } else {
                    Log.d(TAG, "load tutor experience fail " + e.toString());
                }
            }
        });
    }

    /*
    * create ReviewViewModel based on return object from server
    * */
    public void addReviewViewModel(AVObject avObject) {

        String reviewerName = avObject.getAVObject(getResources().getString(R.string.database_table_review_reviewerid)).getString(getResources().getString(R.string.database_table_user_username));
        String revieweeName = avObject.getAVObject(getResources().getString(R.string.database_table_review_revieweeid)).getString(getResources().getString(R.string.database_table_user_username));
        String comment = avObject.getString(getResources().getString(R.string.database_table_review_review));
        String reviewerId = avObject.getAVObject(getResources().getString(R.string.database_table_review_reviewerid)).getObjectId();
        String revieweeId = avObject.getAVObject(getResources().getString(R.string.database_table_review_revieweeid)).getObjectId();

        ReviewViewModel reviewViewModel = new ReviewViewModel(comment, reviewerId, revieweeId);
        reviewViewModel.setRevieweeName(revieweeName);
        reviewViewModel.setReviewerName(reviewerName);
        mReviewViewModelList.add(reviewViewModel);
        mProfileReviewAdapter.notifyDataSetChanged();
        Log.d(TAG, "add review " + comment);
    }

    /*
    * create reviewViewModel and display on local
    * save review to remote server
    * */
    public void addReview(final String review) {
        Review newReview = new Review(review, AVUser.getCurrentUser().getObjectId(), mTutorID);
        ReviewViewModel reviewViewModel = new ReviewViewModel(newReview);
        reviewViewModel.setReviewerName((String) AVUser.getCurrentUser().get(getResources().getString(R.string.database_table_user_username)));
        mReviewViewModelList.add(reviewViewModel);
        mProfileReviewAdapter.notifyDataSetChanged();

        // load current userInfo
        AVObject currentUser = AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId());
        AVQuery<AVObject> currentUserInfo = new AVQuery<>("UserInfo");
        currentUserInfo.whereEqualTo("uid", currentUser);
        currentUserInfo.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        throw new NullPointerException();
                    } else {

                        // save review to remote database
                        // Review needs reviewerId(AVObject), revieweeId(AVObject) and review(String).
                        AVObject currentUserInfoReturn = list.get(0);
                        AVObject reviewer = AVObject.createWithoutData(
                                getResources().getString(R.string.database_table_userinfo),
                                currentUserInfoReturn.getObjectId());
                        AVObject reviewee = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), mTutorID);
                        AVObject object = new AVObject(getResources().getString(R.string.database_table_review));
                        object.put(getResources().getString(R.string.database_table_review_review),review);
                        object.put(getResources().getString(R.string.database_table_review_reviewerid), reviewer);
                        object.put(getResources().getString(R.string.database_table_review_revieweeid), reviewee);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Log.d(TAG, "save review successfully");
                                } else {
                                    Log.d(TAG, "saving went wrong + " + e.toString());

                                }

                            }
                        });
                    }
                } else {
                    Log.d(TAG, "fetch userInfo went wrong");
                }
            }
        });
    }


    class ProfileAddReviewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean wrapInScrollView = true;

            // display dialog to prompt user enter review for this tutor
            new MaterialDialog.Builder(ProfileReviewActivity.this)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            View view = dialog.getCustomView();

                            final String userInputReview = ((EditText) view.findViewById(R.id.profile_review_add_user_input)).getText().toString();

                            addReview(userInputReview);

                            Toast.makeText(ProfileReviewActivity.this, "User add review", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .title("Add Review")
                    .customView(R.layout.profile_add_review, wrapInScrollView)
                    .negativeText("Cancel")
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(ProfileReviewActivity.this, "User cancel", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .positiveText("Submit")
                    .show();
        }
    }
}
