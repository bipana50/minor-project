package com.example.dell.firebaseintro.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.firebaseintro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText loginEmailField;
    private EditText loginPasswordField;
    private Button loginButton;
    private Button createAccButton;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        loginEmailField = (EditText) findViewById(R.id.loginEmailID);
        loginPasswordField = (EditText) findViewById (R.id.loginPasswordID);
        loginButton = (Button) findViewById(R.id.loginButtonID);
        createAccButton = (Button) findViewById(R.id.createAccountButtonID);

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,CreateAccountActivity.class));

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(loginEmailField.getText().toString()) && !TextUtils.isEmpty(loginPasswordField.getText().toString())) {
                    String email = loginEmailField.getText().toString();
                    String pwd = loginPasswordField.getText().toString();

                    login(email, pwd,v);
                }
            }
        });



}

    private void login(String email, String pwd, final View view) {

        mAuth.signInWithEmailAndPassword(email, pwd).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                           mUser = mAuth.getCurrentUser();

                           Toast.makeText(MainActivity.this,"Sign-in successful !",Toast.LENGTH_SHORT).show();

                           //update UI

                           startActivity(new Intent(MainActivity.this, PostListActivity.class));
                           finish();

                        } else {

                           Snackbar.make(view,"Authentication failed !", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Toast.makeText(this, "Already signed in !!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, PostListActivity.class));
            finish();

        } else {

            Toast.makeText(this, "Not signed in !!", Toast.LENGTH_SHORT).show();

        }
    }
}
