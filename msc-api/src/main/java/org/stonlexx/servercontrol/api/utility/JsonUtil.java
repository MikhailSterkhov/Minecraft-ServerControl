package org.stonlexx.servercontrol.api.utility;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    @Getter
    private final Gson jsonManager = new Gson();


    /**
     * Преобразовать объект в JSON
     *
     * @param object - объект
     */
    public String toJson(Object object) {
        return jsonManager.toJson(object);
    }

    /**
     * Преобразовать JSON обратно в объект
     *
     * @param json - JSON
     * @param clazz - класс объекта
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return jsonManager.fromJson(json, clazz);
    }

}
