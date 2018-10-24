package com.mvp.testmvp.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.mvp.testmvp.bean.MyHttpResponse;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.*;
import java.nio.charset.Charset;


public class CheckGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;
    private final Gson mGson;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    CheckGsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.mGson = new Gson();
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        MyHttpResponse httpStatus = mGson.fromJson(response, MyHttpResponse.class);
        if (httpStatus.getCode() != ApiCode.SUCCESS) {
            value.close();
            throw new ApiException(response);
        }
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = mGson.newJsonReader(reader);

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }


}
