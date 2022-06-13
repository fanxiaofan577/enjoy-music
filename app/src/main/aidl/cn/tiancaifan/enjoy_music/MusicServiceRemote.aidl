// MusicServiceRemote.aidl
package cn.tiancaifan.enjoy_music;

// Declare any non-default types here with import statements
import cn.tiancaifan.enjoy_music.bean.Music;

interface MusicServiceRemote {
    //播放单曲
    void play(in Music music);
    //根据position播放单曲
    void playByPosition(int position);
    //播放列表
    void playList(in List<Music> playlist);
    //下一首
    void nextMusic();
    //上一首
    void preMusic();
    //停止播放
    void stop();
    //暂停或播放
    void pauseOrStartMusic();
    //设置播放模式
    void setMode(int mode);
    //获取播放模式
    int getMode();
    //从播放列表删除歌曲
    void removeMuiscFromPlayList(int index);
    //获取player播放状态
    boolean getPlayerStatus();
    //获取播放时长
    int getDuration();
    //获取player当前时长
    int getCurrentPosition();
    //清空播放列表
    void clearPlayList();
    //调整播放进度
    void seekTo(int ms);
    //获取当前播放对象
    Music getMusic();
    //获取播放列表
    List<Music> getListMusic();
    //获取当前指针
    int getPosition();

}