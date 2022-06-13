package cn.tiancaifan.enjoy_music.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Artist implements Parcelable,Serializable {
    private int id;
    private String name;
    private String picUrl;
    private List<String> alias;
    private int albumSize;
    private int picId;
    private String img1v1Url;
    private int img1v1;
    private String trans;

    public Artist(){}

    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picUrl = in.readString();
        alias = in.createStringArrayList();
        albumSize = in.readInt();
        picId = in.readInt();
        img1v1Url = in.readString();
        img1v1 = in.readInt();
        trans = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
    public List<String> getAlias() {
        return alias;
    }

    public void setAlbumSize(int albumSize) {
        this.albumSize = albumSize;
    }
    public int getAlbumsize() {
        return albumSize;
    }

    public void setPicid(int picId) {
        this.picId = picId;
    }
    public int getPicId() {
        return picId;
    }

    public void setImg1v1url(String img1v1url) {
        this.img1v1Url = img1v1url;
    }
    public String getImg1v1url() {
        return img1v1Url;
    }

    public void setImg1v1(int img1v1) {
        this.img1v1 = img1v1;
    }
    public int getImg1v1() {
        return img1v1;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getTrans() {
        return trans;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", alias=" + alias +
                ", albumSize=" + albumSize +
                ", picId=" + picId +
                ", img1v1Url='" + img1v1Url + '\'' +
                ", img1v1=" + img1v1 +
                ", trans='" + trans + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picUrl);
        dest.writeStringList(alias);
        dest.writeInt(albumSize);
        dest.writeInt(picId);
        dest.writeString(img1v1Url);
        dest.writeInt(img1v1);
        dest.writeString(trans);
    }
}
