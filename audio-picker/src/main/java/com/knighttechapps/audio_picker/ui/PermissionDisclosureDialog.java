package com.knighttechapps.audio_picker.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.knighttechapps.audio_picker.databinding.DialogPermissionDisclosureBinding;


public class PermissionDisclosureDialog extends DialogFragment {

    public interface PermissionDisclosureListener {
        void onPermissionAccepted();
        void onPermissionDenied();
    }

    private DialogPermissionDisclosureBinding binding;
    private PermissionDisclosureListener listener;
    private String title = "Access Your Audio Files";
    private String message = "This app needs access to your audio files to let you select and process audio.";
    private String positiveButtonText = "Allow";
    private String negativeButtonText = "Don't Allow";

    public static PermissionDisclosureDialog newInstance() {
        return new PermissionDisclosureDialog();
    }

    public static PermissionDisclosureDialog newInstance(String title, String message) {
        PermissionDisclosureDialog dialog = new PermissionDisclosureDialog();
        dialog.title = title;
        dialog.message = message;
        return dialog;
    }

    public static PermissionDisclosureDialog newInstance(String title, String message, 
                                                       String positiveText, String negativeText) {
        PermissionDisclosureDialog dialog = new PermissionDisclosureDialog();
        dialog.title = title;
        dialog.message = message;
        dialog.positiveButtonText = positiveText;
        dialog.negativeButtonText = negativeText;
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

            // 👇 Make window background transparent
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        binding = DialogPermissionDisclosureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupDialog();
        setupClickListeners();
    }

    private void setupDialog() {
        binding.textTitle.setText(title);
        binding.textMessage.setText(message);
        binding.buttonAllow.setText(positiveButtonText);
        binding.buttonDeny.setText(negativeButtonText);
        
        // Make dialog not cancelable by back button or outside touch
        setCancelable(false);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    private void setupClickListeners() {
        binding.buttonAllow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPermissionAccepted();
            }
            dismiss();
        });

        binding.buttonDeny.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPermissionDenied();
            }
            dismiss();
        });
    }

    public void setPermissionDisclosureListener(PermissionDisclosureListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}