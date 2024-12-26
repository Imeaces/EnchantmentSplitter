package io.github.silvigarabis.esplitter.data;

import java.util.LinkedList;
import java.util.function.BiFunction;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import io.github.silvigarabis.esplitter.Messages;
import io.github.silvigarabis.esplitter.consumpsion.*;

public class ESplitterConsumpsion {
    private int experiencePoint;
    private int experienceLevel;

    private Material item1;
    private int item1Amount;
    private Material item2;
    private int item2Amount;

    private Material backItem1;
    private int backItem1Amount;
    private Material backItem2;
    private int backItem2Amount;

    public int getExperiencePoint() {
        return experiencePoint;
    }

    public void setExperiencePoint(int experiencePoint) {
        this.experiencePoint = experiencePoint;
    }

    public int changeEXperiencePoint(int experiencePoint) {
        return this.experiencePoint += experiencePoint;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public int changeEXperienceLevel(int experienceLevel) {
        return this.experienceLevel += experienceLevel;
    }

    public Material getItem1() {
        return item1;
    }

    public void setItem1(Material item1) {
        this.item1 = item1;
    }

    public int getItem1Amount() {
        return item1Amount;
    }

    public void setItem1Amount(int item1Amount) {
        this.item1Amount = item1Amount;
    }

    public int changeItem1Amount(int item1Amount) {
        return this.item1Amount += item1Amount;
    }

    public Material getItem2() {
        return item2;
    }

    public void setItem2(Material item2) {
        this.item2 = item2;
    }

    public int getItem2Amount() {
        return item2Amount;
    }

    public void setItem2Amount(int item2Amount) {
        this.item2Amount = item2Amount;
    }

    public int changeItem2Amount(int item2Amount) {
        return this.item2Amount += item2Amount;
    }

    public Material getBackItem1() {
        return backItem1;
    }

    public void setBackItem1(Material backItem1) {
        this.backItem1 = backItem1;
    }

    public int getBackItem1Amount() {
        return backItem1Amount;
    }

    public void setBackItem1Amount(int backItem1Amount) {
        this.backItem1Amount = backItem1Amount;
    }

    public int changeBackItem1Amount(int backItem1Amount) {
        return this.backItem1Amount += backItem1Amount;
    }

    public Material getBackItem2() {
        return backItem2;
    }

    public void setBackItem2(Material backItem2) {
        this.backItem2 = backItem2;
    }

    public int getBackItem2Amount() {
        return backItem2Amount;
    }

    public void setBackItem2Amount(int backItem2Amount) {
        this.backItem2Amount = backItem2Amount;
    }

    public int changeBackItem2Amount(int amount) {
        return this.backItem2Amount += amount;
    }

    public ESplitterConsumpsion() {

    }

    public ESplitterConsumpsion(int experiencePoint, int experienceLevel, Material item1, int item1Amount,
            Material item2, int item2Amount, Material backItem1, int backItem1Amount, Material backItem2,
            int backItem2Amount) {
        this.experiencePoint = experiencePoint;
        this.experienceLevel = experienceLevel;
        this.item1 = item1;
        this.item2 = item2;
        this.item1Amount = item1Amount;
        this.item2Amount = item2Amount;
        this.backItem1 = backItem1;
        this.backItem2 = backItem2;
        this.backItem1Amount = backItem1Amount;
        this.backItem2Amount = backItem2Amount;
    }

    public ESplitterConsumpsion clone() {
        return new ESplitterConsumpsion(this.experiencePoint, this.experienceLevel, this.item1, this.item1Amount,
                this.item2, this.item2Amount, this.backItem1, this.backItem1Amount, this.backItem2,
                this.backItem2Amount);
    }

    /**
     * 为玩家生成可阅读的消耗要求文本
     * @param consumption
     * @param player
     * @return
     */
    public static String generateConsumptionText(ESplitterConsumpsion consumption, Player player) {
        StringBuilder text = new StringBuilder();

        // 添加经验信息
        if (consumption.getExperiencePoint() > 0) {
            text.append(Messages.consumpsionExperiencePoints
                .getPlayerText(player, consumption.getExperiencePoint()))
                .append("\n");
        }
        if (consumption.getExperienceLevel() > 0) {
            text.append(Messages.consumpsionExperienceLevels
                .getPlayerText(player, consumption.getExperienceLevel()))
                .append("\n");
        }

        // 添加消耗物品信息
        if (consumption.getItem1() != null && consumption.getItem1Amount() > 0) {
            text.append(Messages.consumpsionItem
                .getPlayerText(player, consumption.getItem1(), consumption.getItem1Amount()))
                .append("\n");
        }
        if (consumption.getItem2() != null && consumption.getItem2Amount() > 0) {
            text.append(Messages.consumpsionItem
                .getPlayerText(player, consumption.getItem2(), consumption.getItem2Amount()))
                .append("\n");
        }

        // 添加返还物品信息
        if (consumption.getBackItem1() != null && consumption.getBackItem1Amount() > 0) {
            text.append(Messages.consumpsionReturnItem
                .getPlayerText(player, consumption.getBackItem1(), consumption.getBackItem1Amount()))
                .append("\n");
        }
        if (consumption.getBackItem2() != null && consumption.getBackItem2Amount() > 0) {
            text.append(Messages.consumpsionReturnItem
                .getPlayerText(player, consumption.getBackItem2(), consumption.getBackItem2Amount()))
                .append("\n");
        }

        // 如果没有任何内容，则显示“无消耗”信息
        if (text.length() == 0) {
            text.append(Messages.consumpsionNone.getPlayerText(player));
        }

        return text.toString().trim();
    }

    /**
     * 在玩家上根据消耗要求执行操作
     * @param player
     * @param consumpsion
     * @return
     */
    public static boolean removeConsumpsion(Player player, ESplitterConsumpsion consumpsion) {
        // 所有操作都要在这时候依次同步完成以避免错误
        LinkedList<Runnable> instantOpList = new LinkedList<>();
        try {
            for (var opGenerator : opGenerators) {
                try {
                    instantOpList.addAll(opGenerator.apply(player, consumpsion));
                } catch (NoOpException ex) {
                    continue;
                }
            }
        } catch (OpCantExecuteException ex) {
            return false;
        }
        instantOpList.forEach(op -> op.run());
        return true;
    }

    public static class NoOpException extends RuntimeException {
    }
    public static class OpCantExecuteException extends RuntimeException {
    }

    private static LinkedList<BiFunction<Player, ESplitterConsumpsion, LinkedList<Runnable>>> opGenerators = new LinkedList<>();
    static {
        opGenerators.add(OpTakePlayerExp::genOpTakePlayerExp);
        opGenerators.add(OpTakePlayerInv::genOpTakePlayerInv);
        opGenerators.add(OpGivePlayerInv::genOpGivePlayerInv);
    }
}
