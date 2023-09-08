package com.shahinur.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login2 extends AppCompatActivity {

    EditText userName,passWord;
    CheckBox showPassword;
    ProgressBar progressBar;
    TextView tvWait,signupText;
    Button loginButton;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.userName);
        passWord = findViewById(R.id.passWord);
        loginButton = findViewById(R.id.loginButton);
        showPassword = findViewById(R.id.showPassword);
        tvWait = findViewById(R.id.tvWait);
        signupText = findViewById(R.id.signupText);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getApplicationContext().getSharedPreferences(""+R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked)
                {
                    //Show Password
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    //Hide Password
                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userName.getText().toString();
                String password = passWord.getText().toString();
                if(username.isEmpty())
                {
                    userName.setError("Enter Your Username");
                }
                else if(password.isEmpty())
                {
                    passWord.setError("Enter Your Password");
                }
                else
                {
                    username=username.toLowerCase();
                    String url="https://appservicebysuvo.000webhostapp.com/app/login.php?un="+username+"&ps="+password;

                    progressBar.setVisibility(View.VISIBLE);
                    tvWait.setVisibility(View.VISIBLE);

                    String finalUsername = username;
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            tvWait.setVisibility(View.GONE);

                            Log.d("Response",""+response);
                            try {
                                boolean status = response.getBoolean("authenticate");


                                if(status)
                                {
                                    String UserId = response.getString("id");
                                    String Name = response.getString("name");
                                    String Username = response.getString("username");
                                    String Email = response.getString("email");

                                    editor.putString("userid",UserId);
                                    editor.putString("username",Username);
                                    editor.putString("name",Name);
                                    editor.putString("email",Email);
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                else
                                {
                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(Login2.this);
                                    LayoutInflater factory = LayoutInflater.from(Login2.this);
                                    final View view = factory.inflate(R.layout.login_failed, null);
                                    alertadd.setView(view);
                                    alertadd.setTitle("Login Failed");
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertadd.show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            tvWait.setVisibility(View.GONE);

                            Log.d("ErrorResponse",""+error);

                            new AlertDialog.Builder(Login2.this)
                                    .setIcon(R.drawable.warning)
                                    .setTitle("Error")
                                    .setMessage("Something Went Wrong\nTry Again Later")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(objectRequest);
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Signup2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}