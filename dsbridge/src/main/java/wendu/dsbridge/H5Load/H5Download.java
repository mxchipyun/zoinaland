package wendu.dsbridge.H5Load;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.alibaba.ailabs.tg.utils.ZipUtils;
import com.blankj.utilcode.util.FileIOUtils;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import wendu.dsbridge.bean.PanelInfoRes;
import wendu.dsbridge.bean.panel.H5LocalCache;
import wendu.dsbridge.bean.panel.H5Result;
import wendu.dsbridge.net.RetrofitManager;
import wendu.dsbridge.net.api.UpdateService;
import wendu.dsbridge.net.config.SpKey;
import wendu.dsbridge.util.LogPet;
import wendu.dsbridge.util.SysUtil;

public class H5Download {
    /**
     * 根据procuctKey检查版本
     *
     * @return
     */
    public static boolean checkVersion(PanelInfoRes info) {
        if (info == null) {
            throw new NullPointerException();
        }
        H5LocalCache h5ModuleLocalCache = H5LocalCacheUtil.getH5ModuleLocalCache(info.getProductKey());
        if (h5ModuleLocalCache == null) {
            return true;
        }

        String version = info.getVersion();

        String currentVerison = h5ModuleLocalCache.getCurrentVerison();

        //版本名相同，但是下载的文件不存在了
        if ((version == currentVerison) || (version != null && version.equals(currentVerison))) {
            return !h5ModuleLocalCache.hasLocal();
        } else {
            return true;
        }
    }

    @SuppressLint("CheckResult")
    public static void downloadH5(PanelInfoRes devicePanelInfo, CallBackH5 callBackH5) throws Exception {
        LogPet.Companion.d("downloadH5 start");
        Observable<H5Result> observable = Observable
                .just(devicePanelInfo)
                .flatMap(new Function<PanelInfoRes, ObservableSource<H5Result>>() {
                    @Override
                    public ObservableSource<H5Result> apply(PanelInfoRes devicePanelInfo) throws Exception {
                        LogPet.Companion.d("---------");
                        if (checkVersion(devicePanelInfo)) {
                            return RetrofitManager.INSTANCE.getService(UpdateService.class)
                                    .downLoadH5ZipFile(devicePanelInfo.getDownload_url())
                                    .map(responseBody -> {
                                        H5LocalCache h5LocalCache = devicePanelInfo.createH5ModuleCache();
                                        boolean fileFromIS = FileIOUtils.writeFileFromIS(h5LocalCache.getZipSavePath(), responseBody.byteStream());
                                        if (fileFromIS) {
                                            try {
                                                ZipUtils.unzipFile(h5LocalCache.getZipSavePath(), h5LocalCache.getModelDirectoryPath());
                                            } catch (IOException e) {
                                                H5Result h5Result = new H5Result();
                                                h5Result.setCode(h5Result.UN_ZIP_FAIL);
                                                h5Result.setMessage("com_panel_unzip_failed");
                                                return h5Result;
                                            }
                                            String h5MainUrl = H5LocalCacheUtil.getH5MainUrl(h5LocalCache);
                                            if (TextUtils.isEmpty(h5MainUrl)) {
                                                H5Result h5Result = new H5Result();
                                                h5Result.setCode(h5Result.MAIN_PAGE_UNFOUND);
                                                h5Result.setMessage("com_panel_no_exist");
                                                return h5Result;
                                            }
                                            h5LocalCache.setUnzipSucccess(true);
                                            h5LocalCache.setMainPage(h5MainUrl);
                                            H5LocalCacheUtil.saveH5ModuleLocalCache(h5LocalCache.getModelName(), h5LocalCache);
                                            H5Result h5Result = new H5Result();
                                            h5Result.setCode(H5Result.SUCCESS);
                                            return h5Result;
                                        } else {
                                            H5Result h5Result = new H5Result();
                                            h5Result.setCode(H5Result.SAVE_ZIP_FAIL);
                                            h5Result.setMessage("com_panel_zip_save_failed");
                                            return h5Result;
                                        }
                                    });
                        } else {
                            H5Result h5Result = new H5Result();
                            h5Result.setCode(H5Result.SUCCESS);
                            return Observable.just(h5Result);
                        }

                    }
                });
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        callBackH5.onBefore();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        callBackH5.onAfter();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<H5Result>() {
                            @Override
                            public void accept(H5Result h5Result) throws Exception {
                                if (h5Result.isSuccess()) {
                                    callBackH5.onResponse(h5Result);
                                } else {
                                    callBackH5.onResponse(h5Result);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogPet.Companion.d("-----33333----");
                                H5Result h5ModuleEntranceResult = new H5Result();
                                h5ModuleEntranceResult.setCode(H5Result.ERROR);
                                h5ModuleEntranceResult.setMessage(throwable.getMessage());
                                callBackH5.onResponse(h5ModuleEntranceResult);
                            }
                        });
    }


}
