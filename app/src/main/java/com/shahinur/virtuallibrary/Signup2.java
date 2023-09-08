package com.shahinur.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Signup2 extends AppCompatActivity {

    EditText name,userName,email,passWord,conPassword;
    Button signupButton;
    CheckBox showPassword;
    ProgressBar progressBar;
    TextView waitText,loginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        userName =findViewById(R.id.userName);
        email = findViewById(R.id.email);
        showPassword = findViewById(R.id.showPassword);
        passWord = findViewById(R.id.passWord);
        conPassword = findViewById(R.id.conPassword);
        signupButton = findViewById(R.id.signupButton);
        waitText = findViewById(R.id.waitText);
        loginText = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked)
                {
                    //Show Password
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    conPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    //Hide Password
                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    conPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name= name.getText().toString();
                String username= userName.getText().toString();
                String Email= email.getText().toString();
                String password= passWord.getText().toString();
                String conPass= conPassword.getText().toString();

                if(Name.isEmpty())
                {
                    name.setError("Enter Your Name");
                } else if (username.isEmpty()) {
                    name.setError("Enter Your Username");
                }else if (Email.isEmpty()) {
                    email.setError("Enter Your Email");
                }else if (password.isEmpty()) {
                    passWord.setError("Set Your Password");
                }else if (conPass.isEmpty()) {
                    conPassword.setError("Confirm Your Password");
                }else if (password.length()<5) {
                    passWord.setError("Password must contain 5 characters");
                }else if (conPass.length()<5) {
                    conPassword.setError("Password must contain 5 characters");
                }
                else
                {
                    username = username.toLowerCase();
                    if(!password.equals(conPass))
                    {
                        new AlertDialog.Builder(Signup2.this)
                                .setTitle("Invalid Password")
                                .setMessage("Check your password")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                    else
                    {
                        String url="https://appservicebysuvo.000webhostapp.com/app/signup.php?nm="+Name+"&un="+username+"&em="+Email
                                +"&ps="+password;

                        progressBar.setVisibility(View.VISIBLE);
                        waitText.setVisibility(View.VISIBLE);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                if(response.contains("Success")) {
                                    progressBar.setVisibility(View.GONE);
                                    waitText.setVisibility(View.GONE);

                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(Signup2.this);
                                    LayoutInflater factory = LayoutInflater.from(Signup2.this);
                                    final View view = factory.inflate(R.layout.after_signup, null);
                                    alertadd.setView(view);
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent intent = new Intent(getApplicationContext(),Login2.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    alertadd.show();
                                }
                                else if(response.contains("Failed"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    waitText.setVisibility(View.GONE);

                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(Signup2.this);
                                    LayoutInflater factory = LayoutInflater.from(Signup2.this);
                                    final View view = factory.inflate(R.layout.login_failed, null);
                                    alertadd.setView(view);
                                    alertadd.setTitle("Registration Failed");
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertadd.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                                waitText.setVisibility(View.GONE);

                                Log.d("ResponseError",""+error);
                                AlertDialog.Builder alertadd = new AlertDialog.Builder(Signup2.this);
                                LayoutInflater factory = LayoutInflater.from(Signup2.this);
                                final View view = factory.inflate(R.layout.login_failed, null);
                                alertadd.setView(view);
                                alertadd.setTitle("Registration Failed");
                                alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertadd.show();
                            }
                        });

                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(stringRequest);
                    }
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}