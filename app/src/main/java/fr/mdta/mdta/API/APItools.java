package com.sli.app.API;

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

/**
 * Created by sliphis-dev on 08/08/2017.
 */

public class APItools {

    public final static String formatAPI = "yyyy-MM-dd'T'HH:mm:ss";
    public final static String Male = "1";
    public final static String Female = "2";
    public final static int AddFriendRequestCode = 0;
    public final static int AcceptFriendRequestCode = 1;
    public final static int RefuseFriendRequestCode = 2;
    //TODO switch on the good server
//  public static final String URL_API_BASE = "https://apipr.sliphis.com/api/";
    public static String URL_API_BASE = "https://apidev.sliapp.co/api/";
    public static final String URL_API_GIFT_REQUESTER = URL_API_BASE + "amigo/presente";
    public static final String URL_API_BUY_VOUCHER_REQUESTER = URL_API_BASE + "market/buy";
    public static final String URL_API_ACCEPT_FRIEND_REQUESTER = URL_API_BASE + "amigo/atualizar";
    public static final String URL_API_REFUSE_FRIEND_REQUESTER = URL_API_BASE + "amigo/atualizar";
    public static final String URL_API_ADD_FRIEND_REQUESTER = URL_API_BASE + "amigo/adicionar";
    public static final String URL_API_FRIEND_LIST_REQUESTER = URL_API_BASE + "friends/list";
    public static final String URL_API_SIGNUP_USER_REQUESTER = URL_API_BASE + "usuario/signup";
    public static final String URL_API_CHANGE_PHOTO_USER_REQUESTER = URL_API_BASE + "usuario/changephoto";
    public static final String URL_API_CHANGE_COVER_PHOTO_USER_REQUESTER = URL_API_BASE + "usuario/changephotocover";
    public static final String URL_API_VERIFY_SMS_REQUESTER = URL_API_BASE + "sms/verify";
    public static final String URL_API_VERIFY_PROMOTIONCODE_REQUESTER = URL_API_BASE + "valida/code";
    public static final String URL_API_VERIFY_USER_REQUESTER = URL_API_BASE + "valida/user";
    public static final String URL_API_SENDSMS_REQUESTER = URL_API_BASE + "sms/send";
    public static final String URL_API_RETRIEVE_TOKEN_REQUESTER = URL_API_BASE + "security/token";
    public static final String URL_API_SEARCH_FRIEND_REQUESTER = URL_API_BASE + "amigo/pesquisar";
    public static final String URL_API_MY_VOUCHER_REQUESTER = URL_API_BASE + "vale/myvouchers";
    public static final String URL_API_FRIEND_DISCOUNT_CODE_REQUESTER = URL_API_BASE + "amigo/codigo";
    public static final String URL_API_LOGIN_REQUESTER = URL_API_BASE + "usuario/login";
    public static final String URL_API_LOCKSCREEN_REQUESTER = URL_API_BASE + "lock/screen";
    public static final String URL_API_USER_OFFER_ACTION_REQUESTER = URL_API_BASE + "oferta/usuario";
    public static final String URL_API_USER_NEWS_ACTION_REQUESTER = URL_API_BASE + "news/usuario";
    public static final String URL_API_LIGHTNING_OFFERS_REQUESTER = URL_API_BASE + "oferta/lightningOffers";
    public static final String URL_API_SLI_PRIME_REQUESTER = URL_API_BASE + "lock/sliprime";
    public static final String URL_API_NEWS_REQUESTER = URL_API_BASE + "news/listNews";
    public static final String URL_API_INDICATIONS_REQUESTER = URL_API_BASE + "usuario/indications";
    public static final String URL_API_MARKET_REQUESTER = URL_API_BASE + "market/list";
    public static final String URL_API_VOUCHER_REQUESTER = URL_API_BASE + "market/vouchers";
    public static final String URL_API_GET_PREFERENCES_REQUESTER = URL_API_BASE + "preferences/getpreferences";
    public static final String URL_API_SEND_PREFERENCES_REQUESTER = URL_API_BASE + "usuario/preferences";
    public static final String URL_API_CHANGE_CPF_REQUESTER = URL_API_BASE + "usuario/changecpf";
    public static final String URL_API_GET_SLI_POINTS_REQUESTER = URL_API_BASE + "usuario/pontuacao";
    public static final String URL_API_GET_USER_PHOTO_REQUESTER = URL_API_BASE + "usuario/photo";
    public static final String URL_API_GET_COVER_PHOTO_REQUESTER = URL_API_BASE + "usuario/coverphoto";
    public static final String URL_API_CHANGE_REGISTER_ID_REQUESTER = URL_API_BASE + "usuario/changeregid";



    public static String convertObjectToJSONString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

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
