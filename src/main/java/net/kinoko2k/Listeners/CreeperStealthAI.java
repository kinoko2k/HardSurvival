package net.kinoko2k.Listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CreeperStealthAI implements Listener {
    private final JavaPlugin plugin;
    private final Set<UUID> stealthCreepers = new HashSet<>();

    public CreeperStealthAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreeperTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Creeper && event.getTarget() instanceof Player) {
            Creeper creeper = (Creeper) event.getEntity();
            startStealthMode(creeper);
        }
    }

    private void startStealthMode(Creeper creeper) {
        if (stealthCreepers.contains(creeper.getUniqueId())) return;
        stealthCreepers.add(creeper.getUniqueId());

        creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!creeper.isValid() || creeper.isDead()) {
                    stealthCreepers.remove(creeper.getUniqueId());
                    cancel();
                    return;
                }

                Location loc = creeper.getLocation();
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 10, 0.3, 0.1, 0.3, 0.01);
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            Location loc = event.getLocation();
            World world = loc.getWorld();
            int radius = 3;

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {
                            Location fireLoc = loc.clone().add(x, 0, z);
                            Block block = fireLoc.getBlock();
                            if (block.getType() == Material.AIR || block.getType().isFlammable()) {
                                block.setType(Material.FIRE);
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 2L);
        }
    }
}