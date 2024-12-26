package io.github.silvigarabis.esplitter.consumpsion;

import java.util.LinkedList;

import org.bukkit.entity.Player;

import io.github.silvigarabis.esplitter.data.ESplitterConsumpsion;

public class OpTakePlayerExp {

    public static LinkedList<Runnable> genOpTakePlayerExp(Player player, ESplitterConsumpsion consumpsion){
        LinkedList<Runnable> opList = new LinkedList<>();
        if (consumpsion.getExperienceLevel() == 0 && consumpsion.getExperiencePoint() == 0) {
            throw new ESplitterConsumpsion.NoOpException();
        }
    
        final var playerCurrentExp = player.getTotalExperience();
        final var playerCurrentLevel = player.getLevel();
        final var expChange = consumpsion.getExperiencePoint();
        final var expLevelChange = consumpsion.getExperienceLevel();
    
        var expPoint = playerCurrentExp;
        var expLevel = playerCurrentLevel;
    
        if (expChange != 0) {
            expPoint -= expChange;
            if (expPoint < 0) {
                throw new ESplitterConsumpsion.OpCantExecuteException();
            }
    
            // 这样做是为了获取在扣除经验点后的经验等级数目
            player.setTotalExperience(expPoint);
            expLevel = player.getLevel();
            player.setTotalExperience(playerCurrentExp);
    
            final var expPointFinalized = expPoint;
            opList.add(() -> {
                player.setTotalExperience(expPointFinalized);
            });
        }
    
        if (expLevelChange != 0) {
            expLevel -= expLevelChange;
            if (expLevel < 0) {
                throw new ESplitterConsumpsion.OpCantExecuteException();
            }
            final var expLevelFinalized = expLevel;
            opList.add(() -> {
                player.setTotalExperience(expLevelFinalized);
            });
        }
        return opList;
    }
    
}
