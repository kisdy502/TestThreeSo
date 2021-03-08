package com.sdt.testthreeso.net;


import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 仅支持json和String返回格式去掉了xml返回格式的支持
 */
public class JsonOrStringConverterFactory extends Converter.Factory {

    private final Converter.Factory jsonFactory = GsonConverterFactory.create();
    private final Converter.Factory stringFactory = StringConverterFactory.create();

    public static JsonOrStringConverterFactory create() {
        return new JsonOrStringConverterFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof ResponseFormat)) {
                continue;
            }
            String value = ((ResponseFormat) annotation).value();
            if (ResponseFormat.JSON.equals(value)) {
                return jsonFactory.responseBodyConverter(type, annotations, retrofit);
            }  else if (ResponseFormat.STRING.equals(value)) {
                return stringFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }

        return jsonFactory.responseBodyConverter(type, annotations, retrofit);
    }
}

