package net.kinoko2k.Listeners.boss;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class EnderDragonAI implements Listener {
    private final JavaPlugin plugin;
    private final Random random = new Random();

    public EnderDragonAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDragonTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getTarget() instanceof Player) {
            EnderDragon dragon = (EnderDragon) event.getEntity();
            Player player = (Player) event.getTarget();
            startDragonAI(dragon, player);
        }
    }

    private void startDragonAI(EnderDragon dragon, Player player) {
        new BukkitRunnable() {
            int tickCounter = 0;

            @Override
            public void run() {
                if (!dragon.isValid() || player == null || !player.isOnline()) {
                    cancel();
                    return;
                }

                double health = dragon.getHealth();
                double maxHealth = dragon.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();

                if (tickCounter % 20 == 0) {
                    shootBeam(dragon, player);
                }

                if (tickCounter % 100 == 0) {
                    shootGhastFireballs(dragon, player);
                }

                if (tickCounter % 200 == 0) {
                    createExplosiveWarning(player);
                }

                if (health <= maxHealth / 2) {
                    dragon.setHealth(Math.min(health + 2, maxHealth));
                    dragon.setVelocity(player.getLocation().subtract(dragon.getLocation()).toVector().normalize().multiply(1.5));
                }

                tickCounter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void shootBeam(EnderDragon dragon, Player player) {
        Location start = dragon.getLocation().add(0, 3, 0);
        Location end = player.getLocation().add(0, 1, 0);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        for (int i = 0; i < 20; i++) {
            Location particleLoc = start.clone().add(direction.clone().multiply(i * 0.5));
            dragon.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 10, 0.2, 0.2, 0.2, 0.01);
        }

        player.damage(4);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
    }

    private void shootGhastFireballs(EnderDragon dragon, Player player) {
        World world = dragon.getWorld();
        Location loc = dragon.getLocation();

        for (int i = 0; i < 5; i++) {
            Fireball fireball = world.spawn(loc.add(0, 2, 0), Fireball.class);
            fireball.setIsIncendiary(false);
            fireball.setYield(3);
            Vector direction = player.getLocation().subtract(loc).toVector().normalize().multiply(1.5);
            fireball.setVelocity(direction);
        }
    }

    private void createExplosiveWarning(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();

        for (int i = 0; i < 3; i++) {
            Location randomLoc = loc.clone().add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            Block block = world.getBlockAt(randomLoc);
            world.spawnParticle(Particle.FLAME, block.getLocation(), 30, 0.5, 0.5, 0.5, 0.05);
            world.playSound(block.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);

            new BukkitRunnable() {
                @Override
                public void run() {
                    world.createExplosion(block.getLocation(), 3, false, false);
                }
            }.runTaskLater(plugin, 60L);
        }
    }
}