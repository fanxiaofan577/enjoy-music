package cn.tiancaifan.enjoy_music.bean;

import java.util.List;

/**
 * @ClassName: Song
 * @Description: 歌曲实体类
 * @Date: 2022/4/10 12:20
 * @Author: fanxiaofan
 */
public class Song {
    //歌曲Id
    private Integer id;
    //歌曲名
    private String name;
    //艺术家
    private List<Artist> ar;
    //是否可以播放
    private Integer fee;
    //专辑
    private Album al;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artist> getAr() {
        return ar;
    }

    public void setAr(List<Artist> ar) {
        this.ar = ar;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Album getAl() {
        return al;
    }

    public void setAl(Album al) {
        this.al = al;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ar=" + ar +
                ", fee=" + fee +
                ", al=" + al +
                '}';
    }
}
