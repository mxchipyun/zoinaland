package wendu.dsbridge.net.api
import retrofit2.http.*
import wendu.dsbridge.bean.BaseResp

/**
 * 产品相关接口
 */
interface ProductService {


    @PUT("/app/v1/device/prop/name")
    suspend fun updateRuleButtonName(@Body data: Map<String, @JvmSuppressWildcards Any>): BaseResp<Any>

    @GET
    suspend fun fetchOwnServerGET(
        @Url url: String,
        @QueryMap data: Map<String, @JvmSuppressWildcards Any>
    ): BaseResp<Any>

    @POST
    suspend fun fetchOwnServerPOST(
        @Url url: String,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): BaseResp<Any>

    @PUT
    suspend fun fetchOwnServerPUT(
        @Url url: String,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): BaseResp<Any>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun fetchOwnServerDELETE(
        @Url url: String,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): BaseResp<Any>



}