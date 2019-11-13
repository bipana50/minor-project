package com.example.dell.firebaseintro.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.dell.firebaseintro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;
    private Button newAccButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private EditText rePassword;

    private android.support.v7.widget.Toolbar toolbar;
    private final static String TITLE = "Create Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("User");

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        firstName = (EditText) findViewById(R.id.createFirstNameID);
        lastName = (EditText) findViewById(R.id.createLastNameID);
        email = (EditText) findViewById(R.id.createEmailID);
        phoneNumber = (EditText) findViewById(R.id.createPhoneNumberID);
        password = (EditText) findViewById(R.id.createPasswordID);
        newAccButton = (Button) findViewById(R.id.createAccID);
        rePassword = (EditText) findViewById(R.id.re_password);

        newAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createNewAccount(v);
            }
        });


    }

    private  void createNewAccount(final View view)
    {

        final String name = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        final String phn = phoneNumber.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        String rePwd = rePassword.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(em) &&
                !TextUtils.isEmpty(phn) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(rePwd))
        {
            if(pwd.equals(rePwd)) {
                progressDialog.setMessage("Creating account...");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(em, pwd).
                        addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String hasBookstore ="0";

                                if (authResult != null) {
                                    String userid = mAuth.getCurrentUser().getUid();
                                    DatabaseReference currentdb = mDatabaseReference.child(userid);

                                    currentdb.child("firstName").setValue(name);
                                    currentdb.child("lastName").setValue(lname);
                                    currentdb.child("phoneNumber").setValue(phn);
                                    currentdb.child("hasBookstore").setValue(hasBookstore);

                                    //TODO : Add user image

                                    currentdb.child("image").setValue("N/A");

                                    progressDialog.dismiss();

                                    Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(intent);
                                }

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(view,e.getMessage() ,Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {

                Snackbar.make(view,"Passwords do not match" ,Snackbar.LENGTH_LONG).show();
            }


        } else {

            Snackbar.make(view,"Enter all fields !", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {

            case android.R.id.home:
                CreateAccountActivity.this.onBackPressed();
                break;

        }
        return true;
    }
}
