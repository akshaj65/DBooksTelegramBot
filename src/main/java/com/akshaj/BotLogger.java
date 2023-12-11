package com.akshaj;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotLogger {
    private static Logger logger;

    private BotLogger(){

    }

    private static Logger getLogger(){
        if(null==logger){
            logger= LoggerFactory.getLogger(BotLogger.class.getName());
        }
        return logger;
    }


    public static void logInfo(String msg){
        getLogger().info(msg);
        System.out.println(msg);
    }
    public static void logInfo(String msg,Throwable throwable){
        getLogger().info("Method Name: "+getMethodName(throwable)+"  Info: "+msg);
        System.out.println(msg);
    }
    public static void logError(String msg,Throwable throwable){
        getLogger().error("Method Name: "+getMethodName(throwable)+"  Error: "  +msg,throwable);
        System.out.println(msg);
    }

    public static void logDebug(String msg){
        getLogger().debug(msg);
        System.out.println(msg);
    }

    private static String getMethodName(Throwable throwable){
        return throwable.getStackTrace()[0].getMethodName();
    }
}
