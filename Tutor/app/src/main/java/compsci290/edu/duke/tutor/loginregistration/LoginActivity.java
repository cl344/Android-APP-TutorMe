package compsci290.edu.duke.tutor.loginregistration;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import compsci290.edu.duke.tutor.chat.CustomUserProvider;
import compsci290.edu.duke.tutor.DatabaseHelper;
import compsci290.edu.duke.tutor.R;


/**
 * LoginActivity:
 * This Activity verifies user identity through protected online database.
 * stores userinfo in local database to support offline usage
 *
 * @author  Mitchell Berger, Cheng Lyu, Jia Zeng, Linda Zhou
 * @version 1.0
 * @since   2017-04-15
 */

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper localDB;
    private final String TAG = "LoginActivity";

    private final String APP_ID = "gPyzAE6Fgqgqeg4xY6sf5MR0-gzGzoHsz";
    private final String APP_KEY = "Mg9W4tqijHAxcOnTm46Idi4U";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        AVOSCloud.setDebugLogEnabled(true);
        LCChatKit.getInstance().init(getApplicationContext(), APP_ID, APP_KEY);
        AVIMClient.setAutoOpen(false);



        //setup local database
        localDB = new DatabaseHelper(this);

        final EditText uemail = (EditText) findViewById(R.id.uemail);

        final EditText uid = (EditText) findViewById(R.id.uemail);

        final EditText pwd = (EditText) findViewById(R.id.pwd);
        final Button lgn = (Button) findViewById(R.id.lgn);
        final TextView Rlink = (TextView) findViewById(R.id.Rlink);
        final TextView Flink = (TextView) findViewById(R.id.Flink);



        /**
         * This method is used to link to Register activity when button clicked
         * @param View V
         * @return Nothing
         */
        Rlink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent RegisterIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(RegisterIntent);


            }

        });

        Flink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent ChangeIntent = new Intent(LoginActivity.this,ChangePwdActivity.class);
                LoginActivity.this.startActivity(ChangeIntent);
            }
        });

        /**
         * This method is used to log user in by verifying user info through online protected database
         * @param View V
         * @return Nothing
         * @exception AVException on online verification fail
         */
        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                AVUser.logInInBackground(uid.getText().toString(), pwd.getText().toString(), new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            Log.i("LoginLeancloud1", "successful");

                            AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                            query.whereEqualTo("uid", AVUser.getCurrentUser());
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (e == null) {
                                        if (list == null || list.size() == 0) {
                                            Log.d(TAG, "Can't find current user in UserInfo table + " + e.toString());
                                        } else {
                                            AVObject avObject = list.get(0);
                                            CustomUserProvider customUserProvider = CustomUserProvider.getInstance();

                                            // Load user profile picture as message thumbnail
                                            String avatarUrl;
                                            AVFile file = (AVFile) avObject.get("picture");
                                            if (file != null) {
                                                avatarUrl = file.getUrl();
                                                Log.d("~~~~~", avObject.getAVFile("picture").getName());
                                            } else {
                                                avatarUrl = "http://www.avatarsdb.com/avatars/tom_and_jerry2.jpg";
                                            }

                                            //here we use the Object ID of UserInfo as the Message ID
                                            customUserProvider.addChatUser(avObject.getObjectId(),
                                                    avObject.getString("firstName")+" "+avObject.getString("lastName"),
                                                    avatarUrl);

                                            //open user for Message using id as userInfo objectId
                                            LCChatKit.getInstance().open(avObject.getObjectId(), new AVIMClientCallback() {
                                                @Override
                                                public void done(AVIMClient avimClient, AVIMException e) {
                                                    if (null == e) {
                                                        finish();
                                                        Intent LoginIntent = new Intent(getApplicationContext(), IdActivity.class);
                                                        LoginActivity.this.startActivity(LoginIntent);

                                                    } else {
                                                        Log.d(TAG, "fail to load AVIMClinet with " + e.toString());
                                                    }
                                                }
                                            });
                                            //Intent LoginIntent = new Intent(getApplicationContext(), TestloginActivity.class);
                                            //LoginActivity.this.startActivity(LoginIntent);
                                        }
                                    } else {
                                        Log.d(TAG, "fail to load current user's UserInfo + " + e.toString());
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "wrong user info", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }


    /**
     * This method asks if user want to go back
     * @return Nothing
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
