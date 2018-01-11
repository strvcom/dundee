package com.strv.ktools

import android.os.Parcel
import android.os.Parcelable
import java.util.*

// Source: @link:https://medium.com/@BladeCoder/reducing-parcelable-boilerplate-code-using-kotlin-741c3124a49a

interface KParcelable : Parcelable {
	override fun describeContents() = 0
//	override fun writeToParcel(dest: Parcel, flags: Int)
}

inline fun <reified T> parcelableCreator(crossinline create: (Parcel) -> T) = object : Parcelable.Creator<T> {
	override fun createFromParcel(source: Parcel) = create(source)
	override fun newArray(size: Int) = arrayOfNulls<T>(size)
}

inline fun <reified T> parcelableClassLoaderCreator(crossinline create: (Parcel, ClassLoader) -> T) = object : Parcelable.ClassLoaderCreator<T> {
	override fun createFromParcel(source: Parcel, loader: ClassLoader) = create(source, loader)
	override fun createFromParcel(source: Parcel) = createFromParcel(source, T::class.java.classLoader)
	override fun newArray(size: Int) = arrayOfNulls<T>(size)
}

inline fun <reified T : Enum<T>> Parcel.readEnum() = readInt().let { if (it >= 0) enumValues<T>()[it] else null }

inline fun <T : Enum<T>> Parcel.writeEnum(value: T?) = writeInt(value?.ordinal ?: -1)

inline fun <T> Parcel.readNullable(reader: () -> T) = if (readInt() != 0) reader() else null

inline fun <T> Parcel.writeNullable(value: T?, writer: (T) -> Unit) {
	if (value != null) {
		writeInt(1)
		writer(value)
	} else {
		writeInt(0)
	}
}

fun Parcel.readDate() =	readNullable { Date(readLong()) }

fun Parcel.writeDate(value: Date?) = writeNullable(value) { writeLong(it.time) }