package com.example.warhammer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AbsListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String id_SRM = "104169151";
    //int photo_count = 8; // ДЛЯ подгрузки кол-во фото, для отображения и парсинга
    int photo_count = 999;

    private String serviceKey = "3c28df41e2c3d4599bb14e5a54538ffc2842dbe5bc3ab79fd1704810e7cbcf9c0999bd00f372fa1e692c4";
    public static ArrayList<Post> tempAlbum = new ArrayList<>();
   // public static ArrayList<Post> tempAlbum_rezerv = new ArrayList<>();
    private static boolean clickedOnItem = false;
//    public static boolean initedGridArray = false;
//    private static boolean isLoadingProduts = false;
    GridView gridview;
    private boolean regim;
    private MenuItem mSearch;
    SearchView search;
    private String name;
    ProgressDialog pDialog;
    Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
        clickedOnItem = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setTitle("Загрузка продуктов. Подождите...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        Intent intent = getIntent();
        String fractid = intent.getExtras().getString("key");
        regim = intent.getExtras().getBoolean("flag");
        List<String> alb = new ArrayList<String>();
        if (!regim) alb.add(fractid);
        else {
            //если пост из альбома с не уникальными товарами тогда храним все ид альбомов
            if (fractid.equals(this.getString(R.string.demonsID)) || fractid.equals(this.getString(R.string.chaosMarinesID))){
                alb.add(this.getString(R.string.demonsID));
                alb.add(this.getString(R.string.chaosMarinesID));
            }
            else if (fractid.equals(this.getString(R.string.knightsID)) || fractid.equals(this.getString(R.string.mechsID))){
                alb.add(this.getString(R.string.knightsID));
                alb.add(this.getString(R.string.mechsID));
            }
            else if (fractid.equals(this.getString(R.string.eldarID)) || fractid.equals(this.getString(R.string.darkEldarID))){
                alb.add(this.getString(R.string.eldarID));
                alb.add(this.getString(R.string.darkEldarID));
            }
            else if (fractid.equals(this.getString(R.string.marinesID)) || fractid.equals(this.getString(R.string.wolfsID))
                    || fractid.equals(this.getString(R.string.bloodAngelsID)) || fractid.equals(this.getString(R.string.darkAngelsID))){
                alb.add(this.getString(R.string.marinesID));
                alb.add(this.getString(R.string.wolfsID));
                alb.add(this.getString(R.string.darkAngelsID));
                alb.add(this.getString(R.string.bloodAngelsID));
            }
            else alb.add(fractid);
            name = intent.getExtras().getString("name");
        }
//       Post a = new Post("", "Craftworlds Hemlock Wraithfighter, оригинал 2600р" +
//               "1600р.\n", "", "",
//                "222401845", "");
        parsePhotos(alb, photo_count, tempAlbum.size()); //загрузка данных из вк
        //код Куока
        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new DataAdapter(this, tempAlbum));
//        gridview.setOnScrollListener(scroll);

//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        DataAdapter postsAdapter = new DataAdapter(this, tempAlbum);
//        gridView.setAdapter(postsAdapter);
        //---

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       // drawer.addDrawerListener(toggle);
       // toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        // код Данила
        gridview.setOnItemClickListener(gridviewOnItemClickListener);

        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.filterlist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                if (selectedItemPosition == 2) sort(tempAlbum, false);
                else if (selectedItemPosition == 1) sort(tempAlbum, true);
                if (selectedItemPosition!=0) {
                    gridview.setAdapter(new DataAdapter(getApplicationContext(), tempAlbum));
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            tempAlbum.clear();
            tempAlbum.isEmpty();
            gridview.setAdapter(new DataAdapter(this, tempAlbum));
        }
    }

    private ArrayList<Post> find (ArrayList<Post> alb, String key){
        ArrayList<Post> tempAlbum = new ArrayList<>();
        for (int i = 0; i < alb.size(); i++){
            if (alb.get(i).getName().toLowerCase().trim().contains(key.trim().toLowerCase())) tempAlbum.add(alb.get(i));
        }
        return tempAlbum;
    }

    private void sort(ArrayList<Post> alb, Boolean poVozre){
        Collections.sort(alb);
        if (poVozre) Collections.reverse(alb); //сортировка по убыванию, если poVozre == true то по возрастанию
    }

    //поиск
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearch = menu.findItem(R.id.action_search);
        search = (SearchView) mSearch.getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (tempAlbum.size() > 0) {
                    ArrayList<Post> findAlb = find(tempAlbum, query);
                    gridview.setAdapter(new DataAdapter(getApplicationContext(), findAlb));
                    if (findAlb.size() > 0) {
                        //tempAlbum_rezerv = new ArrayList<>(tempAlbum);
                       // tempAlbum = new ArrayList<>(findAlb);
                       // gridview.setAdapter(new DataAdapter(getApplicationContext(), tempAlbum));
                        gridview.setBackgroundColor(getResources().getColor(R.color.vk_white));
                    }
                    else {
                        gridview.setBackgroundResource(R.drawable.no_match);
                        gridview.setAdapter(new DataAdapter(getApplicationContext(), findAlb));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //обрабатывает изменение текста в поиске
                return false;
            }
        });
        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
        {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                // Do something when collapsed
                if (!regim) {
                    //if (tempAlbum_rezerv.size() > 0) {
                    //    tempAlbum = new ArrayList<>(tempAlbum_rezerv);
                        gridview.setAdapter(new DataAdapter(getApplicationContext(), tempAlbum));
                    //    tempAlbum_rezerv.clear();
                   // }
                    gridview.setBackgroundColor(getResources().getColor(R.color.vk_white));
                    toolbar.setBackgroundResource(R.drawable.logo_w1);
                }
                else onBackPressed();
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                // TODO Auto-generated method stub
                toolbar.setBackgroundColor(getResources().getColor(R.color.vk_grey_color));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

//    GridView.OnScrollListener scroll = new GridView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//            // TODO Auto-generated method stub
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            if ((firstVisibleItem + visibleItemCount) >= (totalItemCount*2/3) && totalItemCount!=0 && !isLoadingProduts) {
//                parsePhotos(alb, photo_count, tempAlbum.size());
//            }
//        }
//    };

    //открытие нового Активити(окно с товаром)
    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (!clickedOnItem) {
                clickedOnItem = true;
                Intent i = new Intent(getApplicationContext(), ProductActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        }
    };

    private void initGridArray(ArrayList<Post> a) {
        DataAdapter postsItems = new DataAdapter(this, a);
        postsItems.notifyDataSetChanged();
        GridView gridView = (GridView) findViewById(R.id.gridView);
//        DataAdapter postsAdapter = new DataAdapter(this, tempAlbum);
        gridView.invalidateViews();
    }

    // HTTP GET request
    private void makeVkRequest(String method, HashMap<String, String> args, Callback func) throws Exception {
        String access_token = serviceKey;
        args.put("access_token", access_token);
        args.put("v", "5.92");
        StringBuilder res_args = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            res_args.append(entry.getKey());
            res_args.append("=");
            res_args.append(entry.getValue());
            res_args.append("&");
        }

        String url = "https://api.vk.com/method/" + method + "?" + res_args;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(func);

    }

    private void parsePhotos(List<String> albums, int count, int offset) {
//        isLoadingProduts = true;
        for (int i = 0; i < albums.size(); i++) {
            HashMap<String, String> args = new HashMap<String, String>();
            args.put("album_id", albums.get(i));
            args.put("count", String.valueOf(count + 1));
            args.put("offset", String.valueOf(offset));
            getPhotos(args);
        }
    }

    private void getPhotos(HashMap<String, String> args) { //передавать id_альбомов, которые нужно брать в массиве temp_alb будет висеть результат
        try {
            args.put("owner_id", "-"+id_SRM);
            //args.put("count", "1000");

            makeVkRequest("photos.get", args, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    e.printStackTrace();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//обработка ошибки
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String my_response = response.body().string();
                        new Thread()
                        {
                            public void run()
                            {
//                                MainActivity.this.runOnUiThread(new Runnable()
//                                {
//                                    public void run()
//                                    {
                                        try {
                                            JSONObject ob = (new JSONObject(my_response)).getJSONObject("response");
                                            final JSONArray ar = ob.getJSONArray("items");
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pDialog.setMax(ar.length());
                                                    pDialog.setProgressNumberFormat("%d");
                                                    pDialog.show();
                                                }
                                            });
                                            for (int i = 0; i < ar.length(); i++) {
                                                JSONObject object = (JSONObject) ar.get(i);
                                                int max = object.getJSONArray("sizes").length();
                                                JSONObject picture_big = object.getJSONArray("sizes").getJSONObject(max - 1); //фоток всгеда 9 берем самую последнюю
                                                JSONObject picture_small = new JSONObject();
                                                for (int j = 0; j < max; j++) {
                                                    picture_small = object.getJSONArray("sizes").getJSONObject(j);
                                                    if (picture_small.getString("type") == "q")
                                                        j = max + 1;
                                                }

                                                Post good = new Post(object.getString("id"), object.getString("text"), picture_big.getString("url"),
                                                        picture_small.getString("url"), object.getString("album_id"), object.getString("user_id"));
                                                if (object.getString("text").length() > 0) {
                                                    tempAlbum.add(good);
                                                } else {
                                                    getFirstPhotoComment(object.getString("owner_id"), object.getString("id"), good);
                                                }
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.incrementProgressBy(1);
                                                        System.out.println("Отработал");
                                                    }
                                                });
                                            }
//                                    if(!initedGridArray){
//                                        initedGridArray = true;
//                                        initGridArray();
//                                    }
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pDialog.dismiss();
                                                    //sort(tempAlbum, false);
                                                    initGridArray(tempAlbum);
                                                    if (regim) {
                                                        mSearch.expandActionView();
                                                        search.setQuery(name, true);
                                                        search.clearFocus();
                                                    }
                                                }
                                            });
//                                            isLoadingProduts = false;
                                        } catch (final Exception e) {
                                            e.printStackTrace();
                                            System.out.println(e.toString());
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast t = Toast.makeText(MainActivity.this, "Parse error " + e.toString(), Toast.LENGTH_LONG);
                                                    t.show();
                                                }
                                            });
                                        }
//                                    }
//                                });
                            }
                        }.start();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Получить 1-ый комментарий фотографии
    private void getFirstPhotoComment(String owner_id, String photo_id, final Post post) {
        try {
            HashMap<String, String> args = new HashMap<String, String>();
            args.put("owner_id", owner_id);
            args.put("photo_id", photo_id);
            args.put("count", "1");

            makeVkRequest("photos.getComments", args, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    e.printStackTrace();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//обработка ошибки
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String my_response = response.body().string();
                        new Thread() {
                            public void run() {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
                                try {
                                    JSONObject ob = (new JSONObject(my_response)).getJSONObject("response");
                                    final JSONArray ar = ob.getJSONArray("items");
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pDialog.setMax(ar.length());
                                            pDialog.setProgressNumberFormat("%d");
                                            pDialog.show();
                                        }
                                    });
                                    for (int i = 0; i < ar.length(); i++) {
                                        JSONObject object = (JSONObject) ar.get(i);
                                        int max = object.getJSONArray("sizes").length();
                                        JSONObject picture_big = object.getJSONArray("sizes").getJSONObject(max - 1); //фоток всгеда 9 берем самую последнюю
                                        JSONObject picture_small = new JSONObject();
                                        for (int j = 0; j < max; j++) {
                                            picture_small = object.getJSONArray("sizes").getJSONObject(j);
                                            if (picture_small.getString("type") == "q")
                                                j = max + 1;
                                        }

                                        Post good = new Post(object.getString("id"), object.getString("text"), picture_big.getString("url"),
                                                picture_small.getString("url"), object.getString("album_id"), object.getString("user_id"));
                                            tempAlbum.add(good);
                                    }
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pDialog.dismiss();
                                            initGridArray(tempAlbum);
                                            if (regim) {
                                                mSearch.expandActionView();
                                                search.setQuery(name, true);
                                                search.clearFocus();
                                            }
                                        }
                                    });
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                    System.out.println(e.toString());
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast t = Toast.makeText(MainActivity.this, "Parse error " + e.toString(), Toast.LENGTH_LONG);
                                            t.show();
                                        }
                                    });
                                }
//                            }
//                        });
                            }
                        };
                    }
                }
            });
        } catch (Exception e) {

        }

    }
}
