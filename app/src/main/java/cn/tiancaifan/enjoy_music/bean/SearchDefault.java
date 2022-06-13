package cn.tiancaifan.enjoy_music.bean;
/**
 * @ClassName: SearchDefault
 * @Description: 默认搜索实体类
 * @Date: 2022/4/9 21:56
 * @Author: wangjunlin
 */
public class SearchDefault {
    private String showKeyword;
    private String realkeyword;

    public String getShowKeyword() {
        return showKeyword;
    }

    public void setShowKeyword(String showKeyword) {
        this.showKeyword = showKeyword;
    }

    public String getRealkeyword() {
        return realkeyword;
    }

    public void setRealkeyword(String realkeyword) {
        this.realkeyword = realkeyword;
    }

    @Override
    public String toString() {
        return "SearchDefault{" +
                "showKeyword='" + showKeyword + '\'' +
                ", realkeyword='" + realkeyword + '\'' +
                '}';
    }
}
