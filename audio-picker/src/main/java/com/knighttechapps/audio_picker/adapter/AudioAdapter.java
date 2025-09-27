package com.knighttechapps.audio_picker.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.knighttechapps.audio_picker.databinding.ItemAudioBinding;
import com.knighttechapps.audio_picker.models.AudioModel;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    public interface OnAudioClickListener {
        void onAudioClick(AudioModel audio);
    }

    public interface SelectionChecker {
        boolean isSelected(AudioModel audio);
    }

    private final List<AudioModel> audioList;
    private final OnAudioClickListener onAudioClickListener;
    private final boolean allowMultiSelect;
    private SelectionChecker selectionChecker;

    public AudioAdapter(OnAudioClickListener listener, boolean allowMultiSelect) {
        this.audioList = new ArrayList<>();
        this.onAudioClickListener = listener;
        this.allowMultiSelect = allowMultiSelect;
    }

    public void setSelectionChecker(SelectionChecker checker) {
        this.selectionChecker = checker;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAudioBinding binding = ItemAudioBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
      if(!allowMultiSelect){
          binding.checkboxSelect.setVisibility(ViewGroup.GONE);
      }
        return new AudioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioModel audio = audioList.get(position);
        holder.bind(audio);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public void submitList(List<AudioModel> newList) {
        audioList.clear();
        if (newList != null) {
            audioList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // Keep this method for backward compatibility, but it now delegates to ViewModel
    public List<AudioModel> getSelectedAudios() {
        List<AudioModel> selected = new ArrayList<>();
        if (selectionChecker != null) {
            for (AudioModel audio : audioList) {
                if (selectionChecker.isSelected(audio)) {
                    selected.add(audio);
                }
            }
        }
        return selected;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private final ItemAudioBinding binding;

        public AudioViewHolder(@NonNull ItemAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AudioModel audio) {
            // Set basic info

            binding.textTitle.setText(audio.getTitle());
            binding.textArtist.setText(audio.getArtist());
            binding.textDuration.setText(audio.getDurationString());
            binding.textSize.setText(audio.getSizeString());

            // Get selection state from ViewModel via selectionChecker
            boolean isSelected = selectionChecker != null && selectionChecker.isSelected(audio);
            updateSelectionState(isSelected);

            // Handle root click
            binding.getRoot().setOnClickListener(v -> {
                if (onAudioClickListener != null) {
                    onAudioClickListener.onAudioClick(audio);
                }
            });

            // Handle checkbox click (same as root click)
            binding.checkboxSelect.setOnClickListener(v -> {
                if (onAudioClickListener != null) {
                    onAudioClickListener.onAudioClick(audio);
                }
            });
        }

        public void updateSelectionState(boolean isSelected) {
            binding.checkboxSelect.setChecked(isSelected);

            // Enable/disable checkbox based on selection rules
            if (allowMultiSelect) {
                binding.checkboxSelect.setEnabled(true);
            } else {
                // For single select, only enable if this item is selected or nothing is selected
                boolean hasAnySelection = hasAnySelectedItem();
                binding.checkboxSelect.setEnabled(isSelected || !hasAnySelection);
            }
        }

        private boolean hasAnySelectedItem() {
            if (selectionChecker == null) return false;

            for (AudioModel audio : audioList) {
                if (selectionChecker.isSelected(audio)) {
                    return true;
                }
            }
            return false;
        }
    }

    // Method to refresh selection states without full data reload
    public void refreshSelections() {
        notifyDataSetChanged();
    }

    // Method to refresh a specific item's selection state
    public void refreshItemSelection(AudioModel audio) {
        int position = audioList.indexOf(audio);
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }
}