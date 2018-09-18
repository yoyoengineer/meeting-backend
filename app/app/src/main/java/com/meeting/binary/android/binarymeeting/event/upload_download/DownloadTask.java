package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;


    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    private Context context;

    private String fileName;

    public DownloadTask(DownloadListener listener, Context context, String fileName) {
        this.listener = listener;
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0; // 记录已下载的文件长度
            String downloadUrl = params[0];
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory +"/"+ fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl, context);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                // 已下载字节和文件总字节相等，说明已经下载完成了
                return TYPE_SUCCESS;
            }
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            List<Interceptor> in = httpClient.interceptors();
            boolean addInterceptor = true;
            for(Interceptor i: in){
                if(i instanceof AddCookiesInterceptor){
                    addInterceptor = false;
                    break;
                }
            }
            if(addInterceptor) {
                httpClient.addInterceptor(new AddCookiesInterceptor(context));
            }
            OkHttpClient client = httpClient.build();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    //.addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .addHeader("Cookie", CookiePreferences.getStoredCookie(context))
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength); // 跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if(isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }


    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl, Context context) throws IOException {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        List<Interceptor> in = httpClient.interceptors();
        boolean addInterceptor = true;
        for(Interceptor i: in){
            if(i instanceof AddCookiesInterceptor){
                addInterceptor = false;
                break;
            }
        }
        if(addInterceptor) {
            httpClient.addInterceptor(new AddCookiesInterceptor(context));
        }

        OkHttpClient client = httpClient.build();
        Request request = new Request.Builder()
                .addHeader("Cookie", CookiePreferences.getStoredCookie(context))
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

}