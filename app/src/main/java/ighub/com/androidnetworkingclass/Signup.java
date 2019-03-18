package ighub.com.androidnetworkingclass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    private EditText etFirstname, etLastname, etPhone, etEmail, etPassword;
    private String fname, lname, email, password, phone_no;
    private Button btnSignup;
    private KProgressHUD hud;
    private String status, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        AndroidNetworking.initialize(getApplicationContext());

        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Signup.this, "Signup button clicked", Toast.LENGTH_SHORT).show();
                validate();
            }
        });
    }

    private void validate() {
        fname = etFirstname.getText().toString().trim();
        lname = etLastname.getText().toString().trim();
        phone_no = etPhone.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (iscontainit(fname) == 0
                || iscontainit(email) == 0
                || iscontainit(password) == 0
                || iscontainit(lname) == 0
                || iscontainit(phone_no) == 0
                ) {

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(fname)){
            etFirstname.setError("please enter your first name");
            return;
        } if (TextUtils.isEmpty(lname)){
            etLastname.setError("enter your last name");
            return;
        } if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("your password cannot be less than 6");
            return;
        } if (TextUtils.isEmpty(phone_no)){
            etPhone.setError("invalid phone number");
            return;
        } if (TextUtils.isEmpty(email)){
            etEmail.setError("enter your email");
        } else if (isNetworkAvaliable()){
            send_request(fname, lname, email, password, phone_no);
        } else {
            Toast.makeText(this, "network is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void send_request(String fname, String lname, String email, String password, String phone_no) {
                hud.create(Signup.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Registering..." +" " + lname + " " + fname)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        AndroidNetworking.post("https://logios.herokuapp.com/api/register")
                .addBodyParameter("lname", lname)
                .addBodyParameter("fname", fname)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("phone_no", phone_no)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            status = response.getString("status");
                            message = response.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("signup", status);
                        Log.d("signup2", message);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private boolean isNetworkAvaliable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public int iscontainit(String edittext) {
        int number;
        edittext = edittext.trim();
        if (edittext.equals(" ")
                || edittext.length() == 0
                || edittext.equals("")
                || TextUtils.isEmpty(edittext)) {
            number = 0;
        } else {
            number = 1;
        }

        return number;
    }

}
