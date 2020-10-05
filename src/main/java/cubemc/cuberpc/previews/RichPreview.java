package cubemc.cuberpc.previews;

import cubemc.cuberpc.CubeRPC;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public abstract class RichPreview {

    protected String name;
    protected String image = null;
    protected Long timestamp;

    protected boolean online = false;
    protected String customStatus = null;

    public RichPreview(String name){
        this.name = name;
        this.timestamp();
    }

    public abstract void update(CubeRPC loader);


    public DiscordRichPresence.Builder getBuilder(){
        return this.getBuilder("");
    }

    public DiscordRichPresence.Builder getBuilder(String state){
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(this.online? state : this.customStatus);
        builder.setDetails("Join at cubedmc.eu");
        builder.setBigImage("logo-original", "cubedmc.eu");

        if (this.timestamp != null){
            builder.setStartTimestamps(timestamp);
        }
        return builder;
    }

    public void send(){
        if (!this.online && this.customStatus == null){
            DiscordRPC.discordClearPresence();
            this.clearTimestamp();
            return;
        }

        if (this.timestamp == null){
            this.timestamp();
        }

        DiscordRichPresence.Builder builder = this.getBuilder();
        DiscordRPC.discordUpdatePresence(builder.build());
    }

    public void timestamp(){
        this.timestamp = System.currentTimeMillis();
    }

    public void clearTimestamp(){
        this.timestamp = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setCustomStatus(String customStatus) {
        this.customStatus = customStatus;
    }

    public String getCustomStatus() {
        return this.customStatus;
    }
}
