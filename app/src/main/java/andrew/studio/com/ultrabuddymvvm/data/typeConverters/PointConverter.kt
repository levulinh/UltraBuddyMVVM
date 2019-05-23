package andrew.studio.com.ultrabuddymvvm.data.typeConverters

import andrew.studio.com.ultrabuddymvvm.data.entity.Point
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList


class PointConverter {


    private var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Point> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Point>>() {
        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Point>): String {
        return gson.toJson(someObjects)
    }
}
