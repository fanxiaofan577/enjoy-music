package cn.tiancaifan.enjoy_music.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     *  搜索数量默认为30，返回数量默认30，页面默认为1，类型默认为单曲
     * @return
     */
    @GET("/search/")
    Call<ResponseBody> search(@Query("keywords") String keywords);
//
//    /**
//     * 关键词搜索音乐
//     * @param keywords 关键词
//     * @param limit 返回数量
//     * 页数默认为1，类型默认为单曲
//     * @return
//     */
//    @GET("/search")
//    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("limit") int limit);

    /**
     * 关键词搜索音乐
     * @param keywords 关键词
     * @param type 类型：1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台,
     * 页数默认为1，类型默认为单曲
     * @return
     */
    @GET("/search")
    Call<ResponseBody> search(@Query("keywords") String keywords,@Query("type") int type);

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
     * 获取专辑详情
     * @param id
     * @return
     */
    @GET("/album")
    Call<ResponseBody> playAlbum(@Query("id") Long id);

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

    /**
     * 获取排行榜
     * @return
     */
    @GET("toplist")
    Call<ResponseBody> toplist();

    /**
     * 所有榜单内容摘要
     * @return
     */
    @GET("toplist/detail")
    Call<ResponseBody> toplistDetail();

    /**
     * 获取热门电台
     * @param limit 取出数量 , 默认为 30 (支持 offset)
     * @param offset 偏移数量 , 用于分页 , 如 :( 评论页数 -1)*30, 其中 30 为 limit 的值
     * @return
     */
    @GET("/dj/hot")
    Call<ResponseBody> hotDj(@Query("limit") Integer limit,@Query("offset")int offset);

    /**
     * 获取歌单所有歌曲
     * @param id 歌单id
     * @return
     */
    @GET("/playlist/track/all")
    Call<ResponseBody> playlistTrackAll(@Query("id")Long id);
    /**
     * 获取推荐歌手
     * @return
     */
    @GET("/toplist/artist")
    Call<ResponseBody> toplistArtist();
    /**
     * 获取歌曲详情
     *
     * @return
     */
    @GET("/song/detail")
    Call<ResponseBody> songDetail(@Query("ids") Long ids);

    /**
     * 获取歌曲URL
     * @param id 歌曲id
     * @return
     */
    @GET("/song/url")
    Call<ResponseBody> songUrl(@Query("id") Long id);

    /**
     * 获取歌词
     * @param id
     * @return
     */
    @GET("/lyric")
    Call<ResponseBody> lyric(@Query("id") Long id);

    /**
     * 搜索建议
     * 说明 : 调用此接口 , 传入搜索关键词可获得搜索建议 , 搜索结果同时包含单曲 , 歌手 , 歌单 ,mv 信息
     * @param keywords 关键词
     * @param type 如果传 'mobile' 则返回移动端数据
     * @return
     */
    @GET("/search/suggest")
    Call<ResponseBody> searchSuggest(@Query("keywords") String keywords,@Query("type")String type);
    /**
     * 说明 : 调用此接口,可获取歌单分类,包含 category 信息
     * @return
     */
    @GET("/playlist/catlist")
    Call<ResponseBody> playlistCatlist();

    /**
     * 说明 : 调用此接口 , 可获取网友精选碟歌单
     * @return
     */
    @GET("/top/playlist")
    Call<ResponseBody> topPlaylist(@Query("cat") String cat);

    /**
     * 说明 : 调用此接口 , 可获取歌手详情
     * @param id
     * @return
     */
    @GET("/artist/detail")
    Call<ResponseBody> singerDetails(@Query("id") Long id);


    /**
     * 说明 : 调用此接口 , 可获取歌手单曲
     * @param id
     * @return
     */
    @GET("/artists")
    Call<ResponseBody> singerSong(@Query("id") Long id);


    /**
     * 说明 : 调用此接口 , 可获取歌手mv
     * @param id
     * @return
     */
    @GET("/artist/mv")
    Call<ResponseBody> singerMv(@Query("id") Long id);

    /**
     * 说明 : 调用此接口 , 可获取歌手专辑
     * @param id
     * @return
     */
    @GET("/artist/album")
    Call<ResponseBody> singerAlbum(@Query("id") Long id);
}
