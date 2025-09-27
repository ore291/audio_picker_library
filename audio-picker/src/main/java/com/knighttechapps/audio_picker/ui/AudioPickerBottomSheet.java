package com.knighttechapps.audio_picker.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.knighttechapps.audio_picker.adapter.AudioAdapter;
import com.knighttechapps.audio_picker.databinding.BottomSheetAudioPickerBinding;
import com.knighttechapps.audio_picker.listener.AudioPickerListener;
import com.knighttechapps.audio_picker.models.AudioModel;
import com.knighttechapps.audio_picker.viewmodel.AudioPickerViewModel;

import java.util.List;

public class AudioPickerBottomSheet extends BottomSheetDialogFragment {

    // Configuration keys for arguments
    private static final String ARG_ALLOW_MULTI_SELECT = "allow_multi_select";
    private static final String ARG_MAX_SELECTION = "max_selection";
    private static final String ARG_DISCLOSURE_TITLE = "disclosure_title";
    private static final String ARG_DISCLOSURE_MESSAGE = "disclosure_message";
    private static final String ARG_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String ARG_NEGATIVE_BUTTON_TEXT = "negative_button_text";
    private static final String ARG_SHOW_DISCLOSURE = "show_disclosure";

    private BottomSheetAudioPickerBinding binding;
    private AudioAdapter audioAdapter;
    private AudioPickerViewModel viewModel;
    private AudioPickerListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Automatically reconnect listener if the parent implements the interface
        if (context instanceof AudioPickerListener) {
            this.listener = (AudioPickerListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Optional: Clear listener on detach
        this.listener = null;
    }

    // Permission disclosure customization
    private String disclosureTitle = "Access Your Audio Files";
    private String disclosureMessage = "This app needs access to your audio files to let you select and process audio.";
    private String positiveButtonText = "Allow";
    private String negativeButtonText = "Don't Allow";
    private boolean showDisclosure = true;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    viewModel.loadAudioFiles();
                } else {
                    // Permission denied from system dialog
                    if (listener != null) {
                        listener.onPermissionDenied();
                    }
                    dismiss();
                }
            }
    );

    public static AudioPickerBottomSheet newInstance(boolean allowMultiSelect, int maxSelection) {
        AudioPickerBottomSheet fragment = new AudioPickerBottomSheet();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ALLOW_MULTI_SELECT, allowMultiSelect);
        args.putInt(ARG_MAX_SELECTION, maxSelection);
        fragment.setArguments(args);
        return fragment;
    }

    public static AudioPickerBottomSheet newInstance(boolean allowMultiSelect) {
        return newInstance(allowMultiSelect, -1);
    }

    public static AudioPickerBottomSheet newInstance() {
        return newInstance(true, -1);
    }

    // Builder pattern for customization
    public static class Builder {
        private boolean allowMultiSelect = true;
        private int maxSelection = -1;
        private String disclosureTitle = "Access Your Audio Files";
        private String disclosureMessage = "This app needs access to your audio files to let you select and play music. We only access files you choose and don't collect or share your personal data.";
        private String positiveButtonText = "Allow";
        private String negativeButtonText = "Don't Allow";
        private boolean showDisclosure = true;

        public Builder setAllowMultiSelect(boolean allowMultiSelect) {
            this.allowMultiSelect = allowMultiSelect;
            return this;
        }

        public Builder setMaxSelection(int maxSelection) {
            this.maxSelection = maxSelection;
            return this;
        }

        public Builder setDisclosureTitle(String title) {
            this.disclosureTitle = title;
            return this;
        }

        public Builder setDisclosureMessage(String message) {
            this.disclosureMessage = message;
            return this;
        }

        public Builder setDisclosureButtonTexts(String positiveText, String negativeText) {
            this.positiveButtonText = positiveText;
            this.negativeButtonText = negativeText;
            return this;
        }

        public Builder setShowDisclosure(boolean showDisclosure) {
            this.showDisclosure = showDisclosure;
            return this;
        }

        public AudioPickerBottomSheet build() {
            AudioPickerBottomSheet fragment = new AudioPickerBottomSheet();
            Bundle args = new Bundle();
            args.putBoolean(ARG_ALLOW_MULTI_SELECT, this.allowMultiSelect);
            args.putInt(ARG_MAX_SELECTION, this.maxSelection);
            args.putString(ARG_DISCLOSURE_TITLE, this.disclosureTitle);
            args.putString(ARG_DISCLOSURE_MESSAGE, this.disclosureMessage);
            args.putString(ARG_POSITIVE_BUTTON_TEXT, this.positiveButtonText);
            args.putString(ARG_NEGATIVE_BUTTON_TEXT, this.negativeButtonText);
            args.putBoolean(ARG_SHOW_DISCLOSURE, this.showDisclosure);
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get configuration from arguments
        Bundle args = getArguments();
        if (args != null) {
            disclosureTitle = args.getString(ARG_DISCLOSURE_TITLE, disclosureTitle);
            disclosureMessage = args.getString(ARG_DISCLOSURE_MESSAGE, disclosureMessage);
            positiveButtonText = args.getString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText);
            negativeButtonText = args.getString(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText);
            showDisclosure = args.getBoolean(ARG_SHOW_DISCLOSURE, showDisclosure);
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AudioPickerViewModel.class);

        // Configure ViewModel
        boolean allowMultiSelect = args == null || args.getBoolean(ARG_ALLOW_MULTI_SELECT, true);
        int maxSelection = args != null ? args.getInt(ARG_MAX_SELECTION, -1) : -1;
        viewModel.setConfiguration(allowMultiSelect, maxSelection);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAudioPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupButtons();
        observeViewModel();
        checkPermissionsAndLoad();
    }

    private void setupRecyclerView() {
        audioAdapter = new AudioAdapter(
                new AudioAdapter.OnAudioClickListener() {
                    @Override
                    public void onAudioClick(AudioModel audio) {
                        if (viewModel.toggleSelection(audio)) {
                            audioAdapter.notifyDataSetChanged(); // Refresh selection state

                            // For single select, immediately return result
                            if (!viewModel.getAllowMultiSelect()) {
                                if (listener != null) {
                                    listener.onAudioSelected(viewModel.getSelectedAudios());
                                }
                                dismiss();
                            }
                        }
                    }
                },
                viewModel.getAllowMultiSelect()
        );

        // Set selection checker
        audioAdapter.setSelectionChecker(audio -> viewModel.isSelected(audio));

        binding.recyclerView.setAdapter(audioAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupButtons() {
        binding.buttonDone.setOnClickListener(v -> {
            List<AudioModel> selectedAudios = viewModel.getSelectedAudios();
            if (listener != null) {
                listener.onAudioSelected(selectedAudios);
            }
            dismiss();
        });

        if (viewModel.getAllowMultiSelect()) {
            binding.textSelectedCount.setVisibility(View.VISIBLE);
            binding.buttonDone.setVisibility(View.VISIBLE);
        }
    }

    private void observeViewModel() {
        // Observe audio files
        viewModel.audioFiles.observe(getViewLifecycleOwner(), audioFiles -> {
            if (audioFiles.isEmpty()) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.textEmpty.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                audioAdapter.submitList(audioFiles);
            }
        });

        // Observe loading state
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.textEmpty.setVisibility(View.GONE);
            }
        });

        // Observe errors
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.textEmpty.setText(error);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });

        // Observe selection count
        viewModel.selectedCount.observe(getViewLifecycleOwner(), count -> {
            if (viewModel.getAllowMultiSelect()) {
                binding.textSelectedCount.setText(count + " selected");
                binding.buttonDone.setEnabled(count > 0);
            }
        });

        // Observe selected audio IDs to refresh adapter
        viewModel.selectedAudioIds.observe(getViewLifecycleOwner(), selectedIds -> {
            audioAdapter.notifyDataSetChanged();
        });
    }

    private void checkPermissionsAndLoad() {
        String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadAudioFiles();
        } else {
            if (showDisclosure) {
                showPermissionDisclosure(permission);
            } else {
                requestPermission(permission);
            }
        }
    }

    private void showPermissionDisclosure(String permission) {
        PermissionDisclosureDialog disclosureDialog = PermissionDisclosureDialog.newInstance(
                disclosureTitle,
                disclosureMessage,
                positiveButtonText,
                negativeButtonText
        );

        disclosureDialog.setPermissionDisclosureListener(new PermissionDisclosureDialog.PermissionDisclosureListener() {
            @Override
            public void onPermissionAccepted() {
                requestPermission(permission);
            }

            @Override
            public void onPermissionDenied() {
                if (listener != null) {
                    listener.onPermissionDenied();
                }
                dismiss();
            }
        });

        disclosureDialog.show(getParentFragmentManager(), "PermissionDisclosureDialog");
    }

    private void requestPermission(String permission) {
        permissionLauncher.launch(permission);
    }

    public void setAudioPickerListener(AudioPickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}