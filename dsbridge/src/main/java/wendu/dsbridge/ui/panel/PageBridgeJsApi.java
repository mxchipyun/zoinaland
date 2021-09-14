package wendu.dsbridge.ui.panel;


import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.alibaba.ailabs.tg.utils.AppUtils;
import com.aliyun.iot.aep.component.router.Router;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.util.MxUtil;

public class PageBridgeJsApi {

    public static final String TAG = "PageBridgeJsApi";
    IMxchipPanelView view;
    Context context;

    public PageBridgeJsApi(Context context, IMxchipPanelView view) {
        this.context = context;
        this.view = view;
    }

    /**
     * 页面跳转
     * 参数: # path 1. external不存在或者为false , path表示app外页面 2. external为true, path表示app内页面
     * # external 是否跳转外部页面
     */
    @JavascriptInterface
    public void go(Object msg, CompletionHandler handler) {
        try {
            Bundle bundle = new Bundle();
            JSONObject jsonObject = new JSONObject(msg.toString());
            String pathRouter = jsonObject.getString("path");
            if (pathRouter.equals("https://com.aliyun.iot.ilop/page/ota/list")) {//固件升级界面
                Router.getInstance().toUrl(context, "https://com.aliyun.iot.ilop/page/ota/list");
                handler.complete();
                return;
            }
            if (!pathRouter.startsWith("/")) {
                pathRouter = "/" + pathRouter;
            }
            if (jsonObject.has("query")) {
                JSONObject queryObject = jsonObject.getJSONObject("query");
                Iterator<String> it = queryObject.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = queryObject.getString(key);
                    bundle.putString(key, value);
                }
            }
           if (MxUtil.INSTANCE.getRouterListener() != null)
                MxUtil.INSTANCE.getRouterListener().navigate(pathRouter, msg.toString());
//            ARouter.getInstance()
//                    .build(pathRouter)
//                    .with(bundle)
//                    .withObject("deviceInfo", deviceInfo)
//                    .navigation();
            handler.complete();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 退出webview页面
     */
    @JavascriptInterface
    public void closeWebView(Object msg, CompletionHandler handler) {
        view.finishActivity();
        handler.complete();
    }

    /**
     * 设置容器bar title
     */
    @JavascriptInterface
    public void setTitle(Object msg) {

    }

    @JavascriptInterface
    public void getPlatformInfo(Object msg, CompletionHandler handler) {
        JSONObject info = new JSONObject();
        try {
            info.put("platform", "android");
            info.put("version", AppUtils.getAppVersionName(context));

            handler.complete(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void getBarHeight(Object msg, CompletionHandler handler) {
        JSONObject barinfo = new JSONObject();
        try {
            barinfo.put("top", getStatusBarHeight(context) / 3);
            barinfo.put("bottom", 0);
            handler.complete(barinfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //获取状态栏高度
    private int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }

}
