package pl.edu.uj.ii.skwarczek.betterchatfast.util

import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


object RequestHandler {

    const val GET : String = "GET"
    const val POST : String = "POST"
    const val PUT : String = "PUT"

    @Throws(IOException::class)
    fun requestPOST(r_url: String?, postDataParams: JSONObject): String {
        val url = URL(r_url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 3000
        conn.connectTimeout = 3000
        conn.requestMethod = POST
        conn.doInput = true
        conn.doOutput = true
        conn.setRequestProperty("Content-Type","application/json; charset=utf8")

        conn.setRequestProperty("Api-Token","e29fe77840867f6120bbc2ee6b8ea2027ef76eca")

        val os: OutputStream = conn.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
        writer.write(postDataParams.toString())
        writer.flush()
        writer.close()
        os.close()
        val responseCode: Int = conn.responseCode // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val `in` = BufferedReader(InputStreamReader(conn.inputStream))
            val sb = StringBuffer("")
            var line: String? = ""
            while (`in`.readLine().also { line = it } != null) {
                sb.append(line)
                break
            }
            `in`.close()
            Log.d("XDok",sb.toString())
            return sb.toString()
        }
        //error
        val `in` = BufferedReader(InputStreamReader(conn.errorStream))
        val sb = StringBuffer("")
        var line: String? = ""
        while (`in`.readLine().also { line = it } != null) {
            sb.append(line)
            break
        }
        `in`.close()
Log.d("XDnieok",sb.toString())
        return sb.toString()
    }

    @Throws(IOException::class)
    fun requestPUT(r_url: String?, postDataParams: JSONObject): String {
        val url = URL(r_url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 3000
        conn.connectTimeout = 3000
        conn.requestMethod = PUT
        conn.doInput = true
        conn.doOutput = true
        conn.setRequestProperty("Content-Type","application/json; charset=utf8")

        conn.setRequestProperty("Api-Token","e29fe77840867f6120bbc2ee6b8ea2027ef76eca")

        val os: OutputStream = conn.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
        writer.write(postDataParams.toString())
        writer.flush()
        writer.close()
        os.close()
        val responseCode: Int = conn.responseCode // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val `in` = BufferedReader(InputStreamReader(conn.inputStream))
            val sb = StringBuffer("")
            var line: String? = ""
            while (`in`.readLine().also { line = it } != null) {
                sb.append(line)
                break
            }
            `in`.close()
            return sb.toString()
        }
        //error
        val `in` = BufferedReader(InputStreamReader(conn.errorStream))
        val sb = StringBuffer("")
        var line: String? = ""
        while (`in`.readLine().also { line = it } != null) {
            sb.append(line)
            break
        }
        `in`.close()

        return sb.toString()
    }

    @Throws(IOException::class)
    fun requestGET(url: String?): String {
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = GET
        val responseCode = con.responseCode
        println("Response Code :: $responseCode")
        return if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            val `in` =
                BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuffer()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            response.toString()
        } else {
            ""
        }
    }

    @Throws(IOException::class)
    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }
}