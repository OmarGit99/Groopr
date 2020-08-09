package com.example.groopr.ui.minimap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MinimapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MinimapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is minimap fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}