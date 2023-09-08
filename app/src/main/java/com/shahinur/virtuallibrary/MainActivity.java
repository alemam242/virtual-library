package com.shahinur.virtuallibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    FrameLayout frameLayout;
    NavigationView navigationView;
    View myView;
    TabLayout tabLayout;
    CircleImageView headerImage;
    TextView headerTitle,headerEmail,headerUsername,LoadingText;
    LinearLayout profileDetails;
    BottomNavigationView bottomNavigationView;
    AdView adView;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String Username,FullName,Email,Userid;
    String nm="Guest",unm="guest",eml="guest@gmail.com";
    RequestQueue queue;
    String navItem="";
    boolean Notify = false;
    HashMap<String,String> hashMap;
    public static ArrayList<HashMap<String,String>> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frameLayout);

        navigationView = findViewById(R.id.navigationView);
        myView = navigationView.getHeaderView(0);

        headerImage = myView.findViewById(R.id.headerImage);
        headerTitle = myView.findViewById(R.id.headerTitle);
        headerUsername = myView.findViewById(R.id.headerUsername);
        headerEmail = myView.findViewById(R.id.headerEmail);
        profileDetails = myView.findViewById(R.id.profileDetails);
        LoadingText = myView.findViewById(R.id.LoadingText);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tabLayout = findViewById(R.id.tabLayout);
        adView = findViewById(R.id.adView);
        sharedPreferences = getSharedPreferences(""+R.string.app_name,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Username = sharedPreferences.getString("username","guest");
        FullName = sharedPreferences.getString("name","guest");
        Email = sharedPreferences.getString("email","null");
        Userid = sharedPreferences.getString("userid","0");



        getProfileDetails();



        loadCategory();
        FirstFragment.ctg="সকল বই";
        loadFragment();
        //===================================================================
        checkNotification(Userid);



        // for nav drawer toogle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this,drawerLayout,toolbar,R.string.drawer_close,R.string.drawer_open
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
//                profileDetails.setVisibility(View.GONE);
//                LoadingText.setVisibility(View.VISIBLE);
                if(Profile.editProfile) {
                    getProfileDetails();
                }
//                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        /////////////////////////


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.search)
                {
//                    Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(getApplicationContext(), SearchBook.class);
                    startActivity(myIntent);

                }
                else if(item.getItemId()==R.id.Logout)
                {
                    adView.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(false)
                            .setTitle("LOGOUT")
                            .setMessage("Do you really want to logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editor.clear().commit();
                                    Intent intent = new Intent(getApplicationContext(),Login2.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                }
                return false;
            }
        });


        //===================================================
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home)
                {
                    if(Notify)
                    {
//                        Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                        updateNotification();
                        Notify = false;
                    }
//                    checkNotification(Userid);
                    navItem="home";
                    tabLayout.setVisibility(View.VISIBLE);
                    adView.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setSelectedItemId(R.id.Home);
                    frameLayout.removeAllViews();
//                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();

                    tabLayout.getTabAt(0).select();
                    FirstFragment.ctg="সকল বই";
                    FirstFragment.id="0";
                    loadFragment();

                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                if(item.getItemId() == R.id.changePassword)
                {
                    navItem="changePassword";
                    tabLayout.setVisibility(View.GONE);
//                    bottomNavigationView.setVisibility(View.GONE);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, new ChangePassword());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (item.getItemId() == R.id.deleteAccount)
                {
                    navItem="deleteAccount";
                    tabLayout.setVisibility(View.GONE);
//                    bottomNavigationView.setVisibility(View.GONE);

                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayout, new DeleteAccount());
                    fragmentTransaction.commit();
                    Log.d("DeleteAccount","It's Okay");
//                    Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if(item.getItemId() == R.id.share)
                {
                    navItem="share";
//                    adView.setVisibility(View.GONE);
//                    bottomNavigationView.setVisibility(View.VISIBLE);
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://drive.google.com/drive/folders/1p73cLmD9RhsnZFeUbuSVjnJEzCjgICsP?usp=drive_link";
                    String shareSub = "Share our app";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "using"));
//                    Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                return true;
            }
        });


        //========Tab Listener=========//
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tab_name=tab.getText().toString();
                for (int i = 0; i < arrayList.size(); i++) {
                    hashMap = new HashMap<>();
                    hashMap = arrayList.get(i);
                    if (hashMap.get("category").equals(tab_name)){
                        FirstFragment.id = hashMap.get("id");
                        break;
                    }
                }
//                Toast.makeText(MainActivity.this, ""+tab_name, Toast.LENGTH_SHORT).show();
                FirstFragment.ctg=tab_name;
                loadFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //========================================================
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.Home)
                {
                    if(Notify)
                    {
//                        Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                        updateNotification();
                        Notify = false;
                    }
                    checkNotification(Userid);
                    navItem="home";
                    tabLayout.setVisibility(View.VISIBLE);
                    frameLayout.removeAllViews();

                    tabLayout.getTabAt(0).select();
                    FirstFragment.ctg="সকল বই";
                    loadFragment();

//                    Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(id==R.id.Notification)
                {
                    Notify = true;
                    bottomNavigationView.getOrCreateBadge(R.id.Notification).clearNumber();
                    navItem="notification";
                    tabLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new Notifications());
                    fragmentTransaction.commit();

                    return true;
                }
                else if(id==R.id.Profile)
                {
                    if(Notify)
                    {
//                        Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                        updateNotification();
                        Notify = false;
                    }
                    checkNotification(Userid);
                    navItem="profile";
                    tabLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new Profile());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                {
                    if(Notify)
                    {
//                        Toast.makeText(MainActivity.this, ""+Notify, Toast.LENGTH_SHORT).show();
                        updateNotification();
                        Notify = false;
                    }
                    checkNotification(Userid);
                    navItem="favourite";
                    tabLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.add(R.id.frameLayout, new WishList());
                    fragmentTransaction.commit();
//                    Toast.makeText(MainActivity.this, "WishList", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }



    private void updateNotification(){
        String url="https://appservicebysuvo.000webhostapp.com/app/updateNotification.php?uid="+Userid;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");

                    if(result.equals("success"))
                    {
                        bottomNavigationView.getOrCreateBadge(R.id.Notification).clearNumber();
//                        Toast.makeText(getApplicationContext(), "Notification updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        Toast.makeText(getApplicationContext(), "Notification update failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(objectRequest);
    }
    // Checking Notification Status
    public void checkNotification(String userId){
        String url = "https://appservicebysuvo.000webhostapp.com/app/checkNotification.php?uid="+userId;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String res = response.getString("result");
                    String[] separated = res.split(":");
                    String txt = separated[0];
                    int nmbr = Integer.parseInt(separated[1]);

                    Log.d("notify",""+res);
                    if(nmbr>0)
                    {
                        bottomNavigationView.getOrCreateBadge(R.id.Notification).setNumber(nmbr);
                    }
                    else{
                        Log.d("notify",""+res);
                        bottomNavigationView.getOrCreateBadge(R.id.Notification).clearNumber();
                    }
                } catch (JSONException e) {
                    Log.d("notify",""+e);
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(objectRequest);
    }

    // Back Pressed

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.warning)
                .setTitle("Quit")
                .setMessage("Are you sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showRatingBox();
//                        finishAndRemoveTask();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showRatingBox()
    {
        ImageView star1,star2,star3,star4,star5;
        AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.rating, null);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
        alertadd.setView(view);
        alertadd.setCancelable(true);
        alertadd.setTitle("Rate This App");
        alertadd.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finishAndRemoveTask();
            }
        });
        alertadd.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finishAndRemoveTask();
            }
        });
        alertadd.show();

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star_fill);
                star5.setImageResource(R.drawable.star);
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.star_fill);
                star2.setImageResource(R.drawable.star_fill);
                star3.setImageResource(R.drawable.star_fill);
                star4.setImageResource(R.drawable.star_fill);
                star5.setImageResource(R.drawable.star_fill);
            }
        });



    }
    //==================================================


    private void loadCategory()
    {
        arrayList = new ArrayList<>();
        tabLayout.addTab(tabLayout.newTab().setText("সকল বই"));

        String url="https://appservicebysuvo.000webhostapp.com/app/getCategory.php";

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++)
                {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        int id = object.getInt("id");
                        String category = object.getString("category");

                        hashMap = new HashMap<>();
                        hashMap.put("id",""+id);
                        hashMap.put("category",category);
                        arrayList.add(hashMap);


                        // Dynamically set category in the tablayout
                        tabLayout.addTab(tabLayout.newTab().setText(category));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ServerResponse",""+error);
            }
        });

        queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(arrayRequest);
    }

    private void loadFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frameLayout, new FirstFragment());
        fragmentTransaction.commit();
    }

    public void getProfileDetails()
    {
        if(Profile.editProfile)
        {
            Profile.editProfile = false;
        }
        LoadingText.setVisibility(View.VISIBLE);
        profileDetails.setVisibility(View.GONE);
        String url = "https://appservicebysuvo.000webhostapp.com/app/getProfileDetails.php?uid="+Userid;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LoadingText.setVisibility(View.GONE);
                profileDetails.setVisibility(View.VISIBLE);
                Log.d("DResponse","result: "+response);
                try {
                    String fn=response.getString("name");
                    String un=response.getString("username");
                    String em=response.getString("email");

                    FullName = fn;
                    Username =un;
                    Email = em;

                    headerTitle.setText(FullName);
                    headerUsername.setText(Username);
                    headerEmail.setText(Email);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingText.setVisibility(View.GONE);
                profileDetails.setVisibility(View.VISIBLE);
                Log.d("DResponse","error: "+error);
            }
        });

        queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(objectRequest);
    }

}