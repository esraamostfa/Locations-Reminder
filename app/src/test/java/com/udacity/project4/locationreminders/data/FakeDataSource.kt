package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) { shouldReturnError = value }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (shouldReturnError)
            {
                return Result.Error("Test exception")
            }
            else {
                Result.Success(reminders)
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            val reminder = reminders.firstOrNull {
                it.id == id
            }
            if (reminder != null){
                Result.Success(reminder)
            } else{
                Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)

        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}