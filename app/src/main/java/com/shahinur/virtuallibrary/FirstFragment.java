package com.shahinur.virtuallibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class FirstFragment extends Fragment {

    GridView gridView;
    ProgressBar progressBar;
    public static String ctg="";
    public static String id="0";
    String pdf_url="";
    String img_url="";
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_first, container, false);
        gridView = myView.findViewById(R.id.gridView);
        progressBar = myView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        getBooks();

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);


        return myView;
    }

    private void getBooks()
    {
        arrayList = new ArrayList<>();
        String url="https://appservicebysuvo.000webhostapp.com/app/getBook.php?ctg="+ctg+"&cid="+id;
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for(int i=1;i<response.length();i++) {
                    try {
                        Log.d("Response",""+response);
                        JSONObject object = response.getJSONObject(i);
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
                        arrayList.add(hashMap);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.d("ArrayList",""+arrayList);
                if(ctg.contains("সকল বই")) {
                    SearchBook.arrayList = (ArrayList<HashMap<String, String>>) arrayList.clone();
                    Log.d("ArrayList", "Searchbook" + SearchBook.arrayList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ResponseError",""+error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(arrayRequest);
    }


    private class MyAdapter extends BaseAdapter{
        RelativeLayout linearLayout;
        ImageView bookImage;
        TextView bookTitle,bookAuthor;
        Button bookButton;
        @Override
        public int getCount() {
            return arrayList.size();
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
            linearLayout = myView.findViewById(R.id.linearLayout);
            bookImage = myView.findViewById(R.id.bookImage);
            bookTitle = myView.findViewById(R.id.bookTitle);
            bookAuthor = myView.findViewById(R.id.bookAuthor);
            bookButton = myView.findViewById(R.id.bookButton);


            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);
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
                        Toast.makeText(getActivity(), "Sorry! The book is not available right now", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent myIntent = new Intent(getActivity(), loadPdf.class);
                        myIntent.putExtra("pdfUrl", pdfLink);
                        myIntent.putExtra("pdfName", Name);
                        myIntent.putExtra("pdfId", Id);
                        myIntent.putExtra("catId", cId);
                        startActivity(myIntent);
                    }
                }
            });

            Animation LeftToRight = AnimationUtils.loadAnimation(getContext(),R.anim.book_list_anim);
            linearLayout.startAnimation(LeftToRight);

            return myView;
        }
    }
}