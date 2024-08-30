package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private ActivityRegisterBinding binding;
   private FirebaseAuth auth = FirebaseAuth.getInstance();

   private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=  ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, MainActivity2.class));
                finishAffinity();

            }
        });

        binding.btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.registName.getText().toString().trim();
                String password = binding.registerPass.getText().toString();
                String email = binding.email.getText().toString().trim();
                String phone = binding.phone.getText().toString();
                String Cpassword = binding.confirmPassword.getText().toString();
                validate(name, phone,email, password, Cpassword);
            }
        });

        binding.textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Register.this , Login.class));
                finishAffinity();

            }
        });
    }

    private void validate(String fname , String phone,String  email,String pass ,String cpass){

        if(fname.isEmpty()){
            binding.registName.setError("Require");
        }else if (phone.isEmpty()){
            binding.phone.setError("Require");
        }else if (email.isEmpty()) {
            binding.email.setError("Require");
        } else if (pass.isEmpty()){
                binding.registerPass.setError("Require");
        }else if (cpass.isEmpty()) {
            binding.confirmPassword.setError("Require");
        }else if (pass.length() < 6) {
                binding.registerPass.setError("Password must be greater than 6");
        } else if (pass.equals(cpass)) {
            setAuth(fname,phone,email,pass);

        } else {
            Toast.makeText(this, "Wrong Confirm password", Toast.LENGTH_LONG).show();
        }

    }

    private void setAuth(String name,String phone,String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        saveUserData(authResult.getUser().getUid(),name,phone,email);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                });

    }
    private void  saveUserData(String id ,String name,String phone,String email){

        reference.child (id).setValue(new UserModel(id,name,email,phone))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(Register.this, "Login Success",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Register.this , MainHome.class));
                        finishAffinity();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });




    }

}