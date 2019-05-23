package andrew.studio.com.ultrabuddymvvm.utils

import andrew.studio.com.ultrabuddymvvm.data.entity.Point
import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    companion object {
        fun toTimeString(timeMillis: Long): String {
            val sdf = SimpleDateFormat("hh:mm", Locale.US)
            return sdf.format(timeMillis)
        }

        fun <T> gsonToObject(string: String): T? {
            return if (string.isEmpty())
                null
            else {
                val listType = object : TypeToken<List<Polygon>>() {
                }.type
                Gson().fromJson(string, listType)
            }
        }

        fun <T> gsonToList(string: String): List<T> {
            return if (string.isEmpty())
                emptyList()
            else {
                val listType = object : TypeToken<List<Polygon>>() {
                }.type
                Gson().fromJson(string, listType)
            }
        }

        fun <T> objectToString(anyObject: T): String {
            return Gson().toJson(anyObject)
        }

        fun polygonToString(polygon: Polygon): String {
            val points = polygon.points
            var str = ""
            for (i in 0 until points.size - 1) {
                str += "${points[i].x},${points[i].y}|"
            }
            str += "${points[points.size-1].x},${points[points.size-1].y}"
            return str
        }

        fun stringToPolygon(str: String): Polygon{
            try {
                val pointStringList = str.split("|")
                val points = mutableListOf<Point>()
                for (pointString in pointStringList){
                    val paramList = pointString.split(",")
                    points.add(Point(paramList[0].toInt(), paramList[1].toInt()))
                }
                return Polygon(points)
            } catch (e: NumberFormatException) {
                throw e
            }

        }
    }
}