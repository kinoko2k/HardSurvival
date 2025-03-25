package net.kinoko2k.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MobStepAI implements Listener {
    private final JavaPlugin plugin;
    private final Set<UUID> trackingMobs = new HashSet<>();

    private static final Set<EntityType> STEPPING_MOBS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.WITHER_SKELETON
    );

    public MobStepAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobTargetPlayer(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();
            if (STEPPING_MOBS.contains(mob.getType())) {
                startFrontStepAI(mob);
            }
        }
    }

    private void startFrontStepAI(Monster mob) {
        if (trackingMobs.contains(mob.getUniqueId())) return;
        trackingMobs.add(mob.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mob.isValid()) {
                    trackingMobs.remove(mob.getUniqueId());
                    cancel();
                    return;
                }

                frontStep(mob);
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void frontStep(LivingEntity mob) {
        Vector direction = mob.getLocation().getDirection().normalize().multiply(0.8);
        direction.setY(0.2);
        mob.setVelocity(direction);
    }

    @EventHandler
    public void onMobDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Monster && event.getDamager() instanceof Player) {
            Monster mob = (Monster) event.getEntity();
            if (STEPPING_MOBS.contains(mob.getType())) {
                backStep(mob);
            }
        }
    }

    private void backStep(LivingEntity mob) {
        Vector direction = mob.getLocation().getDirection().normalize().multiply(-1.2);
        direction.setY(0.3);
        mob.setVelocity(direction);
    }
}