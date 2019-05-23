package andrew.studio.com.ultrabuddymvvm.data.typeConverters

import andrew.studio.com.ultrabuddymvvm.data.entity.Polygon
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList


class PolygonConverter {
    private var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Polygon> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Polygon>>() {
        }.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Polygon>): String {
        return gson.toJson(someObjects)
    }

}