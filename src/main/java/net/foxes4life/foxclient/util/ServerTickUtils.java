package net.foxes4life.foxclient.util;

import com.google.common.collect.EvictingQueue;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class ServerTickUtils {
    public static EvictingQueue<Float> clientTicks = EvictingQueue.create(20);
    public static EvictingQueue<Float> serverTPS = EvictingQueue.create(3);
    public static EvictingQueue<Float> serverTPS_last = EvictingQueue.create(6);

    private static long systemTime1 = 0;
    private static long systemTime2 = 0;
    private static long serverTime = 0;

    public static void onJoin() {
        clientTicks.clear();
        serverTPS.clear();
        systemTime1 = 0;
        systemTime2 = 0;
        serverTime = 0;
    }

    public static void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
        //System.out.println("a "+packet.getTime());
        if (systemTime1 == 0) {
            systemTime1 = System.currentTimeMillis();
            serverTime = packet.getTime();
        } else {
            long newSystemTime = System.currentTimeMillis();
            long newServerTime = packet.getTime();

            float tps = (((float) (newServerTime - serverTime)) / (((float) (newSystemTime - systemTime1)) / 50.0f)) * 20.0f;
            if(tps > 20) tps = 20;
            //System.out.println(newServerTime+"-"+serverTime+" "+newSystemTime+"-"+systemTime1+" ... = "+tps);
            serverTPS.add(tps);
            systemTime1 = newSystemTime;
            serverTime = newServerTime;

            float currentTPS = calculateServerTPS();
            long currentTime = System.currentTimeMillis()/1000;
        }
    }

    public static void onClientTick() {
        systemTime2 = System.currentTimeMillis();
    }

    public static void onClientTickEnd() {
        long newSystemTime = System.currentTimeMillis();
        float newClientTick = ((float) newSystemTime) - systemTime2;
        clientTicks.add(newClientTick);
    }

    public static float calculateServerTPS() {
        float sum = 0.0f;
        for (Float f : serverTPS) {
            sum += f;
        }

        serverTPS_last.add(sum / (float) serverTPS.size());

        float tpsSum = 0.0f;
        for (Float f : serverTPS_last) {
            tpsSum += f;
        }

        return Math.round((tpsSum / (float) serverTPS_last.size()) * 10.0) / 10.0f;
    }
}
