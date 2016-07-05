package com.cwf.demo.myapplication;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;

/**
 * Created at 陈 on 2016/7/4.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class DownloadService extends Service {
    private final String TAG = "DownloadService";
    public static final String KEY_NAME = "file_name";
    public static final String KEY_URL = "download_url";
    private String url;
    private String name;
    private long mTaskId;
    private DownloadManager downloadManager;

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //获取下载管理器
        downloadManager =
                (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.e("TAG", ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.e("TAG", ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.e("TAG", ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.e("TAG", ">>>下载完成");
                    try {
                        downloadManager.openDownloadedFile(mTaskId);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //下载完成安装APK
//                    downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.e("TAG", ">>>下载失败");
                    break;
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        name = intent.getStringExtra(KEY_NAME);
        url = intent.getStringExtra(KEY_URL);
        downFile(url, name);
        return super.onStartCommand(intent, flags, startId);
    }

    private void downFile(String downloadUrl, String fileName) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("Download/", fileName);
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        //在通知栏中显示，默认就是显示的
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //获取下载管理器
//        downloadManager =
//                (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
        registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
