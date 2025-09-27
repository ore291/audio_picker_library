
package com.knighttechapps.audio_picker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.knighttechapps.audio_picker.models.AudioModel;
import com.knighttechapps.audio_picker.providers.AudioProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudioPickerViewModel extends AndroidViewModel {
    
    private final AudioProvider audioProvider;
    private final ExecutorService executorService;
    
    // LiveData for UI state
    private final MutableLiveData<List<AudioModel>> _audioFiles = new MutableLiveData<>();
    public final LiveData<List<AudioModel>> audioFiles = _audioFiles;
    
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;
    
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;
    
    private final MutableLiveData<Set<Long>> _selectedAudioIds = new MutableLiveData<>(new HashSet<>());
    public final LiveData<Set<Long>> selectedAudioIds = _selectedAudioIds;
    
    private final MutableLiveData<Integer> _selectedCount = new MutableLiveData<>(0);
    public final LiveData<Integer> selectedCount = _selectedCount;

    // Configuration
    private boolean allowMultiSelect = true;
    private int maxSelection = -1;

    public AudioPickerViewModel(@NonNull Application application) {
        super(application);
        audioProvider = new AudioProvider(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void setConfiguration(boolean allowMultiSelect, int maxSelection) {
        this.allowMultiSelect = allowMultiSelect;
        this.maxSelection = maxSelection;
    }

    public void loadAudioFiles() {
        if (_isLoading.getValue() == Boolean.TRUE) {
            return; // Already loading
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);

        executorService.execute(() -> {
            try {
                Future<List<AudioModel>> future = audioProvider.getAllAudioFiles();
                List<AudioModel> files = future.get();
                _audioFiles.postValue(files != null ? files : new ArrayList<>());
            } catch (Exception e) {
                _error.postValue("Error loading audio files: " + e.getMessage());
            } finally {
                _isLoading.postValue(false);
            }
        });
    }

    public boolean toggleSelection(AudioModel audio) {
        Set<Long> currentSelected = _selectedAudioIds.getValue();
        if (currentSelected == null) {
            currentSelected = new HashSet<>();
        }
        
        Set<Long> newSelected = new HashSet<>(currentSelected);
        
        if (newSelected.contains(audio.getId())) {
            // Deselect
            newSelected.remove(audio.getId());
        } else {
            // Select
            if (!allowMultiSelect) {
                newSelected.clear(); // Clear previous selection for single select
            } else if (maxSelection > 0 && newSelected.size() >= maxSelection) {
                return false; // Max selection reached
            }
            newSelected.add(audio.getId());
        }
        
        _selectedAudioIds.setValue(newSelected);
        _selectedCount.setValue(newSelected.size());
        return true;
    }

    public void clearSelection() {
        _selectedAudioIds.setValue(new HashSet<>());
        _selectedCount.setValue(0);
    }

    public List<AudioModel> getSelectedAudios() {
        List<AudioModel> allAudios = _audioFiles.getValue();
        Set<Long> selectedIds = _selectedAudioIds.getValue();
        
        if (allAudios == null || selectedIds == null) {
            return new ArrayList<>();
        }
        
        List<AudioModel> selected = new ArrayList<>();
        for (AudioModel audio : allAudios) {
            if (selectedIds.contains(audio.getId())) {
                selected.add(audio);
            }
        }
        return selected;
    }

    public boolean isSelected(AudioModel audio) {
        Set<Long> selectedIds = _selectedAudioIds.getValue();
        return selectedIds != null && selectedIds.contains(audio.getId());
    }

    public boolean getAllowMultiSelect() {
        return allowMultiSelect;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (audioProvider != null) {
            audioProvider.shutdown();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}