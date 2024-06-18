package com.example.movieapp.Services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.movieapp.R;

public class DownloadReceiver extends BroadcastReceiver {
    private Button download_btn;
    public DownloadReceiver(Button download_btn, Context context){
        this.download_btn = download_btn;
        this.download_btn.setText("Downloading");
        this.download_btn.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.downloading_icon), null,null);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1) {
                download_btn.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.download_done_icon), null,null);
                download_btn.setClickable(false);
                download_btn.setText("Downloaded");
            }
        }
    }
}
