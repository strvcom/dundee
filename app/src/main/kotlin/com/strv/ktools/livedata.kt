package com.strv.ktools

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations

// shorthand for adding source to MediatorLiveData and assigning its value - great for validators, chaining live data etc.
fun <S, T> MediatorLiveData<T>.addValueSource(source: LiveData<S>, resultFunction: (sourceValue: S?) -> T) = this.apply { addSource(source, { value = resultFunction(it) }) }

fun <S, T> LiveData<T>.map(mapFunction: (T) -> S) = Transformations.map(this, mapFunction)
fun <S, T> LiveData<T>.switchMap(switchMapFunction: (T) -> LiveData<S>) = Transformations.switchMap(this, switchMapFunction)