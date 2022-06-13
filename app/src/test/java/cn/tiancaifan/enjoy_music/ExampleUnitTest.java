package cn.tiancaifan.enjoy_music;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;


import cn.tiancaifan.enjoy_music.api.RetrofitAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void MusicAPITest(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://101.43.40.117:4000/") //设置网络请求的Url地址
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
         retrofit2.Call<ResponseBody> call = retrofitAPI.search("孤勇者");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(string);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("错误");
            }
        });
    }
}