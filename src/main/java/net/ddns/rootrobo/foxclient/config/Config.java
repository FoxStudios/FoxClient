package net.ddns.rootrobo.foxclient.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.rootrobo.foxclient.Main;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

// i literally copy-pasted this from https://github.com/frankred/json-config-file because I am lazy
// (it is heavily modified tho)
public class Config {
    public static File configDir = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), Main.MOD_ID).toFile();
    private String configFile;

    private static Config instance;
    private static ConfigData data;

    public static Config getInstance() {
        data = fromFile(Paths.get(configDir.toString(), instance.configFile).toFile());
        if (data == null) {
            data = fromDefaults();
        }

        return instance;
    }

    public static ConfigData getData() {
        return data;
    }

    private static void load(File file) {
        data = fromFile(file);
        //instance = fromFile(file);

        if (instance == null) {
            instance = new Config();
        }

        if (data == null) {
            data = fromDefaults();
        }
    }

    public static void load(String file) {
        load(Paths.get(configDir.toString(), file).toFile());
        instance.configFile = file;
    }

    private static ConfigData fromDefaults() {
        return new ConfigData();
    }

    public void toFile(String file) {
        toFile(Paths.get(configDir.toString(), file).toFile());
    }

    private void toFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonConfig = gson.toJson(data.things);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ConfigData fromFile(File configFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile)));
            @SuppressWarnings("UnstableApiUsage")
            Type type = new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType();
            return new ConfigData(gson.fromJson(reader, type));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    // config set methods
    public void set(String name, Object value) {
        data.things.put(name, value);
        instance.toFile(instance.configFile);
    }

    public boolean getBoolean(String name) {
        Object d = getObject(name);
        if (d != null) {
            try {
                return (boolean) d;
            } catch (ClassCastException ignored) {
                return false;
            }
        }
        return false;
    }

    public String getString(String name) {
        Object d = getObject(name);
        if (d != null) {
            try {
                return (String) d;
            } catch (ClassCastException ignored) {
                return null;
            }
        }
        return null;
    }

    public Object getObject(String name) {
        return data.things.get(name);
    }
}