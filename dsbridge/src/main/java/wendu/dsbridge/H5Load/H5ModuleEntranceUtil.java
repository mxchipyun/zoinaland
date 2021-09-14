package wendu.dsbridge.H5Load;


import wendu.dsbridge.bean.PanelInfoRes;

public class H5ModuleEntranceUtil {

    public static void entranceH5(PanelInfoRes devicePanelInfo, CallBackH5 callBackH5) throws Exception{
        H5Download.downloadH5(devicePanelInfo,callBackH5);
    }

    public static void removePanel(String modulName){
        H5LocalCacheUtil.deletH5ModuleLocalCache(modulName);
    }
}
