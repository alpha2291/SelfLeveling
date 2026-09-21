package com.alpha.selfemployment.Views.Explore.di

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreUIViewModel : ViewModel(){

    private var _exploreSearchText = MutableStateFlow("")
    var exploreSearchText : StateFlow<String> = _exploreSearchText.asStateFlow()

    fun enterText(text: String){
        _exploreSearchText.value = text
    }

    fun clearText(){
        _exploreSearchText.value = ""
    }


}