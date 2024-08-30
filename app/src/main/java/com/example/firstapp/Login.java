package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth =FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity2.class));
                finishAffinity();

            }
        });



        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=binding.emailUser.getText().toString().trim();
                String password= binding.passwordUser.getText().toString();
                validate(name,password);

            }

        });

        binding.textSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class));
                finishAffinity();
            }
        });
    }

    private void validate(String user , String  pass){

        if(user.isEmpty()){

            binding.emailUser.setError("Require");
        }else if (pass.isEmpty()) {
            binding.passwordUser.setError("Require");
        }else if (pass.length() < 6) {
            binding.passwordUser.setError("Password must be greater than 6");

        } else{
            setAuth(user,pass);
        }

    }
    private void setAuth (String user , String pass){
        binding.progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(user, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(Login.this , MainHome.class));
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

}