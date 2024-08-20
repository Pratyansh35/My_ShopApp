package com.example.parawaleapp.Ai

import android.content.Context
import android.util.Log
import com.example.parawaleapp.R
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.datareference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun fetchCategoriesAndNamesFromFirebase(): Pair<List<String>, List<String>> {
    val categories = mutableSetOf<String>()
    val names = mutableSetOf<String>()

    val task = datareference.child("Items").get().await()
    for (itemSnapshot in task.children) {
        val item = itemSnapshot.getValue(Dishfordb::class.java)
        item?.let {
            categories.addAll(it.categories)
            names.add(it.name)
        }
    }

    return Pair(categories.toList(), names.toList())
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun fetchSuggestionsFromOpenAI(context: Context, query: String): List<Dishfordb>? {
    val apiKey = context.getString(R.string.openai_api_key)

    val (categories, names) = fetchCategoriesAndNamesFromFirebase()

    val client = OkHttpClient()

    val requestBody = JSONObject().apply {
        put("model", "gpt-4o-mini")
        put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", """
                    User is looking for items related to: $query.
                    Here is a list of categories: ${categories.joinToString(", ")}.
                    Here is a list of item names: ${names.joinToString(", ")}.
                    Return the most relevant categories or item names based on the user's query.
                """.trimIndent())
            })
        })
        put("max_tokens", 150)
        put("temperature", 0.7)
    }.toString().toRequestBody("application/json".toMediaTypeOrNull())

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

    return suspendCancellableCoroutine { continuation ->
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("OpenAIResponse", "Response Data: $responseData")
                try {
                    val categories = parseOpenAIResponseForCategories(responseData)

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val items = fetchItemsFromFirebase(categories)
                            continuation.resume(items)
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }

                } catch (e: JSONException) {
                    Log.e("OpenAIResponse", "Error parsing JSON: ${e.message}")
                    continuation.resumeWithException(e)
                }
            }
        })
    }
}

suspend fun fetchItemsFromFirebase(categories: List<String>): List<Dishfordb>? {
    return try {
        val matchingItems = mutableListOf<Dishfordb>()
        val addedItemNames = mutableSetOf<String>() // Set to track added names
        val task = datareference.child("Items").get().await()

        for (itemSnapshot in task.children) {
            val item = itemSnapshot.getValue(Dishfordb::class.java)
            item?.let {
                val normalizedItemName = it.name.trim().lowercase(Locale.ROOT) // Normalize name

                // Check if the item matches any category and hasn't been added yet
                if (it.categories.any { category -> categories.contains(category.lowercase(Locale.ROOT)) }) {
                    if (addedItemNames.add(normalizedItemName)) { // Add to set and check if it was already added
                        matchingItems.add(it)
                    } else {
                        Log.d("FetchItems", "Duplicate item not added: ${it.name}")
                    }
                }
            }
        }
        Log.d("OpenAIResponse", "Matching Items Names: ${matchingItems}")
        Log.d("OpenAIResponse", "Matching Items: ${matchingItems.distinctBy { it.name }.toList()}")
        Log.d("OpenAIResponse", "Matching Items Count: ${matchingItems.distinctBy { it.name }.toList().size}")
        matchingItems.distinctBy { it.name }.toList()

    } catch (e: Exception) {
        Log.e("FirebaseData", "Error fetching items: ${e.message}")
        null
    }
}



fun parseOpenAIResponseForCategories(responseData: String?): List<String> {
    return try {
        val jsonObject = responseData?.let { JSONObject(it) }
        val choices = jsonObject?.getJSONArray("choices")
        if (choices != null) {
            if (choices.length() > 0) {
                val messageContent = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // Extract categories using Regex or simple split based on format
                val categoriesSection = messageContent.substringAfter("**Categories:**").substringBefore("**Item Names:**")
                return categoriesSection.split("\n").map { it.trim().replace("-", "").trim() }.filter { it.isNotEmpty() }
            }
        }
        emptyList()
    } catch (e: JSONException) {
        Log.e("JSONParsingError", "Error parsing JSON: ${e.message}")
        emptyList()
    }
}
