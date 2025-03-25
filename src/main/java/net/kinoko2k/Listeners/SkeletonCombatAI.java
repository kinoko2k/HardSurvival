package net.kinoko2k.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SkeletonCombatAI implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, Boolean> meleeMode = new HashMap<>();

    private static final Set<EntityType> SWITCHING_SKELETONS = EnumSet.of(EntityType.SKELETON, EntityType.STRAY);

    public SkeletonCombatAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSkeletonTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && SWITCHING_SKELETONS.contains(event.getEntityType())) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            startWeaponSwitchAI(skeleton, (Player) event.getTarget());
        }
    }

    private void startWeaponSwitchAI(Skeleton skeleton, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!skeleton.isValid() || player == null || !player.isOnline()) {
                    meleeMode.remove(skeleton.getUniqueId());
                    cancel();
                    return;
                }

                double distance = skeleton.getLocation().distance(player.getLocation());

                if (distance <= 3) {
                    if (!meleeMode.getOrDefault(skeleton.getUniqueId(), false)) {
                        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
                        meleeMode.put(skeleton.getUniqueId(), true);
                        startBackStepAI(skeleton);
                    }
                } else {
                    if (meleeMode.getOrDefault(skeleton.getUniqueId(), false)) {
                        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                        meleeMode.put(skeleton.getUniqueId(), false);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private void startBackStepAI(Skeleton skeleton) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!skeleton.isValid() || !meleeMode.getOrDefault(skeleton.getUniqueId(), false)) {
                    cancel();
                    return;
                }

                backStep(skeleton);
            }
        }.runTaskTimer(plugin, 80L, 80L);
    }

    private void backStep(LivingEntity entity) {
        Vector direction = entity.getLocation().getDirection().normalize().multiply(-1.2);
        direction.setY(0.3);
        entity.setVelocity(direction);
    }
}
