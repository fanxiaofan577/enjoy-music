package cn.tiancaifan.enjoy_music.bean;

/**
 * @ClassName: SearchSuggest
 * @Description: TODO
 * @Date: 2022/5/30 19:59
 * @Author: fanxiaofan
 */
public class SearchSuggest {
    private String keyword;//关键词
    private Integer type;//类型

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SearchSuggest{" +
                "keyword='" + keyword + '\'' +
                ", type=" + type +
                '}';
    }
}
