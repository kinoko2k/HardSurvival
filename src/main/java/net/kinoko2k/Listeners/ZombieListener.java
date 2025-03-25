package net.kinoko2k.Listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZombieListener implements Listener {

    private final JavaPlugin plugin;

    public ZombieListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
            zombie.getEquipment().setHelmet(leatherHelmet);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, false, false)); // ゾンビに移動速度上昇Lv3
        }
    }

    @EventHandler
    public void onZombieMove(EntityMoveEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            Block block = zombie.getLocation().getBlock();

            if (block.getType() == Material.TORCH || block.getRelative(BlockFace.UP).getType() == Material.TORCH) {
                block.setType(Material.AIR);
            }
        }
    }
}
