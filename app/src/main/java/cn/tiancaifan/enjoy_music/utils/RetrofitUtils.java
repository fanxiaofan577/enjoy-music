package cn.tiancaifan.enjoy_music.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @ClassName: RetrofitUtils
 * @Description: 单例模式RetrofitUtils
 * @Date: 2022/3/31 11:44
 * @Author: fanxiaofan
 */
public class RetrofitUtils {
    private static RetrofitUtils retrofitUtils;

    private static final String  BASE_URL= "http://101.43.40.117:4000";

    private RetrofitUtils() {
    }

    public static RetrofitUtils getInstance(){

        if (retrofitUtils==null){
            synchronized (RetrofitUtils.class){
                if (retrofitUtils==null){
                    retrofitUtils=new RetrofitUtils();
                }
            }
        }
        return retrofitUtils;
    }


    private static Retrofit retrofit;

    private static synchronized Retrofit getRetrofit(){
        OkHttpClient.Builder ok = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS);

        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL).build();
        }
        return retrofit;
    }

    public <T> T getApiService(Class<T> cl){
        Retrofit retrofit = getRetrofit();
        return retrofit.create(cl);
    }
}
