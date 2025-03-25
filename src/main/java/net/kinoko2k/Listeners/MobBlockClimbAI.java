package net.kinoko2k.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MobBlockClimbAI implements Listener {
    private final JavaPlugin plugin;
    private final Set<UUID> trackingMobs = new HashSet<>();

    private static final Set<EntityType> BLOCK_PLACING_MOBS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED,
            EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON
    );

    public MobBlockClimbAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobTargetPlayer(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();
            if (BLOCK_PLACING_MOBS.contains(mob.getType())) {
                startClimbingAI(mob, (Player) event.getTarget());
            }
        }
    }

    private void startClimbingAI(Monster mob, Player target) {
        if (trackingMobs.contains(mob.getUniqueId())) return;
        trackingMobs.add(mob.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mob.isValid() || target == null || !target.isOnline()) {
                    trackingMobs.remove(mob.getUniqueId());
                    cancel();
                    return;
                }

                Location mobLoc = mob.getLocation();
                Location targetLoc = target.getLocation();
                int mobY = mobLoc.getBlockY();
                int playerY = targetLoc.getBlockY();

                int heightDifference = playerY - mobY;

                if (heightDifference >= 2) {
                    placeBlockUnderMob(mob);
                } else if (heightDifference == 0) {
                    Location front = mobLoc.clone().add(mobLoc.getDirection().multiply(1));
                    if (front.getBlock().getType().isAir()) {
                        placeBlock(front);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private void placeBlockUnderMob(LivingEntity mob) {
        Location loc = mob.getLocation().subtract(0, 1, 0);
        if (loc.getBlock().getType().isAir()) {
            placeBlock(loc);
            mob.teleport(mob.getLocation().add(0, 1, 0));
        }
    }

    private void placeBlock(Location loc) {
        Block block = loc.getBlock();
        if (block.getType().isAir()) {
            block.setType(Material.DIRT);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.DIRT) {
                        block.setType(Material.AIR);
                    }
                }
            }.runTaskLater(plugin, 60L);
        }
    }
}