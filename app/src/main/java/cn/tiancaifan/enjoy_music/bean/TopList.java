package cn.tiancaifan.enjoy_music.bean;

import java.util.List;

/**
 * @ClassName: TopList
 * @Description: 排行榜
 * @Date: 2022/4/19 15:44
 * @Author: fanxiaofan
 */
public class TopList {
    private Long id;
    private String name;
    private String description;
    private String coverImgUrl;
    private String updateFrequency;
    private List<Track> tracks;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "TopList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                ", updateFrequency='" + updateFrequency + '\'' +
                ", tracks=" + tracks +
                '}';
    }

    public class Track{
        private String first;
        private String second;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        @Override
        public String toString() {
            return "Track{" +
                    "first='" + first + '\'' +
                    ", second='" + second + '\'' +
                    '}';
        }
    }
}
