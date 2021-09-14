package wendu.dsbridge.ui.panel;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.Nullable;

import com.alibaba.ailabs.tg.utils.ToastUtils;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelEventCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.util.LogPet;
import wendu.dsbridge.util.SysUtil;

public class DeviceBridgeJsApi {

    public static final String TAG = "DeviceBridgeJsApi";
    IMxchipPanelView view;
    PanelDevice panelDevice;
    private int owner;
    private CompletionHandler subscribeHandler;
    private long handlerTime;//记录最近的操作时间，用于云端消息过滤

    private Map<String, JSONObject> subscribeMessageQueue = new HashMap<>();
    private String iotId;

    public DeviceBridgeJsApi(Context context, IMxchipPanelView view, String iotId) {
        this.view = view;
        panelDevice = new PanelDevice(iotId);
        panelDevice.init(context, new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object erro) {
                if (!bSuc) {
                    //初始化失败
                    throw new IllegalStateException("panelDevice 初始化失败");
                }
            }
        });
    }

    public DeviceBridgeJsApi(Context context, String iotId) {
        Log.d(TAG, "DeviceBridgeJsApi()");
        this.iotId = iotId;
        panelDevice = new PanelDevice(iotId);
        panelDevice.init(context, new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object err) {
                if (!bSuc) {
                    //初始化失败
                    throw new IllegalStateException("panelDevice 初始化失败");
                }
            }
        });
    }

    /**
     * @param msg
     * @param handler
     */
    @JavascriptInterface
    public void getData(Object msg, CompletionHandler handler) {
        Log.d(TAG, "getData()");
    }

    /**
     * 获取设备状态
     * 格式如下：
     * {
     * "status":1 //
     * "time":1232341455
     * }
     * 说明：status表示设备生命周期，目前有以下几个状态，
     * 0:未激活；1：上线；3：离线；8：禁用；time表示当前状态的开始时间；
     */
    @JavascriptInterface
    public void getStatus(Object msg, final CompletionHandler handler) {
        panelDevice.getStatus(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object obj) {
                try {
                    JSONObject data = new JSONObject(obj.toString());
                    LogPet.Companion.e(TAG + "<<getStatus>>" + (data.get("data").toString()));
                    handler.complete(data.getJSONObject("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取用户token
     *
     * @param handler
     */
    @JavascriptInterface
    public void getToken(Object msg, CompletionHandler handler) {
        Log.d(TAG, "getToken()");

    }

    /**
     * 注册iotId监听
     * 注意，此处拿到的 properties，events，services 是物的模型中的物的三要素。
     *
     * @param msg
     * @param handler
     */
    @JavascriptInterface
    public void registThing(Object msg, final CompletionHandler handler) {
        panelDevice.subAllEvents(new IPanelEventCallback() {
            @Override
            public void onNotify(String iotid, String topic, Object data) {
                try {
                    JSONObject dataObject = new JSONObject(data.toString());
                    JSONObject jsonObject = new JSONObject();
                    if (topic.contains("properties")) {
                        jsonObject.put("type", "property");
                        jsonObject.put("data", dataObject.get("items"));
                    } else if (topic.contains("status")) {
                        jsonObject.put("data", dataObject.get("status"));
                        jsonObject.put("type", "status");
                    } else {
                        jsonObject.put("data", dataObject);
                        jsonObject.put("type", "event");
                    }
                    LogPet.Companion.e(jsonObject.toString());
                    handler.setProgressData(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object erro) {
                //成功忽略
                LogPet.Companion.e("doTslTest data:" + erro);
            }
        }, null);

    }

    /**
     * 注销iotId监听
     */
    @JavascriptInterface
    public void unRegistThing(Object msg, CompletionHandler handler) {
        Log.d(TAG, "unRegistThing()");
    }


    /**
     * 解绑设备
     */
    @JavascriptInterface
    public void unbind(Object msg, CompletionHandler handler) {
        Log.d(TAG, "unRegistThing()-->" + msg.toString());
    }

    /**
     * @param msg
     * @param handler 获取设备属性
     *                格式如下：
     *                {
     *                "_sys_device_mid": {
     *                "time": 1516356290173,
     *                "value": "example.demo.module-id"
     *                },
     *                "WorkMode": {
     *                "time": 1516347450295,
     *                "value": 0
     *                },
     *                "_sys_device_pid": {
     *                "time": 1516356290173,
     *                "value": "example.demo.partner-id"
     *                }
     *                }
     */
    @JavascriptInterface
    public void getPropertiesFull(Object msg, CompletionHandler handler) {
        Log.d(TAG, "getPropertiesFull()");
        getRemoteProperties(handler);
    }

    private void getRemoteProperties(final CompletionHandler handler) {
        panelDevice.getProperties(new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object obj) {
                try {
                    if (bSuc) {
                        JSONObject jsonObject = new JSONObject((String) obj);
                        if (jsonObject.getInt("code") == 200) {
                            Log.d(TAG, "getRemoteProperties-complete->" + jsonObject);
                            handler.complete(jsonObject.getJSONObject("data"));
                        } else {
                            JSONObject errJson = new JSONObject();
                            errJson.put("code", jsonObject.getInt("code"));
                            errJson.put("msg", jsonObject.getString("localizedMsg"));
                            Log.d(TAG, "getRemoteProperties-complete->" + errJson);
                            handler.complete(errJson);
                        }
                    }
//                else {
//                    JSONObject data = new JSONObject();
//                    AError a = (AError) obj;
//                    data.put("code", a.getCode());
//                    data.put("msg", a.getMsg());
//                    handler.complete(data);
//                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置设备属性
     */
    @JavascriptInterface
    public void setProperties(Object msg, final CompletionHandler handler) {
        Log.d(TAG, "<<setProperties>>" + msg.toString());
        try {
            JSONObject msgData = new JSONObject(msg.toString());
            String items = msgData.getString("data");
            String iotId = msgData.getString("iotId");
            String paramsStr = "{" +
                    "\"iotId\":\"" + iotId + "\","
                    + "\"items\":" + items +
                    "}";
            LogPet.Companion.e(TAG + "<<setProperties>>", paramsStr);
            panelDevice.setProperties(paramsStr, new IPanelCallback() {
                @Override
                public void onComplete(boolean bSuc, @Nullable Object obj) {
                    LogPet.Companion.e(TAG + "<<setProperties>>", obj.toString());
                    if (bSuc) {
                        JSONObject data = null;
                        try {
                            data = new JSONObject((String) obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.complete(data);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setAliProperties(String items, final CompletionHandler handler) {
        String paramsStr = "{" +
                "\"iotId\":\"" + iotId + "\","
                + "\"items\":" + items +
                "}";
        panelDevice.setProperties(paramsStr, new IPanelCallback() {
            @Override
            public void onComplete(boolean bSuc, @Nullable Object obj) {
                if (bSuc) {
                    if (obj != null) {
                        try {


                            JSONObject jsonObject = new JSONObject(obj.toString());
                            int code = jsonObject.getInt("code");
                            if (code == 200) {
                                jsonObject.put("success", true);
                                handler.complete(jsonObject);
                            } else if (code == 29004) {
                                LogPet.Companion.e("error code:" + code);
//                                ToastUtils.showShort(SysUtil.getString("com_device_had_unbind"));
                            } else if (code == 513) {
                                LogPet.Companion.e("error code:" + code);
//                                ToastUtils.showShort(ResourceUtils.getString("com_net_error"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                ToastUtils.showShort("网络异常，请检查网络");
                    Log.e(TAG, "setAliProperties bSuc=false");
                }
            }
        });
    }


}
