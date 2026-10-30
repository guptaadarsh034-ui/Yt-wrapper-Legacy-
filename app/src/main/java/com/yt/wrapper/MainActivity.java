package com.yt.wrapper;

import android.app.Activity;
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String PREFS = "yt_legacy_downloads";
    private static final String IDS = "download_ids";
    private DownloadManager downloadManager;
    private EditText urlInput;
    private EditText nameInput;
    private LinearLayout downloadList;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable refreshTask;
    private boolean activityResumed;
    private String lastDownloadsSnapshot;
    private final List<Long> downloadIds = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        loadIds();
        buildUi();
        refreshDownloads();
        refreshTask = new Runnable() {
            @Override public void run() {
                refreshDownloads();
                if (activityResumed) handler.postDelayed(this, 4000);
            }
        };
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(20));
        root.setBackgroundColor(0xff101318);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("YT LEGACY");
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(0xffffffff);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Lightweight MP4 downloader • Android 4.4+");
        subtitle.setTextSize(13);
        subtitle.setTextColor(0xffb7c0cc);
        LinearLayout.LayoutParams subParams = params(-1, -2);
        subParams.bottomMargin = dp(18);
        root.addView(subtitle, subParams);

        TextView urlLabel = label("Direct MP4 video URL");
        root.addView(urlLabel);
        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setTextSize(14);
        urlInput.setHint("https://example.com/video.mp4");
        urlInput.setHintTextColor(0xff7d8794);
        urlInput.setTextColor(0xffffffff);
        urlInput.setSelectAllOnFocus(false);
        urlInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        styleInput(urlInput);
        root.addView(urlInput, params(-1, dp(52)));

        TextView nameLabel = label("Save as (MP4)");
        LinearLayout.LayoutParams labelParams = params(-1, -2);
        labelParams.topMargin = dp(10);
        root.addView(nameLabel, labelParams);
        nameInput = new EditText(this);
        nameInput.setSingleLine(true);
        nameInput.setText("video.mp4");
        nameInput.setTextSize(14);
        nameInput.setTextColor(0xffffffff);
        nameInput.setHintTextColor(0xff7d8794);
        nameInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        styleInput(nameInput);
        root.addView(nameInput, params(-1, dp(52)));

        Button download = new Button(this);
        download.setText("DOWNLOAD VIDEO");
        LinearLayout.LayoutParams downloadParams = params(-1, dp(52));
        downloadParams.topMargin = dp(14);
        root.addView(download, downloadParams);
        download.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { startDownload(); }
        });

        Button browse = new Button(this);
        browse.setText("BROWSE YOUTUBE  •  m.youtube.com");
        LinearLayout.LayoutParams browseParams = params(-1, dp(48));
        browseParams.topMargin = dp(4);
        root.addView(browse, browseParams);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new android.content.Intent(MainActivity.this, BrowserActivity.class));
            }
        });

        Button shorts = new Button(this);
        shorts.setText("OPEN SHORTS FEED");
        LinearLayout.LayoutParams shortsParams = params(-1, dp(48));
        shortsParams.topMargin = dp(4);
        root.addView(shorts, shortsParams);
        shorts.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra("start_url", "shorts");
                startActivity(intent);
            }
        });

        TextView note = new TextView(this);
        note.setText("Direct, downloadable MP4 links only. YouTube page links and protected/streaming URLs are not extracted. Downloads are saved in Movies/YT Legacy so your Gallery or built-in video player can find them.");
        note.setTextSize(12);
        note.setTextColor(0xffb7c0cc);
        LinearLayout.LayoutParams noteParams = params(-1, -2);
        noteParams.topMargin = dp(10);
        noteParams.bottomMargin = dp(22);
        root.addView(note, noteParams);

        TextView downloadsTitle = new TextView(this);
        downloadsTitle.setText("DOWNLOADS");
        downloadsTitle.setTextSize(17);
        downloadsTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        downloadsTitle.setTextColor(0xffffffff);
        root.addView(downloadsTitle);

        downloadList = new LinearLayout(this);
        downloadList.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listParams = params(-1, -2);
        listParams.topMargin = dp(8);
        root.addView(downloadList, listParams);

        setContentView(scroll);
    }

    private void styleInput(EditText input) {
        input.setPadding(dp(10), dp(4), dp(10), dp(4));
        input.setBackgroundColor(0xff252b34);
    }

    private TextView label(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(13);
        view.setTextColor(0xffdce3ec);
        return view;
    }

    private void startDownload() {
        String rawUrl = urlInput.getText().toString().trim();
        String filename = nameInput.getText().toString().trim();

        if (rawUrl.length() == 0) {
            toast("Paste a direct MP4 URL first.");
            return;
        }
        if (!filename.toLowerCase(java.util.Locale.US).endsWith(".mp4")) {
            toast("Use an .mp4 filename for Gallery compatibility.");
            return;
        }
        if (filename.contains("/") || filename.contains("\\") || filename.equals(".mp4")) {
            toast("Enter a simple filename such as clip.mp4.");
            return;
        }

        try {
            URL parsed = new URL(rawUrl);
            String protocol = parsed.getProtocol();
            String host = parsed.getHost() == null ? "" : parsed.getHost().toLowerCase(java.util.Locale.US);
            if (!"https".equalsIgnoreCase(protocol) && !"http".equalsIgnoreCase(protocol)) {
                toast("Only HTTP/HTTPS video links are supported.");
                return;
            }
            if (host.equals("youtube.com") || host.endsWith(".youtube.com") ||
                host.equals("youtu.be") || host.endsWith(".youtu.be") ||
                host.equals("googlevideo.com") || host.endsWith(".googlevideo.com")) {
                toast("YouTube page/stream links cannot be downloaded by this tool.");
                return;
            }
        } catch (Exception e) {
            toast("That URL doesn't look valid.");
            return;
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(rawUrl));
            request.setTitle(filename);
            request.setDescription("YT Legacy • saving to Movies/YT Legacy");
            request.setMimeType("video/mp4");
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "YT Legacy/" + filename);
            long id = downloadManager.enqueue(request);
            downloadIds.add(id);
            saveIds();
            urlInput.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
            toast("Download queued. Android will show progress in notifications.");
            refreshDownloads();
        } catch (Exception e) {
            toast("Couldn't start download. Check storage space and the URL.");
        }
    }

    private void refreshDownloads() {
        if (downloadList == null) return;

        // Avoid rebuilding the entire view tree on every poll when nothing changed.
        // This reduces flicker, layout work and short-lived allocations on low-RAM phones.
        String snapshot = getDownloadsSnapshot();
        if (snapshot.equals(lastDownloadsSnapshot)) return;
        lastDownloadsSnapshot = snapshot;
        downloadList.removeAllViews();
        if (downloadIds.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No downloads yet.");
            empty.setTextColor(0xffb7c0cc);
            empty.setTextSize(13);
            downloadList.addView(empty);
            return;
        }

        for (int i = downloadIds.size() - 1; i >= 0; i--) {
            final long id = downloadIds.get(i);
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
            Cursor cursor = null;
            String title = "Video download";
            String state = "Status unavailable";
            int progress = 0;
            boolean finished = false;
            try {
                cursor = downloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int titleCol = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                    if (titleCol >= 0) title = cursor.getString(titleCol);
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (total > 0) progress = (int) Math.min(100, (downloaded * 100L) / total);
                    if (status == DownloadManager.STATUS_PENDING) state = "Waiting";
                    else if (status == DownloadManager.STATUS_RUNNING) state = "Downloading • " + progress + "%";
                    else if (status == DownloadManager.STATUS_PAUSED) state = "Paused by system";
                    else if (status == DownloadManager.STATUS_SUCCESSFUL) { state = "Complete • Movies/YT Legacy"; finished = true; }
                    else if (status == DownloadManager.STATUS_FAILED) state = "Failed • check link/network";
                }
            } catch (Exception ignored) {
                state = "Status unavailable";
            } finally {
                if (cursor != null) cursor.close();
            }

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(10), dp(10), dp(10), dp(10));
            row.setBackgroundColor(0xff1b2028);
            LinearLayout.LayoutParams rowParams = params(-1, -2);
            rowParams.bottomMargin = dp(7);
            downloadList.addView(row, rowParams);

            TextView titleView = new TextView(this);
            titleView.setText(title);
            titleView.setTextColor(0xffffffff);
            titleView.setTextSize(14);
            titleView.setMaxLines(2);
            row.addView(titleView);

            TextView stateView = new TextView(this);
            stateView.setText(state);
            stateView.setTextColor(0xffb7c0cc);
            stateView.setTextSize(12);
            LinearLayout.LayoutParams stateParams = params(-1, -2);
            stateParams.topMargin = dp(3);
            row.addView(stateView, stateParams);

            if (!finished && !state.startsWith("Failed")) {
                Button cancel = new Button(this);
                cancel.setText("CANCEL DOWNLOAD");
                LinearLayout.LayoutParams cancelParams = params(-1, dp(42));
                cancelParams.topMargin = dp(4);
                row.addView(cancel, cancelParams);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        downloadManager.remove(id);
                        downloadIds.remove(Long.valueOf(id));
                        saveIds();
                        refreshDownloads();
                    }
                });
            }
        }
    }

    private String getDownloadsSnapshot() {
        StringBuilder snapshot = new StringBuilder();
        for (int i = downloadIds.size() - 1; i >= 0; i--) {
            long id = downloadIds.get(i).longValue();
            Cursor cursor = null;
            snapshot.append(id).append(':');
            try {
                cursor = downloadManager.query(new DownloadManager.Query().setFilterById(id));
                if (cursor != null && cursor.moveToFirst()) {
                    int titleCol = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                    int statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int downloadedCol = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    int totalCol = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    if (titleCol >= 0) snapshot.append(cursor.getString(titleCol));
                    if (statusCol >= 0) snapshot.append('|').append(cursor.getInt(statusCol));
                    if (downloadedCol >= 0 && totalCol >= 0) {
                        long downloaded = cursor.getLong(downloadedCol);
                        long total = cursor.getLong(totalCol);
                        int percent = total > 0 ? (int) Math.min(100, (downloaded * 100L) / total) : 0;
                        // The visible UI only shows whole percentages, so byte-level
                        // changes do not need to trigger a redraw.
                        snapshot.append('|').append(percent);
                    }
                } else {
                    snapshot.append("missing");
                }
            } catch (Exception ignored) {
                snapshot.append("unavailable");
            } finally {
                if (cursor != null) cursor.close();
            }
            snapshot.append(';');
        }
        return snapshot.toString();
    }

    private void loadIds() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String raw = prefs.getString(IDS, "");
        if (raw.length() == 0) return;
        String[] values = raw.split(",");
        for (String value : values) {
            try { downloadIds.add(Long.parseLong(value)); } catch (NumberFormatException ignored) { }
        }
    }

    private void saveIds() {
        StringBuilder raw = new StringBuilder();
        for (Long id : downloadIds) {
            if (raw.length() > 0) raw.append(',');
            raw.append(id.longValue());
        }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(IDS, raw.toString()).commit();
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override protected void onResume() {
        super.onResume();
        activityResumed = true;
        if (refreshTask != null) {
            handler.removeCallbacks(refreshTask);
            handler.postDelayed(refreshTask, 4000);
        }
    }

    @Override protected void onPause() {
        activityResumed = false;
        if (refreshTask != null) handler.removeCallbacks(refreshTask);
        super.onPause();
    }

    @Override protected void onDestroy() {
        activityResumed = false;
        if (refreshTask != null) handler.removeCallbacks(refreshTask);
        super.onDestroy();
    }
}
