package net.foxes4life.foxclient.egg;

import joptsimple.internal.Strings;
import maow.owo.OwO;
import org.apache.commons.lang3.StringUtils;

public class UwUifier {
    public static void main(String[] args) {
        String in = "This is some text lol I have no hobbies whatsoever xd";
        System.out.println(in);

        System.out.println(UwUify(in));

        System.out.println(OwOify(in));
    }

    public static String OwOify(String in) {
        String out = OwO.INSTANCE.translate(in);

        String[] parts = out.split(" ");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            // replace emotes
            part = OwOifyEmote(part);

            parts[i] = part;
        }
        out = Strings.join(parts, " ");

        out = out
                .replace("th", "d")
                .replace("TH", "D")
                .replace("Th", "D")
                .replace("tH", "d")

                .replace("OVE", "UV")
                .replace("OVe", "UV")
                .replace("Ove", "Uv")
                .replace("OvE", "Uv")
                .replace("oVE", "uV")
                .replace("oVe", "uV")
                .replace("ove", "uv")
                .replace("ovE", "uv")

                .replace(">.<", ">w<")
                .replace("<.<", "<w<")
                .replace(">.>", ">w>");

        out = out.replace("R", "W");
        out = out.replace("L", "W");

        return out;
    }

    public static String OwOifyEmote(String part) {
        if(part.toLowerCase().startsWith("x") && part.toLowerCase().endsWith("d")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, "x", ""), "d", "");
            if(c.length() == 0) {
                part = part.replace("d", "3");
                part = part.replace("D", "3");
            }
        }

        if(part.startsWith(":") && part.toLowerCase().endsWith("d")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ":", ""), "d", "");
            if(c.length() == 0) {
                part = part.replace("d", "3");
                part = part.replace("D", "3");
            }
        }

        if(part.startsWith(":") && part.endsWith(")")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ":", ""), ")", "");
            if(c.length() == 0) {
                part = part.replace(")", "3");
            }
        }

        if(part.startsWith("(") && part.endsWith(":")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, "(", ""), ":", "");
            if(c.length() == 0) {
                StringBuilder pb = new StringBuilder();
                pb.append(":");
                for (int i1 = 0; i1 < part.replace(":", "").length(); i1++) {
                    pb.append("3");
                }
                part = pb.toString();
            }
        }

        if(part.startsWith(":") && part.endsWith("(")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ":", ""), "(", "");
            if(c.length() == 0) {
                part = "qwq";
            }
        }

        if(part.startsWith(")") && part.endsWith(":")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ")", ""), ":", "");
            if(c.length() == 0) {
                part = "qwq";
            }
        }

        if(part.startsWith(":") && part.toLowerCase().endsWith("c")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ":", ""), "c", "");
            if(c.length() == 0) {
                if(part.endsWith("c")) {
                    part = "qwq";
                } else {
                    part = "QwQ";
                }
            }
        }

        if(part.startsWith(":") && part.toLowerCase().endsWith("o")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, ":", ""), "o", "");
            if(c.length() == 0) {
                if(part.endsWith("o")) {
                    part = "owo";
                } else {
                    part = "OwO";
                }
            }
        }

        if(part.toLowerCase().startsWith("c") && part.toLowerCase().endsWith(":")) {
            String c = StringUtils.replaceIgnoreCase(StringUtils.replaceIgnoreCase(part, "c", ""), ":", "");
            if(c.length() == 0) {
                StringBuilder pb = new StringBuilder();
                pb.append(":");
                for (int i1 = 0; i1 < part.replace(":", "").length(); i1++) {
                    pb.append("3");
                }
                part = pb.toString();
            }
        }

        return part;
    }

    private static String UwUify(String string) {
        String[] words = string.split(" ");

        for (int i = 0; i < words.length; i++) {
            if(!words[i].equalsIgnoreCase("owo")) {
                String word = words[i];
                String[] word_split = word.split("");
                for (int i1 = 0; i1 < word_split.length; i1++) {
                    word_split[i1] = OwOfyLetter(word_split[i1], "r", "w");
                    word_split[i1] = OwOfyLetter(word_split[i1], "l", "w");
                }
                words[i] = Strings.join(word_split, "");

                words[i] = words[i]
                        .replace("OVE", "UV")
                        .replace("OVe", "UV")
                        .replace("Ove", "Uv")
                        .replace("OvE", "Uv")
                        .replace("oVE", "uV")
                        .replace("oVe", "uV")
                        .replace("ove", "uv")
                        .replace("ovE", "uv");

                words[i] = words[i]
                        .replace("th", "d")
                        .replace("TH", "D")
                        .replace("Th", "D")
                        .replace("tH", "d");

                words[i] = words[i].replace(":)", ":3")
                        .replace("(:", ":3")
                        .replace(":(", ";-;")
                        .replace(":o", "owo")
                        .replace(":O", "OwO")
                        .replace(":D", ":3")
                        .replace("xD", "x3")
                        .replace("XD", "X3")
                        .replace("Xd", "X3")
                        .replace("xd", "x3")
                        .replace(">.<", ">w<")
                        .replace("<.<", "<w<")
                        .replace(">.>", ">w>");

            }
        }
        return Strings.join(words, " ");
    }

    private static String OwOfyLetter(String letter, String src, @SuppressWarnings("SameParameterValue") String trg) {
        if(letter.length() > 1) return letter;
        if(letter.equalsIgnoreCase(src)) {
            if(letter.toUpperCase().equals(letter)) {
                letter = trg.toUpperCase();
            } else {
                letter = trg.toLowerCase();
            }
        }
        return letter;
    }
}