package net.foxes4life.foxclient.networking;

import com.google.common.collect.EvictingQueue;
import net.foxes4life.foxclient.config.Config;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ServerTickStuff {
    // https://github.com/FliegendeWurst/ServerTPS <3 (ported to fabric and a little bit modified)
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

    public static File TPS_LOG = Paths.get(Config.configDir.getAbsolutePath(), "tps_log.txt").toFile();

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

            new Thread(() -> {
                try {
                    if(!TPS_LOG.exists()) {
                        if(!TPS_LOG.createNewFile()) {
                            System.out.println("COULD NOT CREATE "+TPS_LOG.getAbsolutePath());
                            return;
                        }
                    }
                    FileWriter fw = new FileWriter(TPS_LOG.getAbsolutePath(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(currentTime+":"+currentTPS);
                    bw.newLine();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void onClientTick() {
        systemTime2 = System.currentTimeMillis();
    }

    public static void onClientTickEnd() {
        long newSystemTime = System.currentTimeMillis();
        float newClientTick = ((float) newSystemTime) - systemTime2;
        //System.out.printf("adding %s to client TPS [%s, %s]\n", newClientTick, systemTime2, newSystemTime);
        //System.out.printf("client tick took: %s ms\n", newSystemTime - systemTime2);
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
