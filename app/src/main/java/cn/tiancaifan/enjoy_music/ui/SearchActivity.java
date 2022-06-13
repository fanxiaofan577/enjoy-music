package cn.tiancaifan.enjoy_music.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.yang.flowlayoutlibrary.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.HotSearchAdapter;
import cn.tiancaifan.enjoy_music.adapter.SearchSuggestAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.HotSearch;
import cn.tiancaifan.enjoy_music.bean.SearchDefault;
import cn.tiancaifan.enjoy_music.bean.SearchSuggest;
import cn.tiancaifan.enjoy_music.ui.widget.ClearEditText;
import cn.tiancaifan.enjoy_music.utils.DataBaseUtil;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * @ClassName: SearchActivity
 * @Description: 搜索Activity
 * @Date: 2022/4/9 20:56
 * @Author: wangjunlin
 */
public class SearchActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private  SearchDefault searchDefault;//默认搜索
    private ClearEditText searchEditText;
    private List<HotSearch> hotSearchAll;//热搜列表
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);
    private HotSearchAdapter hotSearchAdapter;
    private LinearLayout searchContent,searchSuggest;
    private RecyclerView searchSuggestRecyclerview;
    private FlowLayout searchHistory;
    private DataBaseUtil dataBaseUtil;
    private ImageView deleteAllHistory;
    private List<String> searchHistoryKeywords;
    private List<SearchSuggest> searchSuggestList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();

        toolbar.inflateMenu(R.menu.search_toobar_menu);
        toolbarSearchColor(Color.WHITE);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getSearch(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //处理事件
                    String keyword = v.getText().toString().trim();
                    dataBaseUtil.addKeywordsToSearchHistory(keyword);
                    searchHistory.addView(keyword, new FlowLayout.OnItemClickListener() {
                        @Override
                        public void onItemClick(String content) {
                            Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                            intent.putExtra("keyword",content);
                            startActivity(intent);
                        }
                    });
                    Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                    intent.putExtra("keyword",keyword);
                    startActivity(intent);

                    InputMethodManager imm = (InputMethodManager)
                            v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }else {
                    return false;
                }
                return true;
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_btn:
                        String keyword = searchEditText.getText().toString();
                        searchEditText.setText("");
                        if (keyword!=null&&!keyword.equals("")){
                            dataBaseUtil.addKeywordsToSearchHistory(keyword);
                            searchHistory.addView(keyword, new FlowLayout.OnItemClickListener() {
                                @Override
                                public void onItemClick(String content) {
                                    Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                                    intent.putExtra("keyword",content);
                                    startActivity(intent);
                                }
                            });
                            Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                            intent.putExtra("keyword",keyword);
                            startActivity(intent);

                        }else {
                            Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_LONG).show();
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });

        //颜色
        int[] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(this, toolbar);
        StatusBarUtil.setPaddingTop(this,searchSuggest);
        getHotSearch();
        getSearchDefaultKeywords();
    }

    private void toolbarSearchColor(int color) {
        MenuItem item = toolbar.getMenu().findItem(R.id.search_btn);
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    private void init() {
        appBarLayout = findViewById(R.id.search_appbarlayout);
        toolbar = findViewById(R.id.search_toolbar);
        searchEditText = findViewById(R.id.search_edit_text);
        searchContent = findViewById(R.id.search_content);
        searchSuggestRecyclerview = findViewById(R.id.search_suggest_recyclerview);
        searchSuggest = findViewById(R.id.search_suggest);
        searchHistory = findViewById(R.id.searchHistory);
        deleteAllHistory = findViewById(R.id.deleteAllHistory);

        dataBaseUtil = new DataBaseUtil(new DataBaseUtil.DataBaseHelper(this, "MusicInfo.db", null, 1));
        searchHistoryKeywords = dataBaseUtil.getSearchHistory();
        this.searchHistory.addViews(searchHistoryKeywords, new FlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(String content) {
                Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                intent.putExtra("keyword",content);
                startActivity(intent);
            }
        });

        deleteAllHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseUtil.clearSearchHistory();
                searchHistoryKeywords = new ArrayList<>();
                searchHistory.setViews(searchHistoryKeywords,null);
            }
        });
    }


    //搜索
    private void getSearch(String charSequence){
        charSequence = charSequence.trim();
        if (charSequence.length()!=0){
            appBarLayout.setBackgroundColor(Color.parseColor("#fbfafa"));
            StatusBarUtil.setColor(this,Color.parseColor("#fbfafa"));
            StatusBarUtil.setDarkMode(this);
            toolbarSearchColor(Color.BLACK);
            searchContent.setVisibility(View.GONE);
            searchSuggest.setVisibility(View.VISIBLE);

            getSearchSuggest(charSequence);
        }else {
            //颜色
            int[] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, colors);
            appBarLayout.setBackground(gradientDrawable);
            StatusBarUtil.setGradientColor(this,toolbar);
            toolbarSearchColor(Color.WHITE);
            searchContent.setVisibility(View.VISIBLE);
            searchSuggest.setVisibility(View.GONE);

        }
    }

    private void getSearchSuggest(String charSequence) {

        Call<ResponseBody> call = retrofitAPI.searchSuggest(charSequence, "mobile");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonElement jsonElement = JsonParser.parseString(responseString).getAsJsonObject().get("result")
                        .getAsJsonObject().get("allMatch");


                if (jsonElement != null){
                    JsonArray data = jsonElement.getAsJsonArray();
                    searchSuggestList = new Gson().fromJson(data, new TypeToken<List<SearchSuggest>>(){}.getType());

                }else {
                    searchSuggestList = new ArrayList<>();
                }
                SearchSuggestAdapter adapter = new SearchSuggestAdapter(searchSuggestList, SearchActivity.this);
                LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false);
                adapter.setOnRvItemClick(new SearchSuggestAdapter.OnRvItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        searchEditText.setText("");
                        String keyword = searchSuggestList.get(position).getKeyword();
                        dataBaseUtil.addKeywordsToSearchHistory(keyword);
                        searchHistory.addView(keyword, new FlowLayout.OnItemClickListener() {
                            @Override
                            public void onItemClick(String content) {
                                Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                                intent.putExtra("keyword",content);
                                startActivity(intent);
                            }
                        });
                        Intent intent = new Intent(SearchActivity.this, SearchInfoActivity.class);
                        intent.putExtra("keyword",keyword);
                        startActivity(intent);
                    }
                });
                searchSuggestRecyclerview.setLayoutManager(manager);
                searchSuggestRecyclerview.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    //获取默认搜索
    private void getSearchDefaultKeywords() {

        Call<ResponseBody> responseBodyCall = retrofitAPI.searchDefault();
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonObject data = JsonParser.parseString(string).getAsJsonObject().getAsJsonObject("data");
                searchDefault = new Gson().fromJson(data, SearchDefault.class);
                searchEditText.setHint(searchDefault.getShowKeyword());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //获取热搜列表
    private void getHotSearch() {

        TextView textView = findViewById(R.id.findAll);
        CardView cardView = findViewById(R.id.card_view);
        RecyclerView recyclerView = findViewById(R.id.hot_search_items);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        Call<ResponseBody> call = retrofitAPI.hotSearchDetail();
        //监听查看更多,点击后文字消失并且展开
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height += 100;
                cardView.setLayoutParams(layoutParams);
                textView.setVisibility(View.GONE);
                hotSearchAdapter.setIndex(hotSearchAll.size());
            }
        });
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = "";
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonArray data = JsonParser.parseString(string).getAsJsonObject().getAsJsonArray("data");
                hotSearchAll = new Gson().fromJson(data, new TypeToken<List<HotSearch>>() {
                }.getType());
                //弄一个适配器
                hotSearchAdapter = new HotSearchAdapter(hotSearchAll, 16, SearchActivity.this,searchHistory,dataBaseUtil);
                recyclerView.setAdapter(hotSearchAdapter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}