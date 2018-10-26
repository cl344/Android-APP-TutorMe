package compsci290.edu.duke.tutor.loginregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

import compsci290.edu.duke.tutor.activities.MainActivity;
import compsci290.edu.duke.tutor.R;

/**
 * IdActivity:
 * This Activity Redirect user to login as either tutor or tutee
 *
 * @author  Mitchell Berger, Cheng Lyu, Jia Zeng, Linda Zhou
 * @version 1.0
 * @since   2017-04-15
 */
public class IdActivity extends AppCompatActivity {

    private final String TAG = "IdActivity";
    protected Button btntutor;
    protected Button btntutee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        btntutor = (Button) findViewById(R.id.loginTuter);
        btntutee = (Button) findViewById(R.id.loginTutee);

        //Reuse MainActivity Class with different mode and fragments
        //login as tutor
        btntutor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent idIntent = new Intent(IdActivity.this, MainActivity.class);
                LoginType.TUTOR.attachTo(idIntent);
                IdActivity.this.startActivity(idIntent);
                loadInfo();
            }

        });

        //login as tutee
        btntutee.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent idIntent = new Intent(IdActivity.this, MainActivity.class);
                LoginType.TUTEE.attachTo(idIntent);
                IdActivity.this.startActivity(idIntent);
            }

        });

    }

    // Load current user info
    private void loadInfo() {

        AVQuery<AVObject> query = new AVQuery<>("UserInfo");
        query.whereEqualTo("uid", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() == 0) {
                        Log.d(TAG, "no userInfo for this user");
                    } else {
                        AVObject avObject = list.get(0);
                        loadTutor(avObject);
                    }
                } else {
                    Log.d(TAG, "fail to fetch user info + " + e.toString());
                }
            }
        });
    }

    /*
    * check if current user has logged in as tutor (if first time, add him to Tutor)
    * */
    private void loadTutor(final AVObject userInfo) {
        AVQuery<AVObject> query = new AVQuery<>("Tutor");
        query.whereEqualTo("user", userInfo);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                    if (list == null || list.size() == 0) {
                        // User is first time login as Tutor
                        Log.d(TAG, "no Tutor for this user");
                        AVObject newTutor = new AVObject("Tutor");
                        newTutor.put("user", userInfo);                 // set userinfo
                        newTutor.put("ratingAsTutor", 0);               // set initial rating
                        newTutor.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Log.d(TAG, "save new tutor success");
                                } else {
                                    Log.d(TAG, "save nwe tutor fail with " + e.toString());
                                }
                            }
                        });
                    } else {
                        // User has registered as Tutor. Do nothing
                    }
                } else {
                    Log.d(TAG, "fail to fetch tutor given userInfo + " + e.toString());
                }
            }
        });
    }
}
