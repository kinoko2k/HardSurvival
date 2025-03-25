package net.kinoko2k.Listeners.nether;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GhastBiomeSpawn implements Listener {
    private final JavaPlugin plugin;

    public GhastBiomeSpawn(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.GHAST) {
            World world = event.getEntity().getWorld();
            Biome biome = world.getBiome(event.getEntity().getLocation());

            if (biome == Biome.SNOWY_BEACH || biome == Biome.SNOWY_PLAINS || biome == Biome.SNOWY_TAIGA || biome == Biome.SNOWY_BEACH || biome == Biome.DESERT) {
                spawnGhast(event.getEntity().getLocation().getWorld());
            }
        }
    }

    private void spawnGhast(World world) {
        world.spawnEntity(world.getSpawnLocation(), EntityType.GHAST);
    }
}