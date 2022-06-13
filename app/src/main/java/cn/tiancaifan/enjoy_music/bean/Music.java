package cn.tiancaifan.enjoy_music.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//歌曲实体类
public class Music implements Parcelable,Serializable {
    private long id;
    //歌曲名
    private String name;
    //艺术家
    private List<Artist> ar;

    private List<Artist> artists;
    //专辑
    private Album al,album;
    //持续时间
    private int duration;

    private int copyrightId;
    private int status;
    private List<String> alias;
    private int rtype;
    private int ftype;
    private int mvid;
    private int fee;
    private String rUrl;
    private int mark;

    public Music(){}

    public Music(long id, String name, String ar, String picUrl) {
        this.id = id;
        this.name = name;
        Artist artist = new Artist();
        artist.setName(ar);
        ArrayList<Artist> artists = new ArrayList<>();
        artists.add(artist);
        Album album = new Album();
        album.setPicUrl(picUrl);
        this.al = album;
        this.ar = artists;
    }

    protected Music(Parcel in) {
        id = in.readLong();
        name = in.readString();
        duration = in.readInt();
        copyrightId = in.readInt();
        status = in.readInt();
        alias = in.createStringArrayList();
        rtype = in.readInt();
        ftype = in.readInt();
        mvid = in.readInt();
        fee = in.readInt();
        rUrl = in.readString();
        mark = in.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public List<Artist> getArtists() {
        if (ar!=null)
            this.artists = ar;
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAr(List<Artist> ar) {
        this.ar = ar;
    }
    public List<Artist> getAr() {
        if (artists!=null)
            this.ar = artists;
        return ar;
    }

    public void setAl(Album al) {
        this.al = al;
    }
    public Album getAl() {
        if (album!=null)
            al = album;
        return al;
    }

    public Album getAlbum() {
        if (al!=null)
            album = al;
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return duration;
    }

    public void setCopyrightId(int copyrightId) {
        this.copyrightId = copyrightId;
    }
    public int getCopyrightId() {
        return copyrightId;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }
    public List<String> getAlias() {
        return alias;
    }

    public void setRtype(int rtype) {
        this.rtype = rtype;
    }
    public int getRtype() {
        return rtype;
    }

    public void setFtype(int ftype) {
        this.ftype = ftype;
    }
    public int getFtype() {
        return ftype;
    }

    public void setMvid(int mvid) {
        this.mvid = mvid;
    }
    public int getMvid() {
        return mvid;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }
    public int getFee() {
        return fee;
    }

    public void setRUrl(String rUrl) {
        this.rUrl = rUrl;
    }
    public String getRUrl() {
        return rUrl;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
    public int getMark() {
        return mark;
    }
    public static List<Music> toMusicArrayList(JsonArray songs){
        List<Music> musics = new Gson().fromJson(songs, new TypeToken<List<Music>>() {}.getType());
        return musics;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artists=" + ar +
                ", album=" + al +
                ", duration=" + duration +
                ", copyrightId=" + copyrightId +
                ", status=" + status +
                ", alias=" + alias +
                ", rtype=" + rtype +
                ", ftype=" + ftype +
                ", mvid=" + mvid +
                ", fee=" + fee +
                ", rUrl='" + rUrl + '\'' +
                ", mark=" + mark +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(duration);
        dest.writeInt(copyrightId);
        dest.writeInt(status);
        dest.writeStringList(alias);
        dest.writeInt(rtype);
        dest.writeInt(ftype);
        dest.writeInt(mvid);
        dest.writeInt(fee);
        dest.writeString(rUrl);
        dest.writeInt(mark);
    }
}
