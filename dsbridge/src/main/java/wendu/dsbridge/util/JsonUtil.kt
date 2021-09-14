package wendu.dsbridge.util

import com.google.gson.Gson
import java.lang.reflect.Type

object JsonUtil {
    private val instance: Gson by lazy {
        Gson()
    }

    fun toJson(src: Any): String {
        return instance.toJson(src)
    }

    fun <T> fromJson(src: String, clazz: Class<T>): T {
        return instance.fromJson(src, clazz)
    }

    fun <T> fromJson(src: String, typeOfT: Type): T {
        return instance.fromJson(src, typeOfT)
    }
}