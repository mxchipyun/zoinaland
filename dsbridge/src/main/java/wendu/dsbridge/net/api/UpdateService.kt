package wendu.dsbridge.net.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import wendu.dsbridge.bean.BaseResp
import wendu.dsbridge.bean.PanelInfoRes
import wendu.dsbridge.bean.panel.H5BeanReq

/**
 * 升级帮助相关接口
 */
interface UpdateService {


    //获取H5面板升级信息
//    @GET("/app/v1/panel/h5/upgradeInfo")
    @POST("/api/v1/h5panel/url/")
    suspend fun panelH5UpgradeInfo(@Body req: H5BeanReq): BaseResp<PanelInfoRes?>?

    //下载H5zip包
    @Streaming
    @GET
    fun downLoadH5ZipFile(@Url path: String?): Observable<ResponseBody?>?

    //下载H5zip包
    @Streaming
    @GET
    fun downLoadH5ZipFile1(@Url path: String?): Response<ResponseBody>


}