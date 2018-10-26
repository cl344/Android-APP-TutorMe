package compsci290.edu.duke.tutor.loginregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;

import compsci290.edu.duke.tutor.R;

public class ChangePwdActivity extends AppCompatActivity {

    private EditText email;
    private Button reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        email = (EditText) findViewById(R.id.pwdResetText);
        reset = (Button) findViewById(R.id.resetPwd);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                AVUser.requestPasswordResetInBackground(email.getText().toString(), new RequestPasswordResetCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {

                            Intent LoginIntent = new Intent(getApplicationContext(),LoginActivity.class);
                            getApplicationContext().startActivity(LoginIntent);

                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

    }
}
