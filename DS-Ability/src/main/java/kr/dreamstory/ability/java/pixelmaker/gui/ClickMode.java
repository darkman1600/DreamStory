package kr.dreamstory.ability.java.pixelmaker.gui;

import org.bukkit.Material;

public enum ClickMode {

    NORMAL("브러쉬", Material.PAPER,7),
    SPOID("스포이드", Material.PAPER,8),
    PAINT("페인트", Material.PAPER,9);

    Material m;
    int customModel;
    String name;

    ClickMode(String name, Material m, int customModel) {
        this.name = name;
        this.m = m;
        this.customModel = customModel;
    }

    public int getCustomModel() { return customModel; }
    public String getName() { return name; }
    public Material getMaterial() { return m; }

}
