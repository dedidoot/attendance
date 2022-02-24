package gapara.co.id.core.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Multipart

interface UrlApi {

    @GET
    suspend fun getRequestApi(@Url url: String,
                      @HeaderMap headers: Map<String, String>,
                      @QueryMap (encoded = true) queries: Map<String, String>): String

    @POST
    suspend fun postRequestApi(@Url url: String,
                       @HeaderMap headers: Map<String, String>,
                       @QueryMap queries: Map<String, String>,
                       @Body params: RequestBody?): String

    @Multipart
    @POST
    suspend fun uploadMedia(@Url url: String,
                    @HeaderMap headers: Map<String, String>,
                    @QueryMap queries: Map<String, String>,
                    @Part image: ArrayList<MultipartBody.Part>): String

    @DELETE
    suspend fun deleteRequestApi(@Url url: String,
                              @HeaderMap headers: Map<String, String>,
                              @QueryMap (encoded = true) queries: Map<String, String>): String

    @PUT
    suspend fun putRequestApi(@Url url: String,
                                 @HeaderMap headers: Map<String, String>,
                                 @QueryMap (encoded = true) queries: Map<String, String>): String

    @PATCH
    suspend fun patchRequestApi(@Url url: String,
                                @HeaderMap headers: Map<String, String>,
                                @QueryMap (encoded = true) queries: Map<String, String>): String
}