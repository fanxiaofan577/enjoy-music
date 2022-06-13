package cn.tiancaifan.enjoy_music.bean;

/**
 * @ClassName: HotSearch
 * @Description: 热搜实体类
 * @Date: 2022/4/8 20:56
 * @Author: wangjunlin
 */
public class HotSearch {
    private String searchWord;
    private String iconUrl;
    private Integer score;

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "HotSearch{" +
                "searchWord='" + searchWord + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", score=" + score +
                '}';
    }
}
