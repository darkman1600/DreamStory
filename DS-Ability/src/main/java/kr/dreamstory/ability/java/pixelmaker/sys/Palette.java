package kr.dreamstory.ability.java.pixelmaker.sys;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum Palette {

    COLOR_WHITE(ChatColor.of("#FFFFFF"), Material.WHITE_DYE, 1, 0),
    COLOR_BLACK(ChatColor.of("#000000"), Material.WHITE_DYE, 2, 1),
    COLOR_YELLOW(ChatColor.of("#F7FE2E"), Material.WHITE_DYE, 3, 2),
    COLOR_LIGHT_GREEN(ChatColor.of("#80FF00"), Material.WHITE_DYE, 4, 3),
    COLOR_GREEN(ChatColor.of("#2EFE64"), Material.WHITE_DYE, 5, 4),
    COLOR_LIGHT_BLUE(ChatColor.of("#2EFEF7"), Material.WHITE_DYE, 6, 5),
    COLOR_BLUE(ChatColor.of("#0080FF"), Material.WHITE_DYE, 7, 6),
    COLOR_DARK_BLUE(ChatColor.of("#0000FF"), Material.WHITE_DYE, 8, 7),
    COLOR_INDIGO(ChatColor.of("#29088A"), Material.WHITE_DYE, 9, 8),
    COLOR_CYAN(ChatColor.of("#46c282"), Material.WHITE_DYE, 10, 9),
    COLOR_PUPLE(ChatColor.of("#7401DF"), Material.WHITE_DYE, 11, 10),
    COLOR_PINK(ChatColor.of("#DF01D7"), Material.WHITE_DYE, 12, 11),
    COLOR_RED(ChatColor.of("#DF0174"), Material.WHITE_DYE, 13, 12),
    COLOR_DARK_RED(ChatColor.of("#610B21"), Material.WHITE_DYE, 14, 13),
    COLOR_DEFENSE(ChatColor.of("#424242"), Material.WHITE_DYE, 15, 14),
    COLOR_LIGHT_PINK(ChatColor.of("#F78181"), Material.WHITE_DYE,16,15),
    COLOR_ORANGE(ChatColor.of("#FF8000"), Material.WHITE_DYE, 17, 16);

    ChatColor color;
    Material m;
    int custom_model;
    int index;

    Palette(ChatColor color, Material m, int custom_model, int index) {
        this.color = color;
        this.m = m;
        this.custom_model = custom_model;
        this.index = index;
    }

    @NotNull
    public Palette next() { return indexOf(index+1); }

    public ChatColor getColor() { return color; }
    public Material getMaterial() { return m; }
    public int getCustom_model() { return custom_model; }
    public int getIndex() { return index; }

    public static Palette colorOf(ChatColor color) {
        for(Palette p : Palette.values()) { if(color.equals(p.color)) return p; }
        return Palette.COLOR_WHITE;
    }

    public static Palette indexOf(int index) {
        if(index > 16) index = 0;
        if(index < 0) index = 0;
        for(Palette p : Palette.values()) { if(p.index == index) return p; }
        return null;
    }


}
