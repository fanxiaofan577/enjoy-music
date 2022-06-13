package cn.tiancaifan.enjoy_music.bean;


public class BannerBean {
    //Id
    private Long targetId;
    //图片url
    private String pic;
    //title
    private String tyoeTitle;
    //类型id 1 单曲 10 专辑 7001 直播
    private int targetType;

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTyoeTitle() {
        return tyoeTitle;
    }

    public void setTyoeTitle(String tyoeTitle) {
        this.tyoeTitle = tyoeTitle;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "targetId=" + targetId +
                ", pic='" + pic + '\'' +
                ", tyoeTitle='" + tyoeTitle + '\'' +
                ", targetType=" + targetType +
                '}';
    }
}
