package com.shahinur.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBook extends AppCompatActivity {

    EditText searchText;
    ImageView searchIcon;
    TextView emptyList;
    GridView gridView;
    ProgressBar progressBar;

    public static ArrayList<HashMap<String,String>> arrayList;

    ArrayList<HashMap<String,String>> searchList;
    HashMap<String,String> hashMap;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        searchText = findViewById(R.id.searchText);
        searchIcon = findViewById(R.id.searchIcon);
        emptyList = findViewById(R.id.emptyList);
        gridView = findViewById(R.id.gridView);
        progressBar = findViewById(R.id.progressBar);

        searchList = new ArrayList<>(arrayList);
        Log.d("SearchList: ",""+searchList);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String txt = searchText.getText().toString();

                    if(!txt.isEmpty()) {
                        searchList = new ArrayList<>();
//                        Toast.makeText(SearchBook.this, "" + txt, Toast.LENGTH_SHORT).show();
                        searchBook(txt);
//                        search(txt);
                    }
            }
        });

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);

    }


    private class MyAdapter extends BaseAdapter{
        RelativeLayout linearLayout;
        ImageView bookImage;
        TextView bookTitle,bookAuthor;
        Button bookButton;

        @Override
        public int getCount() {
//            return arrayList.size();
            return searchList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View myView = inflater.inflate(R.layout.book_item_2,viewGroup,false);
            bookImage = myView.findViewById(R.id.bookImage);
            bookTitle = myView.findViewById(R.id.bookTitle);
            bookAuthor = myView.findViewById(R.id.bookAuthor);
            bookButton = myView.findViewById(R.id.bookButton);
            linearLayout = myView.findViewById(R.id.linearLayout);

            hashMap = new HashMap<>();
            hashMap = searchList.get(i);
//            hashMap = arrayList.get(i);
            String cId = hashMap.get("catId");
            String Id = hashMap.get("bookId");
            String Name=hashMap.get("bookName");
            String Author=hashMap.get("bookAuthor");
            String Image=hashMap.get("bookImage");
            String pdfLink=hashMap.get("bookLink");

            Picasso.get()
                    .load(Image)
                    .placeholder(R.drawable.book_loading)
                    .error(R.drawable.book_cover)
                    .into(bookImage);
            bookTitle.setText(Name);
            bookAuthor.setText(Author);

            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(pdfLink.contains("null")) {
                        Toast.makeText(getApplicationContext(), "Sorry! The book is not available right now", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent myIntent = new Intent(getApplicationContext(), loadPdf.class);
                        myIntent.putExtra("pdfUrl", pdfLink);
                        myIntent.putExtra("pdfName", Name);
                        myIntent.putExtra("pdfId", Id);
                        myIntent.putExtra("catId", cId);
                        startActivity(myIntent);
                    }
                }
            });

            Animation LeftToRight = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.book_list_anim);
            linearLayout.startAnimation(LeftToRight);

            return myView;
        }
    }


    private void searchBook(String text)
    {
//        arrayList = new ArrayList<>();
        searchList = new ArrayList<>();
        String url="https://appservicebysuvo.000webhostapp.com/app/searchBook.php?key="+text;

        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        if (i==0)
                        {
                            String res = object.getString("result");
                            if(res.equals("empty"))
                            {
                                gridView.setEmptyView(emptyList);
                                break;
                            }
                            else
                            {
                                emptyList.setVisibility(View.GONE);
                                continue;
                            }
                        }
                        else
                        {
                            int catId = object.getInt("cat_id");
                            int bookId = object.getInt("book_id");
                            String bookName=object.getString("book_name");
                            String bookAuthor=object.getString("book_author");
                            String bookImage=object.getString("book_image");
                            String bookLink=object.getString("book_link");


                            hashMap = new HashMap<>();
                            hashMap.put("catId",""+catId);
                            hashMap.put("bookId",""+bookId);
                            hashMap.put("bookName",""+bookName);
                            hashMap.put("bookAuthor",""+bookAuthor);
                            hashMap.put("bookImage",""+bookImage);
                            hashMap.put("bookLink",""+bookLink);
//                            arrayList.add(hashMap);
                            searchList.add(hashMap);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                gridView.setVisibility(View.GONE);
            }
        });
        queue = Volley.newRequestQueue(SearchBook.this);
        queue.add(arrayRequest);
    }

//End
}
