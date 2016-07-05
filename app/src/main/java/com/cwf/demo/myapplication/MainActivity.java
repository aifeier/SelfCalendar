package com.cwf.demo.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (EditText) findViewById(R.id.text);
        findViewById(R.id.btn).setOnClickListener(this);
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.KEY_URL, "http://img.tuku.cn/file_big/201503/c066e56887e24a759688b38595b91f12.jpg");
        intent.putExtra(DownloadService.KEY_NAME, System.currentTimeMillis() + ".jpg");
//        startService(intent);
        Toast.makeText(this, "" + getDayOfMonth(), Toast.LENGTH_SHORT).show();

//        downFile(this
//                , "http://img.tuku.cn/file_big/201503/c066e56887e24a759688b38595b91f12.jpg"
//                , System.currentTimeMillis() + ".jpg");
    }

    public static int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }

    private void downFile(Context mContext, String downloadUrl, String fileName) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("Download/", fileName);

        //获取下载管理器
        DownloadManager downloadManager =
                (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }


    @Override
    public void onClick(View view) {
        text.setText(getValidateCode("32012419940312321") + "");
    }

    int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
    char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值

    public char getValidateCode(String id17) {
        int sum = 0;
        int mode = 0;
        for (int i = 0; i < id17.length(); i++) {
            sum = sum + Integer.parseInt(String.valueOf(id17.charAt(i))) * weight[i];
        }
        mode = sum % 11;
        return validate[mode];
    }
}
