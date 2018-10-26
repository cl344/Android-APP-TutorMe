package compsci290.edu.duke.tutor.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import compsci290.edu.duke.tutor.R;

//fill out a form to generate a button which takes you to Venmo
//you can open the Venmo app or log in on the browser
public class VenmoEnterInfoActivity extends AppCompatActivity{
    private EditText recip, amt, nt;
    private Button b1;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //allow users to input recipient, amount, note
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venmo_enter_info);
        recip = (EditText) findViewById(R.id.recipient);
        amt = (EditText) findViewById(R.id.amount);
        nt = (EditText) findViewById(R.id.note);
        b1 =(Button)findViewById(R.id.button);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay with Venmo");


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recip.getText().toString();
                String amount = amt.getText().toString();
                String note = nt.getText().toString();

                //Venmo Payment Links API
                //documentation: https://developer.venmo.com/paymentlinks
                String paymentlink = "https://venmo.com/?txn=pay&audience=friends";
                paymentlink+="&recipients=";
                paymentlink+=recipient;
                paymentlink+="&amount=";
                paymentlink+=amount;
                paymentlink+="&note=";
                paymentlink+=note;

                //opens a browser; user can then choose to open the app or log in
                Uri uri = Uri.parse(paymentlink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
    //Override back animation
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in,
                R.anim.trans_right_out);
    }

    //back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.trans_right_in,
                    R.anim.trans_right_out);
            return true;
        }
        return false;
    }

}
