package com.knighttechapps.audiopicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.knighttechapps.audio_picker.listener.AudioPickerListener;
import com.knighttechapps.audio_picker.models.AudioModel;
import com.knighttechapps.audio_picker.ui.AudioPickerBottomSheet;
import com.knighttechapps.audiopicker.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioPickerListener{

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.selectAudio.setOnClickListener(v -> showAudioPicker());


    }

    private void showAudioPicker() {
        AudioPickerBottomSheet audioPickerBottomSheet = AudioPickerBottomSheet.newInstance(false, 1);



        audioPickerBottomSheet.show(getSupportFragmentManager(), "AudioPickerBottomSheet");
    }

    private void showPermissionDeniedDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Permission Required")
                .setMessage("Audio permission is required to select music files. You can enable it in Settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Open app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAudioSelected(List<AudioModel> audioList) {
        // Handle selected audio files
        for (AudioModel audio : audioList) {
            Log.d("AudioPicker", "Selected: " + audio.getTitle());
        }
    }

    @Override
    public void onCancel() {
        AudioPickerListener.super.onCancel();
    }

    @Override
    public void onPermissionDenied() {
        AudioPickerListener.super.onPermissionDenied();
        showPermissionDeniedDialog();
    }
}