package net.blumbo.keepinvcontrol.misc;

import net.minecraft.item.Item;

import java.util.ArrayList;

public class KeepInvListData {

    public static KeepInvListData whitelist;
    public static KeepInvListData blacklist;

    public ArrayList<Item> list = new ArrayList<>();
    public Boolean expListed = false;
    public String name;

    public KeepInvListData(String name) {
        this.name = name;
    }

    static {
        clear();
    }

    public static void clear() {
        whitelist = new KeepInvListData("whitelist");
        blacklist = new KeepInvListData("blacklist");
    }

    public static KeepInvListData get(boolean whitelist) {
        if (whitelist) return KeepInvListData.whitelist;
        return blacklist;
    }
}
