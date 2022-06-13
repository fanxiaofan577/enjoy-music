package cn.tiancaifan.enjoy_music.bean;

/**
 * @ClassName: SongUrl
 * @Description: TODO 歌曲URL
 * @Date: 2022/4/26 11:22
 * @Author: fanxiaofan
 */
public class SongUrl {
    private Long id;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SongUrl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
