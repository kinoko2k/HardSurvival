package net.kinoko2k.Listeners.nether;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WitherSkeletonCombatAI implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, Boolean> rangedMode = new HashMap<>();

    public WitherSkeletonCombatAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherSkeletonSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof WitherSkeleton) {
            WitherSkeleton witherSkeleton = (WitherSkeleton) event.getEntity();
            witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            witherSkeleton.setHealth(40.0);
        }
    }

    @EventHandler
    public void onWitherSkeletonTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof WitherSkeleton && event.getTarget() instanceof Player) {
            WitherSkeleton witherSkeleton = (WitherSkeleton) event.getEntity();
            startWeaponSwitchAI(witherSkeleton, (Player) event.getTarget());
        }
    }

    private void startWeaponSwitchAI(WitherSkeleton witherSkeleton, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!witherSkeleton.isValid() || player == null || !player.isOnline()) {
                    rangedMode.remove(witherSkeleton.getUniqueId());
                    cancel();
                    return;
                }

                double distance = witherSkeleton.getLocation().distance(player.getLocation());

                if (distance >= 5) {
                    if (!rangedMode.getOrDefault(witherSkeleton.getUniqueId(), false)) {
                        witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                        rangedMode.put(witherSkeleton.getUniqueId(), true);
                    }
                } else {
                    if (rangedMode.getOrDefault(witherSkeleton.getUniqueId(), false)) {
                        witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                        rangedMode.put(witherSkeleton.getUniqueId(), false);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }
}