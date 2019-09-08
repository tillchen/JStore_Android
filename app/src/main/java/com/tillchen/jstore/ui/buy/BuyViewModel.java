package com.tillchen.jstore.ui.buy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BuyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BuyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is buy fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    
}