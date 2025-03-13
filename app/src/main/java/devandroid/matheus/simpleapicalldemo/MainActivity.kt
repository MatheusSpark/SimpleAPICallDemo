package devandroid.matheus.simpleapicalldemo

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.Buffer
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallAPILoginAsyncTask().execute()
    }

    private inner class CallAPILoginAsyncTask() : AsyncTask<Any, Void, String>() {
        private lateinit var customProcessDialog: Dialog
        override fun onPreExecute() {
            super.onPreExecute()
            showProcessDialog()

        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://api.github.com/users/defunkt")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = false
                val httpResult: Int = connection.responseCode
                if (httpResult == HttpsURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }

            }catch (e: SocketTimeoutException){
               result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
            return result 
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProcessDialog()
            if (result != null) {
                Log.i("JSON Response", result)
            }
        }

        private fun showProcessDialog() {
            customProcessDialog = Dialog(this@MainActivity)
            customProcessDialog.setContentView(R.layout.dialog_custom_progress)
            customProcessDialog.show()
        }

        private fun cancelProcessDialog() {
            customProcessDialog.dismiss()
        }
    }
}