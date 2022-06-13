package cn.tiancaifan.enjoy_music.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.tiancaifan.enjoy_music.bean.Music;

/**
 * @ClassName: DataBaseUtil
 * @Description: TODO
 * @Date: 2022/5/24 19:12
 * @Author: fanxiaofan
 */
public class DataBaseUtil {

    private DataBaseHelper musicHelper;
    private SQLiteDatabase database;


    public DataBaseUtil(DataBaseHelper musicHelper) {
        if (database == null)
            database = musicHelper.getWritableDatabase();
    }

    /**
     * 清空播放列表
     */
    public void clearPlayList(){
        database.execSQL("delete from playList;");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'playList';");
    }

    /**
     * 生成播放列表 ，生成之前会清空数据
     * @param musicList
     */
    public void addPlayList(List<Music> musicList){
        clearPlayList();
        ContentValues cv = new ContentValues();
        for (Music music : musicList) {
            cv.put("musicId",music.getId());
            cv.put("name",music.getName());
            cv.put("ar",music.getAr().get(0).getName());
            if (music.getAl()!=null){
                cv.put("picUrl",music.getAl().getPicUrl());
            }
            database.insert("playList", null, cv);
            cv.clear();
        }
    }

    /**
     * 获取当前播放列表
     * @return
     */
    public List<Music> getPlayList(){
        String sql = "select * from playList";
        Cursor cursor = database.rawQuery(sql, null);
        List<Music> musicList = new ArrayList<>();
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Music music = new Music(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                musicList.add(music);
            }
        }
        return musicList;
    }

    /**
     * 插入歌曲至播放列表
     * @param music
     * @return
     */
    public boolean insertMusicToPlayList(Music music){
        ContentValues cv = new ContentValues();
        cv.put("musicId",music.getId());
        cv.put("name",music.getName());
        cv.put("ar",music.getAr().get(0).getName());
        cv.put("picUrl",music.getAl().getPicUrl());
        long playList = database.insert("playList", null, cv);
        if (playList!=0){
            return true;
        }
        return false;
    }

    /**
     * 播放列表中的某一首歌曲
     * @param music
     */
    public void deleteMusicFromPlayListById(Music music){
        database.delete("playList", "id=？", new String[]{music.getId()+""});
    }

    /**
     * 添加音乐至到播放历史中
     * @param music
     */
    public void addMusicToHistory(Music music){
        Music m = getMusicFromHistoryByMusicId(music);
        if (m == null){
            ContentValues values = new ContentValues();
            values.put("musicId",music.getId());
            values.put("name",music.getName());
            values.put("ar",music.getAr().get(0).getName());
            values.put("picUrl",music.getAl().getPicUrl());
            database.insert("history", null, values);
        }

    }

    /**
     * 获取历史表有多少条数据
     * @return
     */
    public int getHistoryCount(){
        String sql = "select count(id) from history";
        Cursor cursor = database.rawQuery(sql, new String[]{});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 获取播放历史列表
     * @return
     */
    public List<Music> getHistory(){
        String sal = "select * from history";
        Cursor cursor = database.rawQuery(sal, null);
        List<Music> historyMusicList = new ArrayList<>();
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Music music = new Music(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                historyMusicList.add(music);
            }
        }
        return historyMusicList;
    }

    /**
     * 从历史播放中删除某一首歌曲
     * @param music
     */
    public void deleteMusicFromHistoryByMusicId(Music music){
        database.delete("history","musicId = ?",new String[]{""+music.getId()});
    }

    /**
     * 根据id获取播放历史列表中音乐
     * @param music
     */
    public Music getMusicFromHistoryByMusicId(Music music){
        String sql = "select * from history where musicId = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{"" + music.getId()});
        Music music1 = null;
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                music1 = new Music(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            }
        }
        return music1;
    }

    /**
     * 清空历史播放列表
     */
    public void clearHistory(){
        database.execSQL("delete from history;");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'history';");
    }

    /**
     * 添加歌曲至收藏列表
     * @param music
     */
    public void addMusicToCollectionMusic(Music music){
        Music m = getMusicFormCollectionMusicByMusicId(music.getId());
        if (m == null){
            ContentValues values = new ContentValues();
            values.put("musicId",music.getId());
            values.put("name",music.getName());
            values.put("ar",music.getAr().get(0).getName());
            values.put("picUrl",music.getAl().getPicUrl());
            long collectionMusic = database.insert("collectionMusic", null, values);
        }
    }

    /**
     * 从歌曲收藏列表中删除歌曲
     * @param music
     */
    public void deleteMusicFormCollectionMusicByMusicId(Music music){
        database.delete("collectionMusic","musicId = ?",new String[]{""+music.getId()});
    }

    /**
     * 从歌曲收藏列表中查询歌曲
     * @param musicId 音乐id
     */
    public Music getMusicFormCollectionMusicByMusicId(Long musicId){
        String sql = "select * from collectionMusic where musicId = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{"" + musicId});
        Music music = null;
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                music = new Music(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            }
        }
        return music;
    }

    /**
     * 从歌曲收藏列表中查询所有歌曲
     */
    public List<Music> getMusicFormCollectionMusic(){
        String sql = "select * from collectionMusic";
        Cursor cursor = database.rawQuery(sql, null);
        List<Music> collectionMusic = new ArrayList<>();
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Music music = new Music(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                collectionMusic.add(music);
            }
        }
        return collectionMusic;
    }

    /**
     * 获取收藏歌曲数量
     * @return
     */
    public int getCollectionMusicCount(){
        String sql = "select count(id) from collectionMusic";
        Cursor cursor = database.rawQuery(sql, new String[]{});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 清空歌曲收藏列表
     */
    public void clearMusicFromCollectionMusic(){
        database.execSQL("delete from collectionMusic;");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'collectionMusic';");
    }

    /**
     * 获取所有歌单
     * @return
     */
    public List<Long> getRemoteSongSheet(){
        String sql = "select * from remoteSongSheet";
        Cursor cursor = database.rawQuery(sql, null);
        List<Long> longList = new ArrayList<>();
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Long songSheet = new Long(cursor.getLong(1));
                longList.add(songSheet);
            }
        }
        return longList;
    }

    /**
     * 根据收藏歌歌单id删除收藏远程歌单列表中收藏歌单
     * @param songSheetId
     */
    public void deleteRemoteSongSheetFromCollectionSongSheetById(Long songSheetId){
        database.delete("remoteSongSheet","songSheetId = ?",new String[]{""+songSheetId});
    }

    /**
     * 查询歌单id
     * @param songSheetId
     */
    public Long getRemoteSongSheetFromCollectionSongSheetById(Long songSheetId){
        String sql = "select * from remoteSongSheet where songSheetId = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{songSheetId+""});
        Long songSheet = null;
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                songSheet = new Long(cursor.getLong(1));
            }
        }
        return songSheet;
    }

    /**
     * 添加歌收藏歌单
     * @param songSheetId
     */
    public void addRemoteSongSheetToCollectionSongSheet(Long songSheetId){
        Long temp = getRemoteSongSheetFromCollectionSongSheetById(songSheetId);
        if (temp == null){
            ContentValues values = new ContentValues();
            values.put("songSheetId",songSheetId);
            database.insert("remoteSongSheet",null,values);
        }
    }

    /**
     * 获取收藏歌单数量
     * @return
     */
    public int getRemoteSongSheetCount(){
        String sql = "select count(id) from remoteSongSheet";
        Cursor cursor = database.rawQuery(sql, new String[]{});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 添加搜索历史
     * @param keyword
     */
    public void addKeywordsToSearchHistory(String keyword){
        String sql = "select * from searchHistory where keyword = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{keyword+""});
        String temp = null;
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                temp = cursor.getString(1);
            }
        }
        if (temp == null){
            ContentValues values = new ContentValues();
            values.put("keyword",keyword);
            database.insert("searchHistory", null, values);
        }
    }

    /**
     * 清空搜索历史
     */
    public void clearSearchHistory(){
        database.execSQL("delete from searchHistory;");
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'searchHistory';");
    }

    /**
     * 获取所有搜索历史
     * @return
     */
    public List<String> getSearchHistory(){
        String sql = "select * from searchHistory";
        Cursor cursor = database.rawQuery(sql, null);
        List<String> keywords = new ArrayList<>();
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                String keyword = cursor.getString(1);
                keywords.add(keyword);
            }
        }
        return keywords;
    }





    public static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //播放列表
            String playList = "create table playList(id integer primary key autoincrement,musicId integer  ,name text,ar text,picUrl text)";
            //历史播放列表
            String history = "create table history(id integer primary key autoincrement,musicId integer  ,name text,ar text,picUrl text)";
            //收藏歌曲列表
            String collectionMusic = "create table collectionMusic(id integer primary key autoincrement,musicId integer  ,name text,ar text,picUrl text)";
            //收藏的歌单
            String collectionRemoteSongSheet = "create table remoteSongSheet (id integer primary key autoincrement,songSheetId integer)";

            String searchHistory = "create table searchHistory(id integer primary key autoincrement,keyword text)";

            db.execSQL(playList);
            db.execSQL(history);
            db.execSQL(collectionMusic);
            db.execSQL(collectionRemoteSongSheet);
            db.execSQL(searchHistory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}

