package com.example.refresh;

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

public class Login extends AppCompatActivity {

    TextView title;
    EditText etEmailLogin;
    EditText etPwdLogin;
    Button btLogin;
    TextView btSignUp;
    ImageView logo;

    UserInfo user = new UserInfo();

    HelperDB helperDB = new HelperDB(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPwdLogin = findViewById(R.id.etPwdLogin);
        btLogin = findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //First two lines might not be needed
                //Editable etEL = etEmailLogin.getText();
                //String emailLogin = etEmailLogin.getText().toString();
                user.setUserEmail(etEmailLogin.getText().toString());

                //Editable etPL = etPwdLogin.getText();
                //String pwdLogin = etPwdLogin.getText().toString();
                user.setUserPwd(etPwdLogin.getText().toString());

                ErrorMessage validate = ValidationHelper.validateLogin(user, helperDB, db);

                if ( validate == null) {

                    Toast toast = Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();

                    etEmailLogin.setError(null);
                    etPwdLogin.setError(null);

                    db.close();

                    Intent intent = new Intent(Login.this, HomeDashboard.class);
                    startActivity(intent);
                }
                else {
                    switch (validate.getField()) {
                        case "email":
                            etEmailLogin.setError(validate.getMessage());
                            break;
                        case "pwd":
                            etPwdLogin.setError(validate.getMessage());
                            break;
                    }

                    Toast toast = Toast.makeText(Login.this, "Unable to Log In.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                }
            }
        });

        btSignUp = findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        logo = findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Start.class);
                startActivity(intent);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}