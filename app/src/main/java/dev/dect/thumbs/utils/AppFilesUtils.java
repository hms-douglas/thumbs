package dev.dect.thumbs.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import dev.dect.thumbs.R;

public class AppFilesUtils {
    private static final String TAG = AppFilesUtils.class.getSimpleName();

    public static File getAppFolder(Context ctx) {
        return createFolder(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            ctx.getString(R.string.app_name)
        );
    }

    public static File getCachedSampleFile(Context ctx) {
        final int resIdRawVideo = R.raw.bigbuckbunny_320x180_24fps; //change the example in string in case of change

        final File file = new File(ctx.getCacheDir(), ctx.getResources().getResourceEntryName(resIdRawVideo) + ".mp4");

        if(!file.exists()) {
            try {
                final InputStream inputStream = ctx.getResources().openRawResourceFd(resIdRawVideo).createInputStream();

                final FileOutputStream fileOutputStream = new FileOutputStream(file);

                final byte[] buffer = new byte[1024];

                int read;

                while((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }

                inputStream.close();

                fileOutputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "getCachedSampleFile: " + e.getMessage());
            }
        }

        return file;
    }

    public static File createFile(Context ctx, String name, String extension) {
        final File folder = getAppFolder(ctx);

        File file = new File(folder, name + "." + extension);

        int i = 0;

        while(file.exists()) {
            file = new File(folder, name + " (" + ++i + ")" + "." + extension);
        }

        return file;
    }

    public static void notifyMediaScanner(Context ctx, File f) {
        notifyMediaScanner(ctx, f.getAbsolutePath());
    }

    public static void notifyMediaScanner(Context ctx, String path) {
        try {
            MediaScannerConnection.scanFile(ctx, new String[]{path}, null, null);
        } catch (Exception e) {
            Log.e(TAG, "notifyMediaScanner: " + e.getMessage());
        }
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    private static File createFolder(File parent, String name) {
        final File folder = new File(parent, name);

        if(!folder.exists()) {
            try {
                folder.mkdirs();
            } catch (Exception ignore) {}
        }

        return folder;
    }
}
