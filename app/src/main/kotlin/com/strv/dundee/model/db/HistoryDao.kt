package com.strv.dundee.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.strv.dundee.model.entity.History

@Dao
interface HistoryDao {

	@Insert(onConflict = REPLACE)
	fun putHistory(ticker: History)

	@Query("SELECT * FROM history WHERE coin = :coin AND timeFrame = :timeFrameKey LIMIT 1")
	fun getHistory(coin: String, timeFrameKey: String): LiveData<History>
}