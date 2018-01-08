package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData


// shorthand for adding source to MediatorLiveData and assigning its value - great for validators, chaining live data etc.
fun <S, T> MediatorLiveData<T>.addValueSource(source: LiveData<S>, resultFunction: (sourceValue: S?) -> T) = this.apply { addSource(source, { value = resultFunction(it) }) }