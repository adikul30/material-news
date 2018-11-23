package kulkarni.aditya.materialnews.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import kulkarni.aditya.materialnews.model.SourceInfo;

public class MyTypeConverter {
    @TypeConverter
    public String fromSourceInfo(SourceInfo sourceInfo) {
        Type type = new TypeToken<SourceInfo>() {
        }.getType();
        String json = new Gson().toJson(sourceInfo, type);
        return json;
    }

    @TypeConverter
    public SourceInfo toSourceInfo(String sourceInfoString) {
        Gson gson = new Gson();
        Type type = new TypeToken<SourceInfo>() {
        }.getType();
        SourceInfo productCategoriesList = gson.fromJson(sourceInfoString, type);
        return productCategoriesList;
    }
}
