package wendu.dsbridge.H5Load;


import wendu.dsbridge.bean.panel.H5Result;

public interface CallBackH5 {
    /**
     * UI Thread
     *

     */
    public void onBefore();

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() ;

    /**
     * UI Thread
     * @param result
     */
    public void onResponse(H5Result result);

    public void onError();


}
