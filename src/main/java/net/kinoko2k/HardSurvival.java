package net.kinoko2k;

import net.kinoko2k.Listeners.*;
import net.kinoko2k.Listeners.nether.WitherSkeletonCombatAI;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardSurvival extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ZombieListener(this), this);
        getServer().getPluginManager().registerEvents(new MobAttackBreak(this), this);
        getServer().getPluginManager().registerEvents(new MobJumpAI(this), this);
        getServer().getPluginManager().registerEvents(new ZombieListener(this), this);
        getServer().getPluginManager().registerEvents(new MobBlockClimbAI(this), this);
        getServer().getPluginManager().registerEvents(new MobStepAI(this), this);
        getServer().getPluginManager().registerEvents(new CreeperStealthAI(this), this);
        getServer().getPluginManager().registerEvents(new SkeletonCombatAI(this), this);
        getServer().getPluginManager().registerEvents(new WitherSkeletonCombatAI(this), this);
        getServer().getPluginManager().registerEvents(new SpiderAttackAI(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
