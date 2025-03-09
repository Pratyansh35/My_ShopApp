package com.parawale.GrocEase.Ai

import android.content.Context
import android.util.Log
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.database.datareference
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
import com.parawale.GrocEase.BuildConfig
suspend fun fetchCategoriesAndNamesFromFirebase(): Pair<List<String>, List<String>> {
    val categories = mutableSetOf<String>()
    val names = mutableSetOf<String>()
    val categoriesToFetch = listOf("Groceries", "Electronics", "Medicines", "Apparel", "Food")

    return try {
        for (category in categoriesToFetch) {
            val task = datareference.child("items").child(category).get().await()


            Log.d("FirebaseData", "Fetched ${task.childrenCount} items for category $category")

            for (itemSnapshot in task.children) {
                val item = itemSnapshot.getValue(Dishfordb::class.java)

                if (item == null) {
                    Log.w("FirebaseData", "Null item in category $category")
                } else {
                    item.categories?.let { categories.addAll(it) }
                    item.name?.let { names.add(it) }
                }
            }
        }

        Pair(categories.toList(), names.toList())
    } catch (e: Exception) {
        Log.e("FirebaseData", "Error fetching categories and names: ${e.message}", e)
        Pair(emptyList(), emptyList())
    }
}


suspend fun fetchSuggestionsFromOpenAI(context: Context, query: String): List<Dishfordb>? {
    val apiKey = BuildConfig.OPENAI_API_KEY

    val (categories, names) = fetchCategoriesAndNamesFromFirebase()
    Log.d("FetchSuggestions", "Fetched Categories: $categories, Names: $names")

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
                Log.e("OpenAIResponse", "Request failed: ${e.message}", e)
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("OpenAIResponse", "Response Data: $responseData")
                try {
                    val parsedCategories = parseOpenAIResponseForCategories(responseData)
                    Log.d("OpenAIResponse", "Parsed Categories: $parsedCategories")

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val items = fetchItemsFromFirebase(parsedCategories)
                            continuation.resume(items)
                        } catch (e: Exception) {
                            Log.e("FetchItems", "Error fetching items: ${e.message}", e)
                            continuation.resumeWithException(e)
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("OpenAIResponse", "Error parsing response: ${e.message}")
                    continuation.resumeWithException(e)
                }
            }
        })
    }
}


suspend fun fetchItemsFromFirebase(categories: List<String>): List<Dishfordb>? {
    return try {
        val matchingItems = mutableListOf<Dishfordb>()
        val addedItemNames = mutableSetOf<String>() // Set to track added item names
        val categoriesToFetch = listOf("Groceries", "Electronics", "Medicines", "Apparel", "Food")

        for (category in categoriesToFetch) {
            val task = datareference.child("items").child(category).get().await()

            for (itemSnapshot in task.children) {
                val item = itemSnapshot.getValue(Dishfordb::class.java)

                item?.let {
                    val normalizedItemName = it.name.trim().lowercase(Locale.ROOT) // Normalize name

                    if (it.categories.any { cat -> categories.contains(cat.lowercase(Locale.ROOT)) } &&
                        addedItemNames.add(normalizedItemName)
                    ) {
                        matchingItems.add(it)
                    } else {
                        Log.d("FetchItems", "Duplicate or unmatched item skipped: ${it.name}")
                    }
                }
            }
        }
        Log.d("OpenAIResponse", "Matching Items Count: ${matchingItems.size}")
        matchingItems.distinctBy { it.name }
    } catch (e: Exception) {
        Log.e("FirebaseData", "Error fetching items: ${e.message}", e)
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
