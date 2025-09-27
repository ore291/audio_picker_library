package com.knighttechapps.audio_picker.providers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.knighttechapps.audio_picker.models.AudioModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudioProvider {
    private Context context;
    private ExecutorService executor;

    public AudioProvider(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Future<List<AudioModel>> getAllAudioFiles() {
        return executor.submit(new Callable<List<AudioModel>>() {
            @Override
            public List<AudioModel> call() throws Exception {
                return loadAudioFiles();
            }
        });
    }

    private List<AudioModel> loadAudioFiles() {
        List<AudioModel> audioList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DISPLAY_NAME
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
        );

        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    String artist = cursor.getString(artistColumn);
                    String album = cursor.getString(albumColumn);
                    long duration = cursor.getLong(durationColumn);
                    long size = cursor.getLong(sizeColumn);
                    long dateAdded = cursor.getLong(dateColumn);

                    if (title == null) title = "Unknown";
                    if (artist == null || artist.equals("<unknown>")) artist = "";
                    if (album == null) album = "Unknown Album";

                    if (displayName != null) {
                        title = displayName;
                    }

                    Uri uri = Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            String.valueOf(id)
                    );


                    audioList.add(new AudioModel(
                            id, title, artist, album, duration, uri, size, dateAdded
                    ));
                }
            } finally {
                cursor.close();
            }
        }

        return audioList;
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}