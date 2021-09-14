package wendu.dsbridge.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.lang.reflect.InvocationTargetException
import java.util.regex.Pattern


object SysUtil {
    private var application: Application? = null

    /**
     * one<two return true
     */
    fun strLessThan(one: String?, two: String?): Boolean? {
        try {
            val regEx = "[^0-9]"
            val p = Pattern.compile(regEx)

            val oneValue = p.matcher(one).replaceAll("").trim()
            val twoValue = p.matcher(two).replaceAll("").trim()

            return oneValue < twoValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getAppContext(): Application {
        if (application == null) {
            try {
                application = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return application!!
    }

    @ColorInt
    fun getColor(@ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(getAppContext(), colorResId)
    }

    @ColorInt
    fun getColor(colorStr: String): Int {
        return Color.parseColor(colorStr)
    }

    fun getString(@StringRes stringResId: Int): String = getAppContext().getString(stringResId)

    @SuppressLint("ResourceType")
    fun getStringArray(stringResId: Int): Array<out String> =
        getAppContext().resources.getStringArray(
            stringResId
        )


    fun getDrawable(@DrawableRes drawableResId: Int): Drawable? =
        getAppContext().getDrawable(drawableResId)


    fun getDimen(@DimenRes dimenRes: Int): Float = getAppContext().resources.getDimension(dimenRes)


    fun dpToPx(dp: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, getAppContext().resources.displayMetrics
    )

    fun getAppVersion(): String = getPkgInfo().versionName

    fun getDeviceInfo(): String = "Product=${Build.PRODUCT}," +
            "SDK=${Build.VERSION.SDK_INT}," +
            "Android=${Build.VERSION.RELEASE}," +
            "Brand=${Build.BRAND}," +
            "Board=${Build.BOARD}," +
            "ID=${Build.ID}," +
            "Manufacturer=${Build.MANUFACTURER}"

    fun getApkChannel() = getAppInfo().metaData.getString("APK_CHANNEL")

    fun isAppInstalled(packageName: String): Boolean {
        getAppContext().packageManager.getInstalledPackages(0).forEach {
            if (it.packageName == packageName) {
                return true
            }
        }
        return false
    }

    private fun getPkgInfo(): PackageInfo =
        getAppContext()
            .packageManager
            .getPackageInfo(getAppContext().packageName, PackageManager.GET_META_DATA)


    private fun getAppInfo(): ApplicationInfo = getAppContext().packageManager.getApplicationInfo(
        getAppContext().packageName,
        PackageManager.GET_META_DATA
    )

    /**
     * app is alive?
     */
    fun isAppAlive(context: Context?): Boolean {
        val service = context?.getSystemService(Context.ACTIVITY_SERVICE)
        if (service != null) {
            val am = service as ActivityManager
            val processes = am.runningAppProcesses
            for (p in processes) {
                if (p.processName == context.packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun isPushServiceRunning(context: Context?): Boolean {
        val service = context?.getSystemService(Context.ACTIVITY_SERVICE)
        if (service != null) {
            val am = service as ActivityManager
            val services = am.getRunningServices(30)
            if (services.isEmpty()) {
                return false
            }
            for (s in services) {
                if (s.javaClass.name == context.packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun isTaskEmpty(context: Context?): Boolean {
        val service = context?.getSystemService(Context.ACTIVITY_SERVICE)
        if (service != null) {
            val am = service as ActivityManager
            val tasks = am.getRunningTasks(100)
            for (t in tasks) {
                if (t.topActivity?.packageName == context.packageName) {
                    return false
                }
            }
        }
        return true
    }

    fun isRunningForeground(context: Context?): Boolean {
        val service = context?.getSystemService(Context.ACTIVITY_SERVICE)
        if (service != null) {
            val am = service as ActivityManager
            val processes = am.runningAppProcesses
            for (p in processes) {
                if (p.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    p.processName == context.applicationInfo.processName
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun setTopApp(context: Context?) {
        val service = context?.getSystemService(Context.ACTIVITY_SERVICE)
        if (service != null) {
            val am = service as ActivityManager
            val tasks = am.getRunningTasks(100)
            for (t in tasks) {
                if (t.topActivity?.packageName == context.packageName) {
//                    am.moveTaskToFront(t.taskId, 0)
                }
            }
        }
    }

    fun reOpenApp(context: Context?) {
        val launchIntent = context?.packageManager?.getLaunchIntentForPackage(context.packageName)
        launchIntent?.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        context?.startActivity(launchIntent)
    }

    private var mAPPPackageName: String = ""

    fun getPackageName(context: Context): String {
        if (!TextUtils.isEmpty(mAPPPackageName)) {
            return mAPPPackageName
        }
        synchronized(this) {
            if (!TextUtils.isEmpty(mAPPPackageName)) {
                return mAPPPackageName
            }
            try {
                val manager = context.packageManager
                val info =
                    manager.getPackageInfo(context.packageName, 0)
                mAPPPackageName = info.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return mAPPPackageName
    }

    fun openBrowser(context: Context, url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        url?.let {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(it)
            context.startActivity(intent)
        }
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
//        if (intent.resolveActivity(context.packageManager) != null) {
//            val componentName = intent.resolveActivity(context.packageManager)
//            LogPet.d("openBrowser = " + componentName.className)
//            context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
//        } else {
//
//        }
    }
}
