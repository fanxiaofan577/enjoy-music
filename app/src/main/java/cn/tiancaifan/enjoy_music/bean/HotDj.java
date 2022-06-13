package cn.tiancaifan.enjoy_music.bean;

public class HotDj {
    private String name;//热门电台名
    private String picUrl;//热门电台图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return "HotDj{" +
                "name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}
