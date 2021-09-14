package wendu.dsbridge

import com.aliyun.iot.aep.sdk.IoTSmart
import com.aliyun.iot.aep.sdk.framework.AApplication
import com.aliyun.iot.aep.sdk.framework.config.AConfigure
import com.aliyun.iot.aep.sdk.framework.sdk.SDKManager
import com.aliyun.iot.aep.sdk.init.PushManagerHelper

open class MxApp : AApplication() {


    //阿里飞燕sdk初始化 link #https://help.aliyun.com/document_detail/146536.html?spm=a2c4g.11186623.6.714.31ab178c55Nu8U
    fun initIoTSmart() {
        // 初始化参数配置
        val initConfig = IoTSmart.InitConfig()
            .setRegionType(IoTSmart.REGION_CHINA_ONLY)// REGION_ALL表示连接全球多个接入点；REGION_CHINA_ONLY表示直连中国内地接入点
            .setProductEnv(IoTSmart.PRODUCT_ENV_PROD)// setProductEnv是API Level 8专用，API Level 9及以上版本使用IoTSmart.setProductScope来区分App是否操作未发布产品，且不再区分测试版与正式版，统一为正式版
            .setDebug(true)// 是否打开日志

        /**
         * 设置App配网列表的产品范围，PRODUCT_SCOPE_ALL表示当前项目中已发布和未发布的所有产品，
         * PRODUCT_SCOPE_PUBLISHED表示只包含已发布产品，正式发布的App请选择PRODUCT_SCOPE_PUBLISHED
         */
//        IoTSmart.setProductScope(IoTSmart.PRODUCT_SCOPE_ALL)
        // 初始化，App须继承自AApplication，否则会报错
        IoTSmart.init(this, initConfig)

        AConfigure.getInstance().init(this)
        PushManagerHelper.getInstance().init(this)

        SDKManager.init(this)


    }

   

}