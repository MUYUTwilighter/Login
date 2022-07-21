package cool.muyucloud.login;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cool.muyucloud.login.access.ServerPlayerEntityAccess;
import cool.muyucloud.login.util.Accounts;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.Objects;

public class LoginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                boolean dedicated) {
        dispatcher.register(
                CommandManager.literal("register")
                        .then(CommandManager.argument("password", StringArgumentType.string())
                                .executes(context ->
                                        register(context.getSource(),
                                                StringArgumentType.getString(context, "password"),
                                                dedicated))));

        dispatcher.register(
                CommandManager.literal("login")
                        .then(CommandManager.argument("password", StringArgumentType.string())
                                .executes(context ->
                                        login(context.getSource(),
                                                StringArgumentType.getString(context, "password"),
                                                dedicated))));

        dispatcher.register(
                CommandManager.literal("password")
                        .then(CommandManager.argument("original", StringArgumentType.string())
                                .then(CommandManager.argument("new", StringArgumentType.string())
                                        .executes(context ->
                                                changePassword(context.getSource(),
                                                        StringArgumentType.getString(context, "origin"),
                                                        StringArgumentType.getString(context, "new"),
                                                        dedicated)))));

        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("password");

        root.then(CommandManager.argument("player", EntityArgumentType.player())
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("password", StringArgumentType.string())
                        .executes(context ->
                                opChangePassword(context.getSource(),
                                        EntityArgumentType.getPlayer(context, "player"),
                                        StringArgumentType.getString(context, "password"),
                                        dedicated))));

        root.then(CommandManager.literal("load")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> load(context.getSource(), dedicated)));

        root.then(CommandManager.literal("dump")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> dump(context.getSource(), dedicated)));

        root.then(CommandManager.literal("query")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> query(context.getSource(), EntityArgumentType.getPlayer(context, "player"),
                                dedicated))));

        dispatcher.register(root);
    }

    private static int register(ServerCommandSource source, String password,
                                boolean dedicated) throws CommandSyntaxException {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!(source.getEntity() instanceof PlayerEntity)) {
            source.sendFeedback(new LiteralText("该命令只可由玩家执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!Accounts.isNewPlayer(Objects.requireNonNull(source.getPlayer()).getName().getString())) {
            source.sendFeedback(new LiteralText("你已经在当前服务器注册")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        // do registration
        Accounts.register(source.getPlayer().getName().getString(), password);
        // send chat info
        source.sendFeedback(new LiteralText("成功创建账户并登录！")
                .setStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))), false);
        // log this successful registration
        Login.LOGGER.info("Player \"" + Objects.requireNonNull(source.getPlayer()).getName().getString()
                + "\" successfully registered with IP " + source.getPlayer().getIp());
        // set as logged in
        ((ServerPlayerEntityAccess) source.getPlayer()).login();

        return 1;
    }

    private static int login(ServerCommandSource source, String password, boolean dedicated) throws CommandSyntaxException {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!(source.getEntity() instanceof PlayerEntity)) {
            source.sendFeedback(new LiteralText("该命令只可由玩家执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (Accounts.isNewPlayer(Objects.requireNonNull(source.getPlayer()).getName().getString())) {
            source.sendFeedback(new LiteralText("你还未在当前服务器注册")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (((ServerPlayerEntityAccess) source.getPlayer()).isLoggedIn()) {
            source.sendFeedback(new LiteralText("你已经登录")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!Accounts.verify(source.getPlayer().getName().getString(), password)) {
            source.sendFeedback(new LiteralText("密码错误")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        // mark as logged in
        ((ServerPlayerEntityAccess) source.getPlayer()).login();
        // send chat info
        source.sendFeedback(new LiteralText("成功登录")
                .setStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))), false);
        // log this successful log-in
        Login.LOGGER.info("Player \"" + Objects.requireNonNull(source.getPlayer()).getName().getString()
                + "\" successfully logged in with IP " + source.getPlayer().getIp());
        return 1;
    }

    private static int changePassword(ServerCommandSource source, String original, String value, boolean dedicated) throws CommandSyntaxException {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!(source.getEntity() instanceof PlayerEntity)) {
            source.sendFeedback(new LiteralText("该命令只可由玩家执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (Accounts.isNewPlayer(Objects.requireNonNull(source.getPlayer()).getName().getString())) {
            source.sendFeedback(new LiteralText("你还未在当前服务器注册")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (!((ServerPlayerEntityAccess) source.getPlayer()).isLoggedIn()) {
            source.sendFeedback(new LiteralText("你还未登录")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (Accounts.verify(source.getPlayer().getName().getString(), original)) {
            source.sendFeedback(new LiteralText("密码错误")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        Accounts.changePassword(source.getPlayer().getName().getString(), value);
        // send chat info
        source.sendFeedback(new LiteralText("成功修改密码")
                .setStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))), false);
        // log this successful log-in
        Login.LOGGER.info("Player \"" + Objects.requireNonNull(source.getPlayer()).getName().getString()
                + "\" successfully changed password with IP " + source.getPlayer().getIp());

        return 1;
    }

    private static int opChangePassword(ServerCommandSource source, PlayerEntity player, String password, boolean dedicated) {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (Accounts.isNewPlayer(Objects.requireNonNull(player.getName().getString()))) {
            source.sendFeedback(new LiteralText("你还未在当前服务器注册")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        Accounts.changePassword(player.getName().getString(), password);
        // send chat info
        source.sendFeedback(new LiteralText("成功修改密码")
                .setStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))), false);
        // send info to player
        player.sendMessage(new LiteralText(source.getName() + ": 密码已被管理员修改")
                .setStyle(Style.EMPTY.withColor(TextColor.parse("aqua"))), false);
        // log this
        Login.LOGGER.info("Player \""
                + player.getName().getString()
                + "\" successfully changed password by operator \""
                + Objects.requireNonNull(source.getName()) + "\"");
        return 1;
    }

    private static int dump(ServerCommandSource source, boolean dedicated) {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        source.sendFeedback(new LiteralText("正在将密码库保存至文件"), false);
        Accounts.dump();
        return 1;
    }

    private static int load(ServerCommandSource source, boolean dedicated) {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        source.sendFeedback(new LiteralText("正在从文件读取密码库"), false);
        Accounts.load();
        return 1;
    }

    private static int query(ServerCommandSource source, PlayerEntity player, boolean dedicated) {
        if (!dedicated) {
            source.sendFeedback(new LiteralText("该命令无法在本地游戏执行")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        if (Accounts.isNewPlayer(player.getName().getString())) {
            source.sendFeedback(new LiteralText("该玩家还未在当前服务器注册")
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("red"))), false);
            return 0;
        }

        source.sendFeedback(new LiteralText(String.format("玩家 %s 的密码是 %s", player.getName().getString(), Accounts.query(player.getName().getString()))), false);
        Login.LOGGER.info(String.format("Operator %s queried password of player %s.", source.getName(), player.getName().getString()));
        return 1;
    }
}
