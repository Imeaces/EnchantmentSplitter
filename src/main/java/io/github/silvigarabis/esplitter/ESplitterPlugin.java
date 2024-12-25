/*
   Copyright (c) 2024 Silvigarabis
   EnchantmentSplitter is licensed under Mulan PSL v2.
   You can use this software according to the terms and conditions of the Mulan PSL v2. 
   You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2 
   THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
   See the Mulan PSL v2 for more details.  
*/

package io.github.silvigarabis.esplitter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.silvigarabis.esplitter.commands.ESplitterComand;
import io.github.silvigarabis.esplitter.invgui.ESplitterInvGuiListener;

public final class ESplitterPlugin extends JavaPlugin {

    private static ESplitterPlugin pluginInstance = null;
    public static ESplitterPlugin getPluginInstance(){
        if (pluginInstance == null) {
            throw new RuntimeException("plugin instance not found");
        }
        return pluginInstance;
    }
    
    @Override
    public void onDisable() {
        if (this.equals(pluginInstance)){
            pluginInstance = null;
        }
        getLogger().info("插件已禁用");
    }

    @Override
    public void onEnable(){
        getLogger().info("加载中");
        
        if (pluginInstance == null){
            pluginInstance = this;
        } else {
            getLogger().severe("插件状态异常");
            throw new IllegalStateException("plugin is not null");
        }

        reloadConfig();

        registerEvents();
        registerCommands();
        
        getLogger().info("插件已启用！");
        getLogger().info("ESplitter 插件，一个让玩家可以分离装备上的附魔的插件");
        getLogger().info("源代码： https://github.com/Imeaces/EnchantmentSplitter");
        getLogger().info("你可以在在GitHub上提出建议，或者反馈错误： https://github.com/Imeaces/EnchantmentSplitter/issues");
    }

    private static void registerEvents(){
        pluginInstance.getServer().getPluginManager().registerEvents(new ESplitterInvGuiListener(), pluginInstance);
    }

    private static void registerCommands(){
        pluginInstance.getCommand("esplitter").setExecutor(new ESplitterComand(pluginInstance));
    }
    
    @Override
    public void reloadConfig(){
        getLogger().info("重新加载配置中");

        // config.yml
        super.reloadConfig();
        this.getConfig().setDefaults(ESplitterConfig.DEFAULT_CONFIGURATIONS);
        getLogger().info("已加载插件配置");

        // message.yml
    }

    public void openGui(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Player getPlayer(String playerName) {
        return this.getServer().getPlayerExact(playerName);
    }
}
