package com.knighttechapps.audio_picker.listener;


import com.knighttechapps.audio_picker.models.AudioModel;

import java.util.List;

public interface AudioPickerListener {
    void onAudioSelected(List<AudioModel> audioList);
    
    default void onCancel() {
        // Default empty implementation
    }

    default void onPermissionDenied() {
        // Default empty implementation
    }
}