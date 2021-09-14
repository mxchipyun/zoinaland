package wendu.dsbridge.bean

import android.graphics.drawable.Drawable
import wendu.dsbridge.R

data class OtaProgressBean(
    var step: Int = 0,
    val stepDes: String,
    var localStep: Int = 0,
    var startRotate: Boolean = false,
    var updateSuccess: Boolean? = false
) {

}