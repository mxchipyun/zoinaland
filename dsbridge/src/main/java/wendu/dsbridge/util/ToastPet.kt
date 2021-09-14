package wendu.dsbridge.util

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import wendu.dsbridge.ext.runUI

class ToastPet {
    companion object {
        @MainThread
        fun showShort(@StringRes errorString: Int) {
            runUI {
                Toast.makeText(SysUtil.getAppContext(), errorString, Toast.LENGTH_SHORT).show()
            }
        }

        @MainThread
        fun showLang(@StringRes errorString: Int) {
            runUI {
                Toast.makeText(SysUtil.getAppContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }

        @MainThread
        fun showShort(errorString: String) {
            runUI {
                Toast.makeText(SysUtil.getAppContext(), errorString, Toast.LENGTH_SHORT).show()
            }
        }

        @MainThread
        fun showLang(errorString: String) {
            runUI {
                Toast.makeText(SysUtil.getAppContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }

    }
}