package io.github.silvigarabis.esplitter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.silvigarabis.esplitter.ESplitterPlugin;
import io.github.silvigarabis.esplitter.Messages;
import io.github.silvigarabis.esplitter.Permissions;

import java.util.logging.Logger;

public class ESplitterComand implements CommandExecutor {
    private ESplitterPlugin plugin;
    public ESplitterComand(ESplitterPlugin plugin) {
        this.plugin = plugin;
    }
    private Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onCommand0(sender, command, label, args);
        return true;
    }

    private void onCommand0(CommandSender sender, Command command, String label, String[] args) {
        getLogger().info("正在处理指令……");

        if (!Permissions.COMMAND.checkPermission(sender)) {
            sender.sendMessage(Messages.commandErrorNoPerm.getText());
            return;
        }

        if ("esgui".equals(label)){
            cmd_gui(sender, label, args);
        } else {
            cmd_main(sender, label, args);
        }

    }
    
    private void cmd_gui(CommandSender sender, String label, String[] args){
        String p_playerName = null;
        if ("esgui".equals(label)) {
            if (args.length > 0) {
                p_playerName = args[0];
            }
        } else {
            if (args.length > 1) {
                p_playerName = args[1];
            }
        }

        // 获得最终要操作的玩家
        Player player = null;
        if (p_playerName != null) { // spec arg: player
            if (!Permissions.OP_OTHER_PLAYER.checkPermission(sender)){
                sender.sendMessage(Messages.commandErrorNoPerm.getText());
                return;
            }

            player = plugin.getPlayer(p_playerName);
        } else if (!(sender instanceof Player)) {  // is console
            sender.sendMessage(Messages.commandArgMissingPlayerOnConsole.getText());
            return;
        } else {  // else case: player run command without arg: player, use sender self
            player = (Player) sender;
        }

        // 执行操作
        if (player == null){
            sender.sendMessage(Messages.commandArgMissingPlayer.getText());
        } else {
            plugin.openGui(player);
        }
    }

    private void cmd_main(CommandSender sender, String label, String[] args) {
        if (args.length == 0){
            cmd_help(sender, label, args);
            return;
        }

        switch (args[0]){
            case "gui":
                cmd_gui(sender, label, args);
                break;
            // case "reload":
            //     cmd_reload(sender, label, args);
            //     break;
            // case "debug":
            //     cmd_debug(sender, label, args);
            //     break;
            case "help":
                cmd_help(sender, label, args);
                break;
            default:
                sender.sendMessage(Messages.commandErrorUnknown.getText());
                cmd_help(sender, label, args);
                break;
        }
    }

    private void cmd_help(CommandSender sender, String label, String[] args){
        sender.sendMessage("用法：/" + label + " [gui|help|reload|debug]");
    }
}
