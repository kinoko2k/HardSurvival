package net.kinoko2k.Listeners.nether;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Blaze;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.entity.Ghast;

public class BlazeFireballAI implements Listener {
    private final JavaPlugin plugin;

    public BlazeFireballAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlazeTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Blaze && event.getTarget() instanceof Player) {
            Blaze blaze = (Blaze) event.getEntity();
            Player player = (Player) event.getTarget();
            startFireballAttackAI(blaze, player);
        }
    }

    private void startFireballAttackAI(Blaze blaze, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!blaze.isValid() || player == null || !player.isOnline()) {
                    cancel();
                    return;
                }

                throwExplosiveFireball(blaze, player);
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void throwExplosiveFireball(Blaze blaze, Player player) {
        Fireball fireball = blaze.getWorld().spawn(blaze.getLocation().add(0, 1, 0), Fireball.class);
        fireball.setIsIncendiary(false);
        fireball.setYield(4);

        Vector direction = player.getLocation().subtract(blaze.getLocation()).toVector().normalize().multiply(1.5);
        fireball.setVelocity(direction);
    }
}