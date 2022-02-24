package gapara.co.id.core.api

import java.lang.reflect.Type

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import okhttp3.RequestBody.Companion.toRequestBody

class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter<ResponseBody, Any> { responseBody -> responseBody.string() }
        } else null
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        return if (String::class.java == type) {
            Converter<String, RequestBody> { value ->
                value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        } else null
    }
}