package wendu.dsbridge.util

import android.util.Log

class LogPet {
    companion object {
        private const val TAG = "LogPet"

        fun e(msg: String?, tag: String? = TAG) {
            Log.e(tag, msg ?: "msg is null")
        }

        fun d(msg: String?, tag: String? = TAG) {
            Log.d(tag, msg ?: "msg is null")
        }

        fun e(msg: String?) {
            Log.e(TAG, msg ?: "msg is null")
        }

        fun d(msg: String?) {
            Log.d(TAG, msg ?: "msg is null")
        }

        fun v(msg: String?, tag: String? = TAG) {
            Log.v(tag, msg ?: "msg is null")
        }

        fun w(msg: String?, tag: String? = TAG) {
            Log.w(tag, msg ?: "msg is null")
        }
    }
}