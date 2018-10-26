package compsci290.edu.duke.tutor.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.Date;
import java.util.List;

import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import compsci290.edu.duke.tutor.chat.CustomUserProvider;
import compsci290.edu.duke.tutor.loginregistration.LoginType;
import compsci290.edu.duke.tutor.R;
import compsci290.edu.duke.tutor.activities.VenmoEnterInfoActivity;
import compsci290.edu.duke.tutor.model.Request;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private final String TAG = "Profile Activity";
    protected View requestButtons;          // three buttons view
    private TextView userName;              // username on profile page
    protected CircleImageView mProfilePic;        // profile picture
    protected Button mRequestTutor;         // request tutor button
    protected Button mSendMessage;          // send message button
    protected Button mVenmo;                // send venmo button
    protected TextView mBackgroundTextView; // entry point for profile experience
    protected TextView mReviewTextView;     // entry point for profile review
    protected String mCourseId;             // course id to send tutor request
    protected String mTutorId;              // TutorId for the current profile
    protected String mTutorInfoId;          // UserInfoId for the current profile
    protected String mTutorName;            // name for the tutor
    protected String currentUserInfoId;     // UserInfoId for the current user
    protected Date mCreatedAt;
    private AVFile mAVFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_v2);

        Bundle bundle = getIntent().getExtras();
        mTutorInfoId = bundle.getString("tutorID");
        mCourseId = bundle.getString("courseID");

        loadTutor(mTutorInfoId);

        userName = (TextView) findViewById(R.id.user_profile_name_v2);
        mProfilePic = (CircleImageView) findViewById(R.id.profile_image);

        mRequestTutor = (Button) findViewById(R.id.button_request_tutor);
        mSendMessage = (Button) findViewById(R.id.button_send_message);
        requestButtons = findViewById(R.id.profile_header_request_message_view);
        requestButtons.setVisibility(View.VISIBLE);

        mVenmo = (Button) findViewById(R.id.button_send_venmo);

        mBackgroundTextView = (TextView) findViewById(R.id.profile_info_background);
        mReviewTextView = (TextView) findViewById(R.id.profile_info_review);

        mBackgroundTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: put current userID into Intent and start activity

                Intent intent = new Intent(ProfileActivity.this, ExperienceActivity.class);
                Bundle b = new Bundle();
                b.putString("userInfoId", mTutorInfoId);
                intent.putExtra("mode", LoginType.TUTEE);
                intent.putExtras(b);
                startActivity(intent);

            }
        });
        mReviewTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: put current userID into Intent and start activity
                Intent intent = new Intent(ProfileActivity.this, ProfileReviewActivity.class);
                Bundle b = new Bundle();
                b.putString("userInfoId", mTutorInfoId);
                intent.putExtra("mode", LoginType.TUTEE);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    /* when request tutor button is clicked, show request dialog*/
    class ProfileRequestTutorOnClickListener implements View.OnClickListener {

        private String TAG = "Tutor_Request_Form";

        public Context mContext;

        //TODO: handle potential exception
        public ProfileRequestTutorOnClickListener(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onClick(View v) {

            new MaterialDialog.Builder(mContext)
                    .negativeText("cancel")
                    .positiveText("submit request")
                    .title("Tutor Request")
                    .customView(R.layout.tutor_request, true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            View view = dialog.getCustomView();
                            try {
                                final int userInputDuration = Integer.parseInt(((EditText) view
                                        .findViewById(R.id.tutor_request_session_duration_input))
                                        .getText().toString());
                                final String userInputComment = ((EditText) view
                                        .findViewById(R.id.tutor_request_additional_comment_input))
                                        .getText()
                                        .toString();

                                // Create request from user input
                                Request request = new Request(
                                        mCourseId,
                                        mTutorId,
                                        currentUserInfoId,
                                        userInputComment,
                                        userInputDuration,
                                        mCreatedAt
                                );

                                // set read write access
                                AVACL acl = new AVACL();
                                acl.setPublicReadAccess(true);
                                acl.setPublicWriteAccess(true);
                                request.setACL(acl);

                                //send the request form to server in background
                                request.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Log.d(TAG, "save requestForm success");
                                        } else {
                                            Log.d(TAG, "save requestForm fail with " + e.toString());
                                        }
                                    }
                                });
                                Log.d(TAG, "Tutor Request Form built");

                            } catch (NullPointerException exception) {
                                Log.d(TAG, "view id not found");
                            }

                        }
                    })
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(mContext, "User cancel", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    /**
     * Load tutor info from table Tutor through UserInfo objectID
     * Handle in background thread
     * onSuccess call callback function onTutorLoadingReady
     * otherwise log exception
     * */
    private void loadTutor(String id) {

        final String test = id;
        AVObject userInfo = AVObject.createWithoutData(getResources().getString(R.string.database_table_userinfo), id);

        AVQuery<AVObject> newQuery = new AVQuery<>(getResources().getString(R.string.database_table_tutor));
        newQuery.whereEqualTo(getResources().getString(R.string.database_table_tutor_user), userInfo);
        newQuery.include(getResources().getString(R.string.database_table_tutor_user));
        newQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 1 || list == null || list.size() == 0) {
                        Log.d(TAG, "Incorrect tutor list size. with id " + test);
                    } else {
                        AVObject tutor = list.get(0);
                        onTutorLoadingReady(tutor);
                    }

                } else {
                    Log.d(TAG, "load Tutor info failed");
                }
            }
        });
    }

    /*
    * call loadCurrentUserInfo
    * render page with corresponding information
    * set onclicklisteners
    * */
    private void onTutorLoadingReady(AVObject avObject) {
        loadCurrentUserInfo();

        // combine user's first name and last name
        String firstName = avObject.getAVObject(getResources().getString(R.string.database_table_tutor_user)).getString(getResources().getString(R.string.database_table_userinfo_firstname));
        String lastName = avObject.getAVObject(getResources().getString(R.string.database_table_tutor_user)).getString(getResources().getString(R.string.database_table_userinfo_lastname));
        userName.setText(firstName + " " + lastName);
        mTutorName = avObject.getAVObject(getResources().getString(R.string.database_table_tutor_user)).getString(getResources().getString(R.string.database_table_user_username));
        mTutorId = avObject.getObjectId();
        mAVFile = (AVFile) avObject.getAVObject("user").get("picture");
        if (mAVFile != null) {
            mAVFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mProfilePic.setImageBitmap(Bitmap.createBitmap(bmp));
                }
            });
        }

        //open chat activity
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String avatarUrl;
                if (mAVFile != null) {
                    avatarUrl = mAVFile.getUrl();
                } else {
                    avatarUrl = "http://www.avatarsdb.com/avatars/tom_and_jerry2.jpg";
                }
                CustomUserProvider customUserProvider = CustomUserProvider.getInstance();
                customUserProvider.addChatUser(mTutorInfoId,
                        mTutorName, avatarUrl);

                //Intent intent = new Intent(ProfileActivity.this, LCIMConversationActivity.class);
                Intent intent = new Intent(ProfileActivity.this, LCIMConversationActivity.class);
                intent.putExtra(LCIMConstants.PEER_ID,mTutorInfoId);
                startActivity(intent);
            }
        });

        //open Venmo page
        mVenmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, VenmoEnterInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    /* load current user info through current user objectId and set request form listener */
    private void loadCurrentUserInfo() {
        AVObject user = AVObject.createWithoutData(getResources().getString(R.string.database_table_user), AVUser.getCurrentUser().getObjectId());
        AVQuery<AVObject> query = new AVQuery<>(getResources().getString(R.string.database_table_userinfo));
        query.whereEqualTo(getResources().getString(R.string.database_table_course_taken_uid), user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() > 1) {
                        Log.d(TAG, "list size incorrect in loading current user info");
                    } else {
                        AVObject avObject = list.get(0);
                        currentUserInfoId = avObject.getObjectId();
                        mRequestTutor.setOnClickListener(new ProfileRequestTutorOnClickListener(ProfileActivity.this));
                    }
                } else {
                    Log.d(TAG, "fetching current userinfo error " + e.toString());
                }
            }
        });
    }
}
