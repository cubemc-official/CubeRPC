package cubemc.cuberpc;


public class CubeLogger {

    private static CubeLogger instance;

    public CubeLogger(){
        if (instance == null){
            instance = this;
        }
    }

    public static CubeLogger getInstance() {
        return instance;
    }

    public void info(String message){
        System.out.println("[INFO] "+message);
    }

    public void error(String s, Throwable throwable) {
        System.out.println("[ERROR ]"+throwable.getLocalizedMessage());
        for (StackTraceElement element : throwable.getStackTrace()){
            System.out.println(element.toString());
        }
    }
}
