package net.kinoko2k.Listeners.nether;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class SlimeAndMagmaCubeSizeAI implements Listener {
    private final JavaPlugin plugin;

    public SlimeAndMagmaCubeSizeAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Slime || event.getEntity() instanceof MagmaCube) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            startSizeIncreaseAI(entity);
        }
    }

    private void startSizeIncreaseAI(LivingEntity entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isValid()) {
                    cancel();
                    return;
                }

                if (entity instanceof Slime) {
                    Slime slime = (Slime) entity;
                    if (slime.getSize() < 4) {
                        slime.setSize(slime.getSize() + 1);
                    }
                } else if (entity instanceof MagmaCube) {
                    MagmaCube magmaCube = (MagmaCube) entity;
                    if (magmaCube.getSize() < 4) {
                        magmaCube.setSize(magmaCube.getSize() + 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L);
    }
}