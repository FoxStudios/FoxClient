package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

public class MiscUtil {
    public static InputStream getInputStreamFromModJar(String name) {
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
}
