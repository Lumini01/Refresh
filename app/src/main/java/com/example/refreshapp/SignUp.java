package com.example.refreshapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    TextView title;
    EditText etNameSignUp;
    EditText etPwdSignUp;
    EditText etEmailSignUp;
    EditText etTelSignUp;
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
        etTelSignUp = findViewById(R.id.etPhoneSignUp);
        btSignUp = findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                user.setUserName(etNameSignUp.getText().toString());
                user.setUserPwd(etPwdSignUp.getText().toString());
                user.setUserEmail(etEmailSignUp.getText().toString());
                user.setUserPhone(etTelSignUp.getText().toString());

                ContentValues cv = new ContentValues();

                cv.put(helperDB.USER_NAME, user.getUserName());
                cv.put(helperDB.USER_PWD, user.getUserPwd());
                cv.put(helperDB.USER_EMAIL, user.getUserEmail());
                cv.put(helperDB.USER_PHONE, user.getUserPhone());

                db = helperDB.getWritableDatabase();
                db.insert(helperDB.USERS_TABLE, null, cv);
                db.close();

                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
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