package cubemc.cuberpc.previews;

import com.google.gson.JsonObject;
import cubemc.cuberpc.CubeRPC;
import cubemc.cuberpc.html.HtmlUtils;
import net.arikia.dev.drpc.DiscordRichPresence;

public class ServerPreview extends RichPreview{

    private String server;

    public ServerPreview(String server){
        super("Server Preview");
        this.setServer(server);
    }

    @Override
    public void update(CubeRPC loader) {
        JsonObject playerData = HtmlUtils.getPlayerData(loader.getPlayerName());
        if (playerData == null || !playerData.get("online").getAsBoolean()){
            this.online = false;
        }else {
            this.online = true;
            String server = playerData.get("server").getAsString();
            this.setServer(server);
        }

        this.send();
    }

    @Override
    public DiscordRichPresence.Builder getBuilder() {
        if (this.server == null){
            return super.getBuilder();
        }

        String name = this.server.contains("Lobby")? "Waiting in Lobby!" : "Playing in "+this.server;
        return super.getBuilder(name);
    }

    public void setServer(String server) {
        this.server = server == null? null : server.substring(0, 1).toUpperCase() + server.substring(1);
    }

    public String getServer() {
        return this.server;
    }
}
