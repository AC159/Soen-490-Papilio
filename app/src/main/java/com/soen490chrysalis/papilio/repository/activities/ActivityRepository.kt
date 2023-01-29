package com.soen490chrysalis.papilio.repository.activities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.soen490chrysalis.papilio.services.network.IUserApiService
import com.soen490chrysalis.papilio.view.dialogs.EventDate
import com.soen490chrysalis.papilio.view.dialogs.EventTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ActivityRepository(
    private var firebaseAuth : FirebaseAuth,
    private val userAPIService : IUserApiService,
    private val coroutineDispatcher : CoroutineDispatcher = Dispatchers.IO
) : IActivityRepository
{
    private val logTag = ActivityRepository::class.java.simpleName

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun postNewUserActivity(
        activityTitle : String,
        description : String,
        groupSize : Int,
        pictures : List<Pair<String, InputStream>>,
        activityDate : EventDate,
        startTime : EventTime,
        endTime : EventTime,
        activityAddress : String
    ) : Response<Void>
    {
        return withContext(coroutineDispatcher)
        {
            val calendar : Calendar = Calendar.getInstance()

            // Set the activity start date and time
            calendar.set(
                activityDate.year,
                activityDate.month,
                activityDate.day,
                startTime.hourOfDay,
                startTime.minute
            )

            val outputFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:'00.000 +00:00'") // ISO-8601 date format
            val activityStartTime : String = outputFmt.format(calendar.time)

            calendar.set(
                activityDate.year,
                activityDate.month,
                activityDate.day,
                endTime.hourOfDay,
                endTime.minute
            )

            val activityEndTime : String = outputFmt.format(calendar.time)

            Log.d(logTag, "Activity start & end times: $activityStartTime, $activityEndTime")

            val images : MutableList<MultipartBody.Part> = ArrayList()
            for (pair in pictures)
            {
                val inputStream = pair.second
                val imageFileExtension = pair.first

                val file = File.createTempFile("tempFile", null, null)
                val out : OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len : Int
                while (inputStream.read(buf).also { len = it } > 0)
                {
                    out.write(buf, 0, len)
                }
                out.close()
                inputStream.close()

                val currentImage = MultipartBody.Part.createFormData(
                    "images", // this name must match the name given in the backend
                    file.name,
                    file.asRequestBody("image/$imageFileExtension".toMediaType())
                )

                images.add(currentImage)
            }

            val activityRequestBody : MutableMap<String, Any> = HashMap()
            activityRequestBody["activity[title]"] = activityTitle
            activityRequestBody["activity[description]"] = description
            activityRequestBody["activity[startTime]"] = activityStartTime
            activityRequestBody["activity[endTime]"] = activityEndTime
            activityRequestBody["activity[address]"] = activityAddress
            activityRequestBody["activity[groupSize]"] = groupSize

            Log.d(logTag, "Finished converting images to a Multipart request body")

            val response =
                userAPIService.postNewUserActivity(
                    firebaseAuth.currentUser?.uid,
                    activityRequestBody,
                    images
                )
            Log.d(logTag, "Post new user activity: $response")

            return@withContext response
        }
    }
}