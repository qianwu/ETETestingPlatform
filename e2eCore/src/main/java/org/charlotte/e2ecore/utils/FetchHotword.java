package org.charlotte.e2ecore.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * SecurityContextUtils is used to get username and roles to set created by, last updated by fields.
 */
@Component
public class FetchHotword {

    private static final Logger logger = LoggerFactory.getLogger(FetchHotword.class);

    private FetchHotword() {
    }

    public static String fetchHotword(String username, String password) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();

        MediaType mediaType = MediaType.parse("application/json");
        String content = String.format("{\"username\": \"%s\", \"password\": \"%s\" }", username, password);

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://metabaseurl/api/session")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
//        logger.info("get metabase session request:");
//        logger.info(request.toString());
        Response response = client.newCall(request).execute();
//        logger.info("get metabase session res:");
//        logger.info(response.toString());
        String strRes;
        strRes = response.body().string();
        System.out.println(strRes);
//        logger.info("response body: ");
//        logger.info(strRes);
        JsonObject jsonObject = new JsonParser().parse(strRes).getAsJsonObject();
        String id = jsonObject.get("id").toString().replaceAll("\"", "");
        logger.info("get id success");

        RequestBody body2 = RequestBody.create(mediaType, "{}");
        Request request2 = new Request.Builder()
                .url("https://metabase/api/card/449/query")
                .method("POST", body2)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Metabase-Session", id)
                .build();
        Response response2 = client.newCall(request2).execute();
        logger.info("query metabase res:", response2);

        JsonObject jsonObject2 = new JsonParser().parse(response2.body().string()).getAsJsonObject();
        String res2 = jsonObject2.get("data").getAsJsonObject().get("rows").toString();
        String str = res2.replaceAll("\"", "").replaceAll("\\[", "");
//        System.out.println(str.replaceAll("],", "   "));

        return str.replaceAll("],", "   ");
    }

}