package cn.tiancaifan.enjoy_music.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RetrofitAPI_bak {

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     *  搜索数量默认为30，返回数量默认30，页面默认为1，类型默认为单曲
     * @return
     */
    @GET("/search/")
    Call<ResponseBody> search(@Query("keywords") String keywords);

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     * @param limit 返回数量
     * 页数默认为1，类型默认为单曲
     * @return
     */
    @GET("/search")
    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("limit") int limit);

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     * @param limit 返回数量
     * @param offset 第几页
     *  搜索类型默认为单曲
     * @return
     */
    @GET("/search")
    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("limit") int limit,@Query("offset") int offset);

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     * @param limit 返回数量
     * @param offset 第几页
     * @param type 类型：1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台,
     * @return
     */
    @GET("/search")
    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("limit") int limit,@Query("offset") int offset,@Query("type") int type);

    /**
     * 电台banner
     * @return
     */
    @GET("/dj/banner")
    Call<ResponseBody> djBanner();

    /**
     * 获取banner 默认是pc类型
     * @return
     */
    @GET("/banner")
    Call<ResponseBody> banner();

    /**
     * 获取banner
     * @param type 0:pc 1:android 2:ipone 3:ipad
     * @return
     */
    @GET("/banner")
    Call<ResponseBody> banner(@Query("type") int type);

    /**
     *获取热门话题 默认为 20
     * @return
     */
    @GET("/hot/topic")
    Call<ResponseBody> hotTopic();

    /**
     * 获取热门话题
     * @param limit 取出评论数量 , 默认为 20
     * @return
     */
    @GET("/hot/topic")
    Call<ResponseBody> hotTopic(@Query("limit") int limit);

    /**
     * 获取热门话题
     * @param limit 取出评论数量 , 默认为 20
     * @param offset 偏移数量 , 用于分页 , 如 :( 评论页数 -1)*20, 其中 20 为 limit 的值
     * @return
     */
    @GET("/hot/topic")
    Call<ResponseBody> hotTopic(@Query("limit") int limit,@Query("offset")int offset);

    /**
     * 获取推荐歌单
     * @param limit 取出数量 , 默认为 30 (不支持 offset)
     * @return
     */
    @GET("/personalized")
    Call<ResponseBody> personalized(@Query("limit") Integer limit);

    /**
     * 获取歌单详情
     * @param id
     * @return
     */
    @GET("/playlist/detail")
    Call<ResponseBody> playlistDetail(@Query("id") Long id);

    /**
     * 热搜
     * @return
     */
    @GET("/search/hot/detail")
    Call<ResponseBody> searchHotDetail();


    /**
     * 获取热搜列表
     * @return
     */
    @GET("/search/hot/detail")
    Call<ResponseBody> hotSearchDetail();


    /**
     * 获取默认搜索关键字
     * @return
     */
    @GET("search/default")
    Call<ResponseBody> searchDefault();

//    /**
//     * 获取歌单详情
//     * @param id
//     * @return
//     */
//    @GET("/playlist/detail")
//    Call<ResponseBody> playlistDetail(@Query("id") Long id);
}
