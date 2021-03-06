package cn.tiancaifan.enjoy_music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.tiancaifan.enjoy_music.R;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadListener;
import cn.tiancaifan.enjoy_music.utils.download.DownLoadManager;
import cn.tiancaifan.enjoy_music.utils.download.TaskInfo;
import cn.tiancaifan.enjoy_music.utils.download.dbcontrol.bean.SQLDownLoadInfo;

/**
 * @ClassName: DownloadingAdapter
 * @Description: TODO
 * @Date: 2022/6/8 21:49
 * @Author: fanxiaofan
 */
public class DownloadingAdapter extends BaseAdapter {
    private ArrayList<TaskInfo> listdata = new  ArrayList<TaskInfo>();
    private Context mcontext;
    private DownLoadManager downLoadManager;
    public DownloadingAdapter(Context context,DownLoadManager downLoadManager){
        this.mcontext = context;
        this.downLoadManager = downLoadManager;
        listdata = downLoadManager.getAllTask();
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            holder = new Holder();
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.download_listview_item, null);
            holder.fileName = (TextView)convertView.findViewById(R.id.file_name);
            holder.textProgress = (TextView)convertView.findViewById(R.id.file_size);
            holder.fileProgress = (ProgressBar)convertView.findViewById(R.id.progressbar);
            holder.downloadIcon = (CheckBox)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }
        holder.fileName.setText(listdata.get(position).getFileName());
        holder.fileProgress.setProgress(listdata.get(position).getProgress());
        holder.textProgress.setText(listdata.get(position).getProgress() + "%");
        holder.downloadIcon.setOnCheckedChangeListener(new CheckedChangeListener(position));
        if(listdata.get(position).isOnDownloading()){
            holder.downloadIcon.setChecked(true);
        }else{
            holder.downloadIcon.setChecked(false);
        }
        return convertView;
    }

    static class Holder {
        TextView fileName = null;
        TextView textProgress = null;
        ProgressBar fileProgress = null;
        CheckBox downloadIcon = null;
    }

    public void addItem(TaskInfo taskinfo){
        this.listdata.add(taskinfo);
        this.notifyDataSetInvalidated();
    }

    public void setListdata(ArrayList<TaskInfo> listdata){
        this.listdata = listdata;
        this.notifyDataSetInvalidated();
    }

    class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int position = 0;
        public CheckedChangeListener(int position){
            this.position = position;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                // ????????????
                listdata.get(position).setOnDownloading(true);
                downLoadManager.startTask(listdata.get(position).getTaskID());
            }else{
                //????????????
                listdata.get(position).setOnDownloading(false);
                downLoadManager.stopTask(listdata.get(position).getTaskID());
            }
            DownloadingAdapter.this.notifyDataSetChanged();
        }

    }

    private class DownloadManagerListener implements DownLoadListener {

        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {

        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            //????????????????????????????????????????????????????????????????????????????????????
            for(TaskInfo taskInfo : listdata){
                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
                    DownloadingAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            //??????????????????????????????????????????????????????????????????????????????
            for(TaskInfo taskInfo : listdata){
                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
                    listdata.remove(taskInfo);
                    DownloadingAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            //????????????????????????????????????????????????????????????????????????
            for(TaskInfo taskInfo : listdata){
                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
                    taskInfo.setOnDownloading(false);
                    DownloadingAdapter.this.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}

