package io.drevezerezh.scylla.advanced.integration.restbase

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

object HttpHelper {

    val JSON: MediaType = "application/json".toMediaType()

    fun mergeUrl( url1 : String , url2 : String) : String {
        if( url1.isEmpty() && url2.isEmpty())
            return ""
        if( url1.isEmpty())
            return url2
        if (url2.isEmpty())
            return url1
        if( url2.startsWith("/"))
            return url1+url2
        return "$url1/$url2"
    }
}