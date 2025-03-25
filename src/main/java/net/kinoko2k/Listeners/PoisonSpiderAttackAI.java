package net.kinoko2k.Listeners;

import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PoisonSpiderAttackAI implements Listener {
    private final JavaPlugin plugin;

    public PoisonSpiderAttackAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPoisonSpiderTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getTarget() instanceof Player) {
            LivingEntity spider = (LivingEntity) event.getEntity();
            Player player = (Player) event.getTarget();
            if (spider.getType() == EntityType.CAVE_SPIDER) {
                startPoisonAttackAI(spider, player);
            }
        }
    }

    private void startPoisonAttackAI(LivingEntity spider, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!spider.isValid() || player == null || !player.isOnline()) {
                    cancel();
                    return;
                }

                if (spider.getLocation().distance(player.getLocation()) < 10) {
                    throwPoisonSnowball(spider, player);
                }
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void throwPoisonSnowball(LivingEntity spider, Player player) {
        Snowball snowball = spider.getWorld().spawn(spider.getLocation().add(0, 1, 0), Snowball.class);
        Vector direction = player.getLocation().subtract(spider.getLocation()).toVector().normalize().multiply(1.5);
        snowball.setVelocity(direction);

        snowball.getWorld().spawnParticle(Particle.SNOWFLAKE, snowball.getLocation(), 10, 0.3, 0.1, 0.3, 0.01);
    }

    @EventHandler
    public void onPoisonSnowballHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getDamager();
            Player player = (Player) event.getEntity();

            if (snowball.getShooter() instanceof LivingEntity && ((LivingEntity) snowball.getShooter()).getType() == EntityType.CAVE_SPIDER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            }
        }
    }
}
