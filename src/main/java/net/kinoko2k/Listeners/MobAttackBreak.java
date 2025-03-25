package net.kinoko2k.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.*;

public class MobAttackBreak implements Listener {
    private final JavaPlugin plugin;

    private static final Set<EntityType> DOOR_BREAKING_MOBS = EnumSet.of(
            ZOMBIE, HUSK, DROWNED, SKELETON, STRAY, WITHER_SKELETON,
            PILLAGER, VINDICATOR, EVOKER, CREEPER, SPIDER
    );

    public MobAttackBreak(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDamageDoor(BlockDamageEvent event) {
        Entity damager = event.getPlayer();
        if (damager instanceof LivingEntity) {
            EntityType type = damager.getType();
            if (DOOR_BREAKING_MOBS.contains(type) && isDoor(event.getBlock().getType())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getBlock().breakNaturally();
                    }
                }.runTaskLater(plugin, 20L);
            }
        }
    }

    @EventHandler
    public void onMobAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Monster) {
            Player player = (Player) event.getEntity();
            ItemStack shield = player.getInventory().getItemInMainHand();

            if (shield.getType() == SHIELD) {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    private boolean isDoor(Material material) {
        return material == OAK_DOOR || material == SPRUCE_DOOR ||
                material == BIRCH_DOOR || material == JUNGLE_DOOR ||
                material == ACACIA_DOOR || material == DARK_OAK_DOOR ||
                material == IRON_DOOR;
    }
}
