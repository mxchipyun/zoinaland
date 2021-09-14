package wendu.dsbridge.bean.panel;

public class H5Result {
    private int code = -1;
    private String  message;

    public static final int ERROR = 1;
    public static final int SUCCESS = 0;
    public static final int SAVE_ZIP_FAIL = 2;
    public static final int UN_ZIP_FAIL = 3;
    public static final int MAIN_PAGE_UNFOUND = 4;

    public boolean isSuccess() {
        return this.code == SUCCESS;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }
}
