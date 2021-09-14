package wendu.dsbridge.H5Load;

import android.text.TextUtils;

import com.alibaba.ailabs.tg.utils.FileUtils;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.utils.SpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import wendu.dsbridge.bean.panel.H5LocalCache;


/**
 *
 */
public class H5LocalCacheUtil {

    private static HashMap<String, H5LocalCache> h5ModuleLocalCachesMap;
    private static final String H5_MODULE_LOCAL_CACHES_MAP = "H5_MODULE_LOCAL_CACHES_MAP";
    public static final Gson GSON = new Gson();

    static {


        String string = SpUtil.getString(AApplication.getInstance(), H5_MODULE_LOCAL_CACHES_MAP);
        if (!TextUtils.isEmpty(string)) {
            try {
                Gson gson = new Gson();
                TypeToken<HashMap<String, H5LocalCache>> typeToken = new TypeToken<HashMap<String, H5LocalCache>>() {
                };
                h5ModuleLocalCachesMap = gson.fromJson(string, typeToken.getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        if (h5ModuleLocalCachesMap == null) {
            h5ModuleLocalCachesMap = new HashMap<>();
        }
    }


    public static void saveH5ModuleLocalCache(String moduleName, H5LocalCache h5ModuleLocalCache) {
        h5ModuleLocalCachesMap.put(moduleName, h5ModuleLocalCache);
        SpUtil.putString(AApplication.getInstance(), H5_MODULE_LOCAL_CACHES_MAP, GSON.toJson(h5ModuleLocalCachesMap));
    }

    public static void deletH5ModuleLocalCache(String moduleName) {
        h5ModuleLocalCachesMap.remove(moduleName);
        SpUtil.putString(AApplication.getInstance(), H5_MODULE_LOCAL_CACHES_MAP, GSON.toJson(h5ModuleLocalCachesMap));
    }

    public static H5LocalCache getH5ModuleLocalCache(String moduleName) {
        return h5ModuleLocalCachesMap.get(moduleName);
    }

    public static String getH5Url(String productKey, String iotId, int owned) {
        H5LocalCache localCache = H5LocalCacheUtil.getH5ModuleLocalCache(productKey);
        if (localCache == null || TextUtils.isEmpty(localCache.getMainPage())) {
            return null;
        }
        String url = localCache.getMainPage() + "?iotId=" + iotId +
                "&productKey=" + productKey +
                "&isOwner=" + owned;
        if (!url.startsWith("http")) {
            url = "file://" + url;
        }
        return url;
    }

    public static String getH5MainUrl(String moduleName, String pageName) {
        if (TextUtils.isEmpty(moduleName)) {
            throw new NullPointerException("面板名不存在");
        }
        H5LocalCache h5ModuleLocalCache = getH5ModuleLocalCache(moduleName);
        if (h5ModuleLocalCache == null) {
            return null;
        }

        return getH5MainUrl(h5ModuleLocalCache);

    }

    public static String getH5MainUrl(H5LocalCache h5ModuleLocalCache) {
        String modelDirectoryPath = h5ModuleLocalCache.getModelDirectoryPath();
        if (!FileUtils.isDir(modelDirectoryPath)) {
            return null;
        }
        return findMainPageByPageName(modelDirectoryPath, h5ModuleLocalCache.getPageName());
    }


    private static String findMainPageByPageName(String dir, String pageName) {
        if (TextUtils.isEmpty(pageName)) {
            pageName = "index.html";
        }
        return findFileByName(dir, pageName);

    }

    public static String findFileByName(String dir, String pageName) {
        if (TextUtils.isEmpty(pageName)) {
            pageName = "index.html";
        }
        boolean isFile = FileUtils.isFile(dir + File.separator + pageName);
        if (isFile) {
            return dir + File.separator + pageName;
        }
        List<File> files = FileUtils.listFilesInDir(dir);

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (pageName.endsWith(fileName)) {
                    return file.getAbsolutePath();
                }
            } else {
                String fileByName = findFileByName(file.getAbsolutePath(), pageName);
                if (!TextUtils.isEmpty(fileByName)) {
                    return fileByName;
                }
            }
        }
        return null;
    }
}
