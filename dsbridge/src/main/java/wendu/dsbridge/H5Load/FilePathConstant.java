package wendu.dsbridge.H5Load;


import java.io.File;

import wendu.dsbridge.util.SysUtil;

public interface FilePathConstant {


    public static final String  APP_SD_ROOT_PATH = SysUtil.INSTANCE.getAppContext().getExternalFilesDir("").getAbsolutePath();

    public static final String  APP_H5_PATH = APP_SD_ROOT_PATH+File.separator+"h5";



}
