package kr.dreamstory.ability.java.pixelmaker.sys;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class DrawIcon {

    private static String[] unicode;
    private static final String LEFT_ESC = "\uF801";
    static {
        unicode = new String[7];
        unicode[6] = "\u221a";
        unicode[5] = "\u2030";
        unicode[4] = "\u00ad";
        unicode[3] = "\u00b7";
        unicode[2] = "\u20b4";
        unicode[1] = "\u2260";
        unicode[0] = "\u00bf";
    }

    public static String replaceUniCode(String data) {
        for(String s : unicode) data = data.replace(s,"");
        return data;
    }

    private int index;
    private Palette palette;
    private String res;

    public DrawIcon(int index) {
        this.index = index;
        res = unicode[index % 7];
        palette = Palette.COLOR_WHITE;
    }

    public DrawIcon(String data,int index) {
        this.index = index;
        res = unicode[index % 7];
        try {
            palette = Palette.colorOf(ChatColor.of(data));
        } catch (Exception e){
            palette = Palette.COLOR_WHITE;
        }
    }

    public void removeColor() { palette = Palette.COLOR_WHITE; }
    public void setColor(@NotNull Palette p) { this.palette = p; }
    public Palette getPalette() { return palette; }

    public String toString() { return palette.color+res; }

}
