package cn.tiancaifan.enjoy_music.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.adapter.SearchSuggestAdapter;
import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import cn.tiancaifan.enjoy_music.bean.Album;
import cn.tiancaifan.enjoy_music.bean.Music;
import cn.tiancaifan.enjoy_music.bean.SearchSuggest;
import cn.tiancaifan.enjoy_music.bean.Singer;
import cn.tiancaifan.enjoy_music.bean.SongSheet;
import cn.tiancaifan.enjoy_music.ui.my.MyFragment;
import cn.tiancaifan.enjoy_music.ui.radio.RadioFragment;
import cn.tiancaifan.enjoy_music.ui.rank.RankFragment;
import cn.tiancaifan.enjoy_music.ui.recommended.RecommendedFragment;
import cn.tiancaifan.enjoy_music.ui.widget.ClearEditText;
import cn.tiancaifan.enjoy_music.utils.RetrofitUtils;
import cn.tiancaifan.enjoy_music.utils.StatusBarUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ViewPager2 vp;
    private ClearEditText searchEditText;
    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private LinearLayout searchSuggest;
    private RetrofitAPI retrofitAPI = RetrofitUtils.getInstance().getApiService(RetrofitAPI.class);

    private RecyclerView recyclerView;
    private String keyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);
        searchEditText = findViewById(R.id.search_edit_text);
        toolbar = findViewById(R.id.toolbarInfo);
        appBarLayout = findViewById(R.id.appbar);
        vp = findViewById(R.id.vp);
        searchSuggest = findViewById(R.id.search_suggests);
        tabLayout = findViewById(R.id.main_tablayout);
        recyclerView = findViewById(R.id.search_suggest_recyclerview);

        //拿到传过来的数据
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");

        searchEditText.setText(keyword);

        toolbar.inflateMenu(R.menu.search_toobar_menu);
        toolbarCanelColor(Color.WHITE);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //处理事件
                    keyword = v.getText().toString().trim();
                    Intent intent = new Intent(SearchInfoActivity.this, SearchInfoActivity.class);
                    intent.putExtra("keyword",keyword);
                    startActivity(intent);
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
                        keyword = searchEditText.getText().toString();
                        Intent intent = new Intent(SearchInfoActivity.this, SearchInfoActivity.class);
                        intent.putExtra("keyword", keyword);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        /**
         * 监听搜索栏的文字变化
         * */
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

        //颜色
        int[] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, colors);
        appBarLayout.setBackground(gradientDrawable);
        StatusBarUtil.setGradientColor(SearchInfoActivity.this, toolbar);


        fragments = new ArrayList<>();
        fragments.add(new SearchInfoFragment<Music>(keyword, Music.class));
        fragments.add(new SearchInfoFragment<Album>(keyword, Album.class));
        fragments.add(new SearchInfoFragment<SongSheet>(keyword, SongSheet.class));
        fragments.add(new SearchInfoFragment<Singer>(keyword, Singer.class));

        //添加适配器
        MyFragmentStateAdapter myFragmentStateAdapter =
                new MyFragmentStateAdapter(this, fragments);

        vp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        vp.setAdapter(myFragmentStateAdapter);

        List<String> titles = new ArrayList<>();
        titles.add("单曲");
        titles.add("专辑");
        titles.add("歌单");
        titles.add("歌手");

        new TabLayoutMediator(tabLayout, vp, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();

    }

    private void toolbarCanelColor(int color) {
        MenuItem item = toolbar.getMenu().findItem(R.id.search_btn);
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    /**
     * 输入文字后布局变化
     */
    private void getSearch(String charSequence) {
        charSequence = charSequence.trim();
        if (charSequence.length() != 0) {
            appBarLayout.setBackgroundColor(Color.parseColor("#fbfafa"));
            StatusBarUtil.setColor(this, Color.parseColor("#fbfafa"));
            StatusBarUtil.setDarkMode(this);
            toolbarCanelColor(Color.BLACK);
            tabLayout.setVisibility(View.GONE);
            vp.setVisibility(View.GONE);
            searchSuggest.setVisibility(View.VISIBLE);
            getSearchSuggest(charSequence);
        } else {
            //颜色
            int[] colors = {Color.parseColor("#716bff"), Color.parseColor("#d837f6")};
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, colors);
            appBarLayout.setBackground(gradientDrawable);
            StatusBarUtil.setGradientColor(this, toolbar);
            toolbarCanelColor(Color.WHITE);
            tabLayout.setVisibility(View.VISIBLE);
            searchSuggest.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索建议
     */
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
                Log.d("SearchSuggest", responseString);
                JsonElement jsonElement = JsonParser.parseString(responseString).getAsJsonObject().get("result")
                        .getAsJsonObject().get("allMatch");
                List<SearchSuggest> searchSuggestList = null;

                if (jsonElement != null) {
                    JsonArray data = jsonElement.getAsJsonArray();
                    searchSuggestList = new Gson().fromJson(data, new TypeToken<List<SearchSuggest>>() {
                    }.getType());

                } else {
                    searchSuggestList = new ArrayList<>();
                }
                SearchSuggestAdapter adapter = new SearchSuggestAdapter(searchSuggestList, SearchInfoActivity.this);
                LinearLayoutManager manager = new LinearLayoutManager(SearchInfoActivity.this, RecyclerView.VERTICAL, false);
                adapter.setOnRvItemClick(new SearchSuggestAdapter.OnRvItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        TextView content = v.findViewById(R.id.search_suggest_content);
                        keyword = content.getText().toString();
                        Intent intent = new Intent(SearchInfoActivity.this, SearchInfoActivity.class);
                        intent.putExtra("keyword", keyword);
                        startActivity(intent);
                    }
                });
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    class MyFragmentStateAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }
    }
}