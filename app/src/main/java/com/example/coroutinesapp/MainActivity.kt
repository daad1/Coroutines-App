package com.example.coroutinesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL


class MainActivity : AppCompatActivity() {

    val adviceAPI = "https://api.adviceslip.com/advice"
    private lateinit var textAdvice: TextView
    private lateinit var buttonAdvice: Button
    private lateinit var buttonStop: Button

    private var stop = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textAdvice = findViewById<TextView>(R.id.tv_Show)
        buttonAdvice = findViewById<Button>(R.id.btn_Advice)
        buttonStop = findViewById<Button>(R.id.btn_stop)

        buttonAdvice.setOnClickListener() {
            stop = true
            requestAPI()
        }
        buttonStop.setOnClickListener {
            stop = false
        }

    }

    private fun requestAPI() {
        CoroutineScope(Dispatchers.IO).launch {

            while (stop) {

                if (!stop) {
                    break
                }


                val data = async {

                    fetchRandomApi()
                }.await()

                if (data.isNotEmpty()) {
                    updateAdvice(data)
                }
            }
        }
    }

    private fun fetchRandomApi(): String {
        var response = ""

        try {
            response = URL(adviceAPI).readText(Charsets.UTF_8)


        } catch (e: Exception) {

            print("Error $e")
        }
        return response

    }

    private suspend fun updateAdvice(data: String) {
        withContext(Dispatchers.Main) {

            val JSONObject = JSONObject(data)
            val slip = JSONObject.getJSONObject("slip")
            val id = slip.getInt("id")
            val advice = slip.getString("advice")

            textAdvice.text = advice

        }
    }
}