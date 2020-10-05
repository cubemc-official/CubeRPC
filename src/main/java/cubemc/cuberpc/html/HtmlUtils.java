package cubemc.cuberpc.html;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cubemc.cuberpc.CubeLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HtmlUtils {

    public static final String BASE_URL = "http://cubedmc.eu/api/";

    public static String postResponse(String restUrl) throws IOException {
        return postResponse(restUrl, Collections.emptyList());
    }

    public static String postResponse(String restUrl, List<NameValuePair> params) throws IOException {
        if (restUrl.startsWith("/")){
            restUrl = restUrl.substring(1);
        }

        HttpPost post = new HttpPost(BASE_URL+restUrl);
        if (!params.isEmpty()){
            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        }

        HttpResponse response = HttpClients.createDefault().execute(post);
        HttpEntity entity = response.getEntity();
        return entity == null? null : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    public static String getPlayerName(String discordTag){
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("user", discordTag));

        try {
            String response = postResponse("discord-user", params);
            if (response == null) return null;

            JsonElement json = JsonParser.parseString(response);
            if (json.isJsonObject()){
                return json.getAsJsonObject().get("playername").getAsString();
            }
        }catch (IOException e){
            CubeLogger.getInstance().error("Error appeared while loading player name!", e);
        }
        return null;
    }

    public static JsonObject getPlayerData(String playerName){
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("playername", playerName));
        params.add(new BasicNameValuePair("allinfo", "false"));

        try {
            String response = postResponse("player-info", params);
            if (response == null) return null;

            JsonElement json = JsonParser.parseString(response);
            return json.isJsonObject()? json.getAsJsonObject() : null;
        }catch (IOException e){
            CubeLogger.getInstance().error("Error appeared while loading player name!", e);
        }
        return null;
    }
}
