package cn.tiancaifan.enjoy_music.bean;

/**
 * @ClassName: User
 * @Description: 用户的实体类；
 * @Date: 2022/4/17 17:55
 * @Author: fanxiaofan
 */
public class User {
    //用户id
    private Long id;
    //用户昵称
    private String nickname;
    //用户头像
    private String avatarUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nikename) {
        this.nickname = nikename;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
