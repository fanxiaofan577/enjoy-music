package cn.tiancaifan.enjoy_music.bean;

import java.util.Arrays;
import java.util.List;

public class SingerDetails {
    private String cover;//图片
    private String name;//歌手名
    private String briefDesc;//歌手简介
   private List<String> transNames;//翻译名
   private List<String> identities;//身份

    public List<String> getTransNames() {
        return transNames;
    }

    public void setTransNames(List<String> transNames) {
        this.transNames = transNames;
    }

    public List<String> getIdentities() {
        return identities;
    }

    public void setIdentities(List<String> identities) {
        this.identities = identities;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    @Override
    public String toString() {
        return "SingerDetails{" +
                "cover='" + cover + '\'' +
                ", name='" + name + '\'' +
                ", briefDesc='" + briefDesc + '\'' +
                ", transNames=" + transNames +
                ", identities=" + identities +
                '}';
    }
}
