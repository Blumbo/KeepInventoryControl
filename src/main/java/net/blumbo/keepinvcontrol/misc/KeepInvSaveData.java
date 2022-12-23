package net.blumbo.keepinvcontrol.misc;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class KeepInvSaveData {

    public String[] whitelist;
    public Boolean expWhitelisted;
    public String[] blacklist;
    public Boolean expBlacklisted;

    private KeepInvSaveData() {}

    public static KeepInvSaveData getSaveData() {
        KeepInvSaveData data = new KeepInvSaveData();
        data.whitelist = getStringArray(KeepInvListData.whitelist.list);
        data.blacklist = getStringArray(KeepInvListData.blacklist.list);
        data.expWhitelisted = KeepInvListData.whitelist.expListed;
        data.expBlacklisted = KeepInvListData.blacklist.expListed;
        return data;
    }

    private static String[] getStringArray(ArrayList<Item> itemList) {
        String[] array = new String[itemList.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = Registries.ITEM.getId(itemList.get(i)).toString();
        }
        return array;
    }

    public static void loadSaveData(KeepInvSaveData data) {
        ArrayList<Item> itemList = getItemList(data.whitelist);
        if (itemList != null) KeepInvListData.whitelist.list = itemList;
        KeepInvListData.whitelist.expListed = data.expWhitelisted;

        itemList = getItemList(data.blacklist);
        if (itemList != null) KeepInvListData.blacklist.list = itemList;
        KeepInvListData.blacklist.expListed = data.expBlacklisted;
    }

    private static ArrayList<Item> getItemList(String[] array) {
        if (array == null) return null;
        ArrayList<Item> itemList = new ArrayList<>();
        for (String itemName : array) {
            Identifier identifier = new Identifier(itemName);
            Item item = Registries.ITEM.get(identifier);
            if (item != Items.AIR) itemList.add(item);
        }
        return itemList;
    }


}
