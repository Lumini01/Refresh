package com.example.refreshapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    TextView title;
    EditText etNameSignUp;
    EditText etPwdSignUp;
    EditText etEmailSignUp;
    EditText etPhoneSignUp;
    Button btSignUp;
    TextView btLogin;
    ImageView logo;
    UserInfo user = new UserInfo();

    HelperDB helperDB = new HelperDB(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        etNameSignUp = findViewById(R.id.etNameSignUp);
        etPwdSignUp = findViewById(R.id.etPwdSignUp);
        etEmailSignUp = findViewById(R.id.etEmailSignUp);
        etPhoneSignUp = findViewById(R.id.etPhoneSignUp);
        btSignUp = findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                user.setUserName(etNameSignUp.getText().toString());
                user.setUserPwd(etPwdSignUp.getText().toString());
                user.setUserEmail(etEmailSignUp.getText().toString());
                user.setUserPhone(etPhoneSignUp.getText().toString());

                ErrorMessage validate = ValidationHelper.validateSignUp(user, helperDB, db);

                if ( validate == null) {
                    if (helperDB.registerNewAccount(user, db)) {
                        db.close();

                        Toast toast = Toast.makeText(SignUp.this, "Signup Successful!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.show();

                        etNameSignUp.setError(null);
                        etPwdSignUp.setError(null);
                        etEmailSignUp.setError(null);
                        etPhoneSignUp.setError(null);

                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                    }
                    else {
                        Toast toast = Toast.makeText(SignUp.this, "Unexpected Signup Error.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.show();
                    }
                }
                else {
                    switch (validate.getField()) {
                        case "name":
                            etNameSignUp.setError(validate.getMessage());
                            break;
                        case "pwd":
                            etPwdSignUp.setError(validate.getMessage());
                            break;
                        case "email":
                            etEmailSignUp.setError(validate.getMessage());
                            break;
                        case "phone":
                            etPhoneSignUp.setError(validate.getMessage());
                            break;
                    }

                    Toast toast = Toast.makeText(SignUp.this, "Input Error - Signup Not Completed.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                }
            }
        });

        btLogin = findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        logo = findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Start.class);
                startActivity(intent);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}