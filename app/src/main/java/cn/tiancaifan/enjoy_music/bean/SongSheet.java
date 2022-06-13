package cn.tiancaifan.enjoy_music.bean;

import java.util.List;

/**
 * @ClassName: SongSheet
 * @Description: 歌单实体对象
 * @Date: 2022/4/7 17:13
 * @Author: fanxiaofan
 */
public class SongSheet {

    //歌单id
    private Long id;
    //歌单名
    private String name;
    //歌单图片URL
    private String picUrl,coverImgUrl;
    //最后更新时间
    private Long trackNumberUpdateTime;
    //播放次数
    private Long playCount;
    //创建者
    private User creator;
    //歌曲简略列表
    private List<Music> tracks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        if (coverImgUrl!=null)
            this.picUrl = this.coverImgUrl;
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Long getTrackNumberUpdateTime() {
        return trackNumberUpdateTime;
    }

    public void setTrackNumberUpdateTime(Long trackNumberUpdateTime) {
        this.trackNumberUpdateTime = trackNumberUpdateTime;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public List<Music> getTracks() {
        return tracks;
    }

    public void setTracks(List<Music> tracks) {
        this.tracks = tracks;
    }

    public String getCoverImgUrl() {
        if (picUrl!=null)
            this.coverImgUrl = this.picUrl;
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
        this.picUrl = coverImgUrl;
    }

    @Override
    public String toString() {
        return "SongSheet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                ", trackNumberUpdateTime=" + trackNumberUpdateTime +
                ", playCount=" + playCount +
                ", creator=" + creator +
                ", tracks=" + tracks +
                '}';
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
