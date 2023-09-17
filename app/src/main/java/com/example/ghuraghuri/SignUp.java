package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    TextView sign_in;
    EditText full_name,email,pass;
    Button btn;
    TextInputLayout email_input,pass_input;
    ProgressBar progressBar;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_in=findViewById(R.id.or_sign_in);
        full_name=findViewById(R.id.f_name);
        email=findViewById(R.id.email_sign_up);
        pass=findViewById(R.id.pass_sign_up);
        btn=findViewById(R.id.sign_up_btn);
        email_input=findViewById(R.id.email_input_signup);
        pass_input=findViewById(R.id.pass_input_signup);
        progressBar=findViewById(R.id.pr_sign_up);

        auth=FirebaseAuth.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,SignIn.class);
                startActivity(intent);
            }
        });

        full_name.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        pass.addTextChangedListener(watcher);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                UserRegistration();
            }
        });


    }

    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name, password,em;
            name=full_name.getText().toString().trim();
            em=email.getText().toString().trim();
            password=pass.getText().toString().trim();

            btn.setEnabled(!name.isEmpty() && !password.isEmpty() && !em.isEmpty());

            switch (Constant.ch)
            {
                case 'e':
                    email_input.setError(null);
                    break;
                case 'p':
                    pass_input.setError(null);
                    break;
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    void UserRegistration()
    {
        String password,em;
        //name=full_name.getText().toString().trim();
        em=email.getText().toString().trim();
        password=pass.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(em).matches())
        {
            progressBar.setVisibility(View.INVISIBLE);
            email_input.setError("Invalid email");
            Constant.ch='e';
        }
        else if(password.contains(" "))
        {
            progressBar.setVisibility(View.INVISIBLE);
            pass_input.setError("No space is allowed!");
            Constant.ch='p';
        }
        else if(password.length()<6)
        {
            progressBar.setVisibility(View.INVISIBLE);
            pass_input.setError("Must contain at least 6 character");
            Constant.ch='p';
        }

        else
        {
            auth.createUserWithEmailAndPassword(em,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        savedata();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignUp.this,"Sign up Successful!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SignUp.this,SignIn.class);
                        startActivity(intent);
                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                            Toast.makeText(SignUp.this,"This user has already registered!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }


    }

    void savedata()
    {
        String name=full_name.getText().toString().trim();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("User").child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        ref.child("Name").setValue(name);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}