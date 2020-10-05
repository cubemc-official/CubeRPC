package cubemc.cuberpc;

import cubemc.cuberpc.html.HtmlUtils;
import cubemc.cuberpc.previews.RichPreview;
import cubemc.cuberpc.previews.ServerPreview;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import org.apache.commons.cli.*;

import java.util.concurrent.CompletableFuture;

public class CubeRPC {

    public static final String APP_ID = "607642019158032416";

    public static void main(String[] args) {
        CubeLogger logger = new CubeLogger();
        logger.info("Starting CubeRPC ...");

        Options options = new Options();
        Option dev = new Option("dev", false, "Enable development mode");
        dev.setRequired(false);
        Option status = new Option("custom", true, "Set custom idle status");
        status.setRequired(false);

        options.addOption(dev);
        options.addOption(status);

        CommandLine cmd;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        }catch (ParseException e){
            logger.error("Can not parse options!", e);
            return;
        }

        boolean debug = cmd.hasOption("dev");
        String customStatus = null;
        if (cmd.hasOption("custom")){
            customStatus = cmd.getOptionValue("custom").replace("_", " ");
            logger.info("Setting custom status to: "+customStatus);
        }

        try {
            CubeRPC app = new CubeRPC(logger, customStatus);
        }catch (Exception e){
            logger.error("Exiting application! Error appeared!", e);
        }
    }

    private final CubeLogger logger;
    private RichPreview richPreview;

    private String playerName = null;

    private boolean shutdown = false;
    private boolean debug = false;
    private long currentTick = 0;
    private long nextTick = 0;

    private long updateTick = -1;

    public CubeRPC(CubeLogger logger, String customStatus){
        this.logger = logger;
        this.richPreview = this.constructPreview();
        this.richPreview.setCustomStatus(customStatus);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.shutdown();
            DiscordRPC.discordShutdown();
        }));

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            String discordTag = user.username+"#"+user.discriminator;
            this.logger.info("Connected as "+discordTag+"!");

            String playerName = HtmlUtils.getPlayerName(discordTag);
            if (playerName == null){
                this.logger.info("Can not find valid player name! Maybe your game account is not linked?");
                this.shutdown();
            }else {
                this.logger.info("Linking with player "+playerName+"!");
                this.playerName = playerName;
                this.updateTick = this.currentTick;
            }
        }).build();

        DiscordRPC.discordInitialize(APP_ID, handlers, false);
        DiscordRPC.discordRegister(APP_ID, "");
        this.tickProcessor();
    }

    private void tickProcessor(){
        this.nextTick = System.currentTimeMillis();

        while (!this.shutdown){
            long now = System.currentTimeMillis();
            long time = now - this.nextTick;

            if (time < 0){
                try {
                    Thread.sleep(Math.max(25, - time));
                }catch (Exception e){
                    //ignore
                }
            }

            this.currentTick += 1;
            DiscordRPC.discordRunCallbacks();
            this.onUpdate(this.currentTick);
            this.nextTick += 50;
        }
    }

    private boolean onUpdate(long currentTick){
        if (this.updateTick < 0 || (this.updateTick - currentTick) > 0){
            return false;
        }

        CompletableFuture.runAsync(() -> this.richPreview.update(this));
        this.updateTick = currentTick + 400;
        return true;
    }

    public RichPreview constructPreview(){
        return new ServerPreview(null);
    }

    public void shutdown(){
        this.logger.info("Shutting down...");
        this.shutdown = true;
    }

    public CubeLogger getLogger() {
        return this.logger;
    }

    public RichPreview getRichPreview() {
        return this.richPreview;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
