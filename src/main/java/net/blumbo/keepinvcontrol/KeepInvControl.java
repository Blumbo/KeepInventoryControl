package net.blumbo.keepinvcontrol;

import com.google.gson.Gson;
import net.blumbo.keepinvcontrol.commands.KeepInvControlCmd;
import net.blumbo.keepinvcontrol.misc.KeepInvListData;
import net.blumbo.keepinvcontrol.misc.KeepInvSaveData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class KeepInvControl implements ModInitializer {

    public static String file = "keepinvcontrol.json";
    private static Path getSaveFolder(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ADVANCEMENTS).getParent();
    }
    private static Path getSaveFile(MinecraftServer server) {
        return getSaveFolder(server).resolve(file);
    }


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(KeepInvControlCmd::register);
    }

    public static boolean dropExp(World world) {
        return (world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && KeepInvListData.blacklist.expListed) ||
                (!world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !KeepInvListData.whitelist.expListed);
    }

    public static void save(MinecraftServer server) {
        try {
            Gson gson = new Gson();
            String saveString = gson.toJson(KeepInvSaveData.getSaveData());
            BufferedWriter writer = new BufferedWriter(new FileWriter(getSaveFile(server).toString()));

            writer.write(saveString);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(MinecraftServer server) {
        KeepInvListData.clear();
        String file = getFile(server);

        Gson gson = new Gson();
        KeepInvSaveData data = gson.fromJson(file, KeepInvSaveData.class);
        if (data != null) KeepInvSaveData.loadSaveData(data);
    }

    private static String getFile(MinecraftServer server) {
        try {
            Files.createDirectories(getSaveFolder(server));
            Path path = getSaveFile(server);

            if (!Files.exists(path)) return null;
            return Files.readString(path);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
