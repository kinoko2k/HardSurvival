package net.kinoko2k.Listeners.boss;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class WitherAI implements Listener {
    private final JavaPlugin plugin;
    private final Random random = new Random();

    public WitherAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Wither) {
            Wither wither = (Wither) event.getEntity();
            enhanceWitherSpawn(wither);
            startWitherAI(wither);
        }
    }

    private void enhanceWitherSpawn(Wither wither) {
        Location loc = wither.getLocation();
        World world = loc.getWorld();

        world.createExplosion(loc, 300, false, false);

        wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        wither.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));

        startBlazeFireballAttack(wither);
        startDragonBreathAttack(wither);
        startExplosiveWarning(wither);
        startTridentThrow(wither);
        startExplosionAtHighAltitude(wither);
        startTeleportToPlayer(wither);
    }


    private void startBlazeFireballAttack(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                LivingEntity target = wither.getTarget();
                if (target instanceof Player) {
                    shootBlazeFireballs(wither, (Player) target);
                }
            }
        }.runTaskTimer(plugin, 30L, 30L);
    }

    private void shootBlazeFireballs(Wither wither, Player player) {
        World world = wither.getWorld();
        Location loc = wither.getLocation();

        for (int i = 0; i < 5; i++) {
            SmallFireball fireball = world.spawn(loc.add(0, 1, 0), SmallFireball.class);

            Vector direction = player.getLocation().subtract(loc).toVector().normalize();
            direction.add(new Vector(
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.5
            ));
            direction.normalize().multiply(1.5);

            fireball.setVelocity(direction);
        }
    }

    private void startDragonBreathAttack(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                LivingEntity target = wither.getTarget();
                if (target instanceof Player) {
                    shootDragonBreath(wither, (Player) target);
                }
            }
        }.runTaskTimer(plugin, 60L, 60L);
    }

    private void shootDragonBreath(Wither wither, Player player) {
        Location start = wither.getLocation().add(0, 3, 0);
        Location end = player.getLocation().add(0, 1, 0);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        for (int i = 0; i < 20; i++) {
            Location particleLoc = start.clone().add(direction.clone().multiply(i * 0.5));
            wither.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 10, 0.2, 0.2, 0.2, 0.01);
        }

        player.damage(10);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
    }

    private void startExplosiveWarning(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                LivingEntity target = wither.getTarget();
                if (target instanceof Player) {
                    Player player = (Player) target;
                    Location playerLocation = player.getLocation();

                    player.getWorld().playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.getWorld().createExplosion(playerLocation, 10, false, false);
                        }
                    }.runTaskLater(plugin, 40L);
                }
            }
        }.runTaskTimer(plugin, 100L, 100L);
    }

    private void startTridentThrow(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                LivingEntity target = wither.getTarget();
                if (target instanceof Player) {
                    for (int i = 0; i < 3; i++) {
                        throwTrident(wither, (Player) target);
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L);
    }

    private void throwTrident(Wither wither, Player player) {
        World world = wither.getWorld();
        Location loc = wither.getLocation().add(0, 1, 0);

        Trident trident = world.spawn(loc, Trident.class);
        Vector direction = player.getLocation().subtract(loc).toVector().normalize().multiply(1.5);
        trident.setVelocity(direction);
        trident.setShooter(wither);
    }

    private void startExplosionAtHighAltitude(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                Location loc = wither.getLocation();
                if (loc.getY() > 100) {
                    wither.getWorld().createExplosion(loc, 3, false, false);
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void startTeleportToPlayer(Wither wither) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                LivingEntity target = wither.getTarget();
                if (target instanceof Player) {
                    teleportToPlayer(wither, (Player) target);
                }
            }
        }.runTaskTimer(plugin, 600L, 600L);
    }

    private void teleportToPlayer(Wither wither, Player player) {
        Location playerLocation = player.getLocation();
        double offsetX = (random.nextDouble() - 0.5) * 5;
        double offsetZ = (random.nextDouble() - 0.5) * 5;
        double offsetY = 0;

        Location teleportLocation = playerLocation.add(offsetX, offsetY, offsetZ);
        teleportLocation.setY(playerLocation.getY());

        wither.teleport(teleportLocation);
    }

    @EventHandler
    public void onWitherHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Wither && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getInventory().getItemInMainHand().getType() == Material.MACE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWitherArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Wither && event.getDamager() instanceof Arrow) {
            event.setCancelled(true);

            Arrow arrow = (Arrow) event.getDamager();
            Vector reverse = arrow.getVelocity().multiply(-1);
            arrow.setVelocity(reverse);
        }
    }

    @EventHandler
    public void onWitherAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof WitherSkull) {
            if (event.getEntity() instanceof Player) {
                event.setDamage(6);
            }
        }
    }

    private void spawnRavagersAroundWither(Wither wither) {
        Location loc = wither.getLocation();
        World world = loc.getWorld();

        for (int i = 0; i < 5; i++) {
            double angle = Math.PI * 2 * (i / 5.0);
            double x = loc.getX() + Math.cos(angle) * 5;
            double z = loc.getZ() + Math.sin(angle) * 5;
            Location spawnLocation = new Location(world, x, loc.getY(), z);

            Ravager ravager = (Ravager) world.spawnEntity(spawnLocation, EntityType.RAVAGER);
            ravager.setTarget(wither.getTarget());

            ravager.setTarget(null);
        }
    }

    private void checkAndSpawnRavagers(Wither wither) {
        double health = wither.getHealth();
        double maxHealth = wither.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        if (health <= maxHealth / 2) {
            spawnRavagersAroundWither(wither);
        }
    }

    private void startWitherAI(Wither wither) {
        new BukkitRunnable() {
            int tickCounter = 0;

            @Override
            public void run() {
                if (!wither.isValid()) {
                    cancel();
                    return;
                }

                if (tickCounter % 100 == 0) {
                    checkAndSpawnRavagers(wither);
                }

                tickCounter++;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onWitherExplosionDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Wither) {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                    event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {

                event.setCancelled(true);
            }
        }
    }
}