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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.RequestEmailVerifyCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import compsci290.edu.duke.tutor.R;


/**
 * RegisterActivity:
 * This Activity takes user info from UI and updates online databse to add new user
 * verifies user email or phone
 */

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private String uid;
    private EditText uemail;
    private EditText firstName;
    private EditText lastName;
    private EditText pwd;
    private EditText pwds;
    private EditText uname;
    private EditText uBio;
    private TextView emailVerify;
    private Button btnregister;
    private Button emailVerifyAgain;
    private AVUser user = new AVUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        uemail = (EditText) findViewById(R.id.uemail);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        uBio = (EditText) findViewById(R.id.uBio);
        pwd = (EditText) findViewById(R.id.pwd);
        pwds = (EditText) findViewById(R.id.pwds);
        uname = (EditText) findViewById(R.id.uname);
        emailVerify = (TextView) findViewById(R.id.emailverify);
        btnregister = (Button) findViewById(R.id.btnregister);
        emailVerifyAgain = (Button) findViewById(R.id.emailVerifyAgain) ;

        /**
         * this method stores user info to server
         * checks password correct
         * request email/phone verification
         */
        btnregister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){

                if(!pwd.getText().toString().equals(pwds.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Passwords Don't Match",Toast.LENGTH_SHORT).show();
                }
                else {

                    user = new AVUser();// Build new user object
                    user.setUsername(uname.getText().toString());// Set username
                    user.setPassword(pwd.getText().toString());// Set userpwd
                    user.setEmail(uemail.getText().toString());// Set useremail
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                // user info update successful & email verification sent
                                uid = AVUser.getCurrentUser().getObjectId();
                                emailVerify.setVisibility(View.VISIBLE);//set verifying text visible
                                emailVerifyAgain.setVisibility(View.VISIBLE);// set verification resend button visible

                                emailcheck(user); //loop to wait for email verification


                            } else {
                                // user info update fail
                                Toast.makeText(getApplicationContext(), "registeration failed send email", Toast.LENGTH_SHORT).show();
                                Log.i("registerLeancloud", "fail");
                            }
                        }
                    });

                }

            }

        });


        /**
         * this method resend email verification
         */
        emailVerifyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                emailVerifyAgain.setVisibility(View.INVISIBLE);
                emailVerify.setVisibility(View.VISIBLE);
                AVUser.requestEmailVerifyInBackground(uemail.getText().toString(), new RequestEmailVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            // verification success
                            emailcheck(user);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Email Resend Failed", Toast.LENGTH_SHORT).show();
                        }
                    }


                });

            }
        });


    }


    /**
     * this method loop to wait for email verification
     */
    public void emailcheck(final AVUser avUser){

        //check email verified status on server
        AVQuery query = new AVQuery("_User");
        query.getInBackground(uid, new GetCallback() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if(e==null){
                    if(avObject.getBoolean("emailVerified")){
                        //if email verified
                        emailVerifyAgain.setVisibility(View.INVISIBLE);// set verification resend button visible
                        emailVerify.setVisibility(View.INVISIBLE); //set verifying text invisible

                        AVObject newUserInfo = new AVObject("UserInfo");
                        newUserInfo.put("uid", avUser);
                        newUserInfo.put("username", avUser.getUsername());
                        newUserInfo.put("firstName", firstName.getText());
                        newUserInfo.put("lastName", lastName.getText());
                        newUserInfo.put("bio", uBio.getText());

                        newUserInfo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Log.d(TAG, "save new user info success");
                                } else {
                                    Log.d(TAG, "save new user info fail with " + e.toString());
                                }
                            }
                        });
                        //go to IdAcrivity
                        Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        RegisterActivity.this.startActivity(LoginIntent);
                    }
                    else{
                        emailcheck(avUser); // continue loop to check email verification status
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Click Resend Email Verification", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}
