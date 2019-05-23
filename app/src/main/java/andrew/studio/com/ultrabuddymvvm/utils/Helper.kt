package andrew.studio.com.ultrabuddymvvm.utils

import java.text.SimpleDateFormat
import java.util.*

class Helper {
    companion object {
        fun toTimeString(timeMillis: Long): String{
            val sdf = SimpleDateFormat("hh:mm", Locale.US)
            return sdf.format(timeMillis)
        }
    }
}