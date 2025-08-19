package com.piashcse.appscheduler.utils
import androidx.room.TypeConverter
import com.piashcse.appscheduler.data.model.ScheduleStatus

class Converters {

    @TypeConverter
    fun fromScheduleStatus(status: ScheduleStatus): String = status.name

    @TypeConverter
    fun toScheduleStatus(status: String): ScheduleStatus = ScheduleStatus.valueOf(status)
}