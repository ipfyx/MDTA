package fr.mdta.mdta.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APItools {
    //API specific JSON format
    private final static String formatAPI = "yyyy-MM-dd'T'HH:mm:ss";

    //API URLs
    private final static String URL_API_BASE = APIconf.URL_API_SERVER;
    public final static String URL_API_BASIC_SCAN = URL_API_BASE + "/permissions/basicscan";

    /**
     * Convert every Object into JSON string
     * @param object
     * @return JSONString corresponding to object
     */
    public static String convertObjectToJSONString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * Convert every JSON String into Object
     * @param jsonString JSON to convert
     * @param convertIn Target Class
     * @return Instance of the target class object
     */
    public static <T> T convertJSONToObject(String jsonString, Class<T> convertIn) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            DateFormat df = new SimpleDateFormat(formatAPI);
            @Override
            public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonString, convertIn);
    }
}
