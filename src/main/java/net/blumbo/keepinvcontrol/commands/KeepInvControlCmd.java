package net.blumbo.keepinvcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.blumbo.keepinvcontrol.misc.KeepInvListData;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class KeepInvControlCmd {

    private static final String command = "keepinvcontrol";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access,
                                CommandManager.RegistrationEnvironment environment) {

        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = CommandManager.literal(command);
        argumentBuilder.requires(source -> source.hasPermissionLevel(2));

        argumentBuilder.then(CommandManager.literal("info").executes(KeepInvControlCmd::info));
        argumentBuilder.executes(KeepInvControlCmd::info);

        addListToCommand(argumentBuilder, access, true);
        addListToCommand(argumentBuilder, access, false);

        dispatcher.register(argumentBuilder);
    }

    private static void addListToCommand(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder,
                                         CommandRegistryAccess access, boolean whitelistMode) {
        KeepInvListData listData = KeepInvListData.get(whitelistMode);

        argumentBuilder.then(CommandManager.literal(listData.name)

                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("item", ItemStackArgumentType.itemStack(access))
                                .executes(context -> add(context, whitelistMode))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("item", ItemStackArgumentType.itemStack(access))
                                .executes(context -> remove(context, whitelistMode))))

                .then(CommandManager.literal("addxp")
                        .executes(context -> addRemoveExp(context, whitelistMode, true)))
                .then(CommandManager.literal("removexp")
                        .executes(context -> addRemoveExp(context, whitelistMode, false)))

                .then(CommandManager.literal("list")
                        .executes(context -> list(context, whitelistMode)))
        );
    }

    private static int info(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        String string = "" +
                "\n §9§m        §9[ §bKeep Inventory Control §9]§9§m        " +
                "\n§7 Whitelisted items are kept upon death if keepInventory gamerule is set to §6false§7." +
                "\n§7 Blacklisted items are dropped upon death if keepInventory gamerule is set to §etrue§7." +
                "\n" +
                "\n§b /" + command + " whitelist/blacklist add/remove <item> §7to add or remove items" +
                "\n§b /" + command + " whitelist/blacklist addxp/removexp §7to add or remove experience" +
                "\n";

        source.sendMessage(Text.of(string));
        return 0;
    }

    private static int add(CommandContext<ServerCommandSource> context, boolean whitelistMode) {
        KeepInvListData listData = KeepInvListData.get(whitelistMode);
        ServerCommandSource source = context.getSource();
        Item item = context.getArgument("item", ItemStackArgument.class).getItem();

        if (!listData.list.contains(item)) {
            listData.list.add(item);
            source.sendMessage(Text.of("§b" + getItemString(item.toString()) + "§7 added to " + listData.name + " items."));
            return 0;
        }
        source.sendMessage(Text.of("§c" + getItemString(item.toString()) + "§7 already exists in " + listData.name + "ed items."));
        return 0;
    }

    private static int remove(CommandContext<ServerCommandSource> context, boolean whitelistMode) {
        KeepInvListData listData = KeepInvListData.get(whitelistMode);
        ServerCommandSource source = context.getSource();
        Item item = context.getArgument("item", ItemStackArgument.class).getItem();

        if (listData.list.remove(item)) {
            source.sendMessage(Text.of("§b" + getItemString(item.toString()) + "§7 removed from " + listData.name + " items."));
        } else {
            source.sendMessage(Text.of("§c" + getItemString(item.toString()) + "§7 is not a " + listData.name + "ed item."));
        }
        return 0;
    }

    private static int addRemoveExp(CommandContext<ServerCommandSource> context, boolean whitelistMode, boolean addMode) {
        KeepInvListData listData = KeepInvListData.get(whitelistMode);
        ServerCommandSource source = context.getSource();

        boolean currentValue = listData.expListed;
        if (currentValue == addMode) {
            source.sendMessage(Text.of("§cExperience §7is already " + listData.name + "ed"));
            return 0;
        }

        listData.expListed = addMode;

        if (addMode) source.sendMessage(Text.of("§eAdded experience §7to the " + listData.name));
        else source.sendMessage(Text.of("§6Removed experience §7from the " + listData.name));

        return 0;
    }

    private static int list(CommandContext<ServerCommandSource> context, boolean whitelistMode) {
        KeepInvListData listData = KeepInvListData.get(whitelistMode);
        ServerCommandSource source = context.getSource();
        String message = listData.list.toString();

        String beginText = "§b" + capsWords(listData.name) + "ed items: ";

        String experienceText = "";
        if (listData.expListed) {
            experienceText = "§2§oExperience";
            if (listData.list.size() > 0) experienceText += "§7, ";
        }

        String listText = "§7" + getItemString(message.substring(1, message.length() - 1));

        source.sendMessage(Text.of(beginText + experienceText + listText));
        return 0;
    }

    private static String getItemString(String itemString) {
        return capsWords(itemString.replace('_', ' '));
    }

    private static String capsWords(String original) {
        if (original.length() == 0) return original;
        StringBuilder string = new StringBuilder(original);

        string.setCharAt(0, Character.toUpperCase(string.charAt(0)));
        for (int i = 0; i < string.length() - 1; i++) {
            if (string.charAt(i) == ' ') {
                string.setCharAt(i + 1, Character.toUpperCase(string.charAt(i + 1)));
            }
        }
        return string.toString();
    }

}
