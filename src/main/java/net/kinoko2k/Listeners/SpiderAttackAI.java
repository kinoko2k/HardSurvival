package net.kinoko2k.Listeners;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpiderAttackAI implements Listener {
    private final JavaPlugin plugin;

    public SpiderAttackAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpiderTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Spider && event.getTarget() instanceof Player) {
            Spider spider = (Spider) event.getEntity();
            Player player = (Player) event.getTarget();
            startSpiderAttackAI(spider, player);
        }
    }

    private void startSpiderAttackAI(Spider spider, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!spider.isValid() || player == null || !player.isOnline()) {
                    cancel();
                    return;
                }

                if (spider.getLocation().distance(player.getLocation()) < 10) {
                    throwSlime(spider, player);
                }
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void throwSlime(Spider spider, Player player) {
        Snowball snowball = spider.getWorld().spawn(spider.getLocation().add(0, 1, 0), Snowball.class);
        Vector direction = player.getLocation().subtract(spider.getLocation()).toVector().normalize().multiply(1.5);
        snowball.setVelocity(direction);
        snowball.getWorld().spawnParticle(Particle.SNOWFLAKE, snowball.getLocation(), 10, 0.3, 0.1, 0.3, 0.01);
    }

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Snowball || event.getDamager() instanceof Arrow)) {
            Player player = (Player) event.getEntity();

            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5, 1));
            player.damage(2);
        }
    }
}