package cn.tiancaifan.enjoy_music.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalMusic extends Music implements Parcelable {

    private String singer;
    private File file;

    public LocalMusic(String name, String singer, File file) {
        super.setName(name);
        this.singer = singer;
        this.file = file;
        List<Artist> artists = new ArrayList<>();
        Artist artist = new Artist();
        artist.setName(singer);
        artists.add(artist);
        super.setAr(artists);
    }

    protected LocalMusic(Parcel in) {
        singer = in.readString();
    }

    public static final Creator<LocalMusic> CREATOR = new Creator<LocalMusic>() {
        @Override
        public LocalMusic createFromParcel(Parcel in) {
            return new LocalMusic(in);
        }

        @Override
        public LocalMusic[] newArray(int size) {
            return new LocalMusic[size];
        }
    };


    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        List<Artist> artists = new ArrayList<>();
        Artist artist = new Artist();
        artist.setName(singer);
        artists.add(artist);
        super.setAr(artists);
        this.singer = singer;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "LocalMusic{" +
                ", singer='" + singer + '\'' +
                ", file=" + file +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(singer);
    }
}
