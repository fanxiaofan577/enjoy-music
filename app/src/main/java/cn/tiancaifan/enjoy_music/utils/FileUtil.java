package cn.tiancaifan.enjoy_music.utils;

import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: FileUtil
 * @Description: TODO 本地文件工具
 * @Date: 2022/6/7 11:17
 * @Author: fanxiaofan
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 获取音乐文件路径
     * @return
     */
    public static String getMusicPath() {
        String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"enjoyMusic";
        return musicPath;
    }

    /**
     * 读取获得音乐的路径下所有的文件名
     *
     * @return fileNameList
     */
    public static List<String> getAllFileName() {
        List<String> fileNameList = new ArrayList<>();
        File file = new File(getMusicPath());
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                try {
                    fileNameList.add(new String(subFile.getName().getBytes(),"utf-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileNameList;
    }


    /**
     * 删除文件
     *
     * @param path path
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * 是否有足够大小的空间存储下载的文件
     * @param file 下载文件
     * @return
     */
    public static boolean isEnoughSpace(File file) {
        //判断剩余的内存大小,存储大容量的文件时使用
        return Environment.getDataDirectory().getUsableSpace() > file.length();
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
