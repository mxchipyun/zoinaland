package wendu.dsbridge.bean.panel;

import android.text.TextUtils;


import com.alibaba.ailabs.tg.utils.FileUtils;

import java.io.File;

import wendu.dsbridge.H5Load.FilePathConstant;

public class H5LocalCache {
    private String modelName;//h5模块名称
    private String pageName;//h5首页名称

    private String modelDirectoryPath;
    private String currentVerison;
    private String zipSavePath;
    private String downloadUrl;
    private boolean unzipSucccess;

    private String mainPage;//h5首页全路径

    public String getMainPage() {
        return mainPage;
    }

    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public boolean isUnzipSucccess() {
        return unzipSucccess;
    }

    public void setUnzipSucccess(boolean unzipSucccess) {
        this.unzipSucccess = unzipSucccess;
    }

    public String getZipSavePath() {
        if (TextUtils.isEmpty(zipSavePath)) {
            zipSavePath = FilePathConstant.APP_H5_PATH+ File.separator+modelName+File.separator+modelName+".zip";
        }
        return zipSavePath;
    }

    public void setZipSavePath(String zipSavePath) {
        this.zipSavePath = zipSavePath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDirectoryPath() {
        if (TextUtils.isEmpty(modelDirectoryPath)) {
            modelDirectoryPath =  FilePathConstant.APP_H5_PATH+ File.separator+modelName+File.separator+modelName;
        }
        return modelDirectoryPath;
    }

    public void setModelDirectoryPath(String modelDirectoryPath) {
        this.modelDirectoryPath = modelDirectoryPath;
    }

    public String getCurrentVerison() {
        return currentVerison;
    }

    public void setCurrentVerison(String currentVerison) {
        this.currentVerison = currentVerison;
    }

    public boolean hasLocal() {
        boolean orExistsDir = FileUtils.isFileExists(modelDirectoryPath);
        boolean mainPageExist= FileUtils.isFileExists(mainPage);
        return orExistsDir&&mainPageExist&&unzipSucccess;
    }

    public String  getH5ModuleIndex() {
        return modelDirectoryPath+"";
    }

}
