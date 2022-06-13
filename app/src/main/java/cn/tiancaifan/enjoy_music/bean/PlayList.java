package cn.tiancaifan.enjoy_music.bean;

import java.util.List;

/**
 * @ClassName: PlayList
 * @Description: 歌单实体类
 * @Date: 2022/4/8 16:13
 * @Author: fanxiaofan
 */
public class PlayList {
    private Long id;
    private String name;
    private String coverImgUrl;
    private String description;
    private User creator;
    private List<String> tags;

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

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                ", tags=" + tags +
                '}';
    }
}
