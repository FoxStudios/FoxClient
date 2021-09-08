package net.ddns.rootrobo.foxclient.client;

import net.ddns.rootrobo.foxclient.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;

public class Client {
    public static List<Long> clicks_right = new ArrayList<>();
    public static List<Long> clicks_left = new ArrayList<>();

    private static long lastPing = System.currentTimeMillis();

    private static int PING = 69;

    private static Field fps_field = null;

    public static double mouseX = 0;
    public static double mouseY = 0;

    public static int getLeftCPS() {
        clicks_left.removeIf(clickTime -> System.currentTimeMillis() - clickTime >= 1000);
        return clicks_left.size();
    }

    public static int getRightCPS() {
        clicks_right.removeIf(clickTime -> System.currentTimeMillis() - clickTime >= 1000);
        return clicks_right.size();
    }

    public static int getPing() {
        if(MinecraftClient.getInstance().isInSingleplayer()) {
            return 0;
        }

        if(System.currentTimeMillis() - lastPing > 2000) {
            lastPing = System.currentTimeMillis();
            PING = ping();
        }
        return PING;
    }

    public static int getFPS() {
        int fps = 0;
        try {
            if(fps_field == null) {
                System.out.println("GET FPS FIELD");
                String fps_field_name;
                if (FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals("named")) {
                    fps_field_name = "currentFps";
                } else {
                    fps_field_name = "field_1738";
                }
                fps_field = MinecraftClient.class.getDeclaredField(fps_field_name);
                fps_field.setAccessible(true);
            }

            fps = (int) fps_field.get(MinecraftClient.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // fallback method
            System.out.println("could not get fps - trying fallback method");

            String debugFpsString = MinecraftClient.getInstance().fpsDebugString;
            debugFpsString = debugFpsString.substring(0, debugFpsString.indexOf(" "));

            try {
                fps = Integer.parseInt(debugFpsString);
            } catch (NumberFormatException ignored) {
            }
        }

        /*

        */
        return fps;
    }

    private static int ping() {
        int ping = 0;
        if(MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
            PlayerListEntry e = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
            if(e != null) {
                ping = e.getLatency();
            } else {
                ping = 0;
            }
        }
        return ping;
    }

    public static File cacheFileToConfig(InputStream stream, String name, boolean overwrite) throws IOException {
        if(stream == null) {
            System.out.println("STREAM NULL");
        }

        String configPath = FabricLoader.getInstance().getConfigDir().toString();
        if(configPath.endsWith(File.separatorChar+"."+File.separatorChar+"config")) {
            configPath = configPath.replace(File.separatorChar+"."+File.separatorChar+"config", File.separatorChar+"config");
        }
        Path dir = Paths.get(configPath + File.separatorChar + Main.MOD_ID, "cache");
        System.out.println(dir);

        //noinspection ResultOfMethodCallIgnored
        dir.toFile().mkdirs();

        Path target = Paths.get(dir.toString(), name);

        /*
        if(target.toFile().exists() && overwrite) {
            //noinspection ResultOfMethodCallIgnored
            target.toFile().delete();
        }
        */
        try {
            if(overwrite && stream != null) {
                Files.copy(Objects.requireNonNull(stream), target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                if(stream != null) {
                    Files.copy(Objects.requireNonNull(stream), target);
                }
            }
        } catch (FileAlreadyExistsException ignored) {
        }

        return new File(dir.toString()+File.separatorChar+name);
    }

    public static InputStream getStreamFromJar(String name) {
        String filePath = null;
        try {
            filePath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(filePath == null) return null;

        InputStream stream = null;

        try {
            JarFile jar = new JarFile(filePath);
            stream = jar.getInputStream(jar.getEntry(name));
        } catch (FileNotFoundException e) {
            filePath = filePath.replace(File.separator+"classes"+File.separator+"java"+File.separator+"main", File.separator+"resources"+File.separator+"main");

            try {
                stream = new FileInputStream(new File(filePath, name));
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream;
    }

    public static String getJarPath() {
        String jarPath = null;
        try {
            jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println(jarPath);

        if(jarPath == null) {
            System.out.println("No JAR! (Path not found)");
            return null;
        }
        if(!jarPath.endsWith(".jar")) {
            System.out.println("No JAR! (Dev environment)");
            return null;
        }
        return jarPath;
    }
}
