package net.kinoko2k.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.entity.EntityType.*;

public class MobJumpAI implements Listener {
    private final JavaPlugin plugin;

    private static final Set<EntityType> JUMPING_MOBS = EnumSet.of(ZOMBIE, HUSK, DROWNED,
            SKELETON, STRAY, WITHER_SKELETON);

    public MobJumpAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobTargetPlayer(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();
            if (JUMPING_MOBS.contains(mob.getType())) {
                startJumpingAI(mob, (Player) event.getTarget());
            }
        }
    }

    private void startJumpingAI(Monster mob, Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mob.isValid() || target == null || !target.isOnline()) {
                    cancel();
                    return;
                }

                Location mobLoc = mob.getLocation();
                Location targetLoc = target.getLocation();

                if (mobLoc.distance(targetLoc) > 15) {
                    cancel();
                    return;
                }

                Location front = mobLoc.clone().add(mobLoc.getDirection().multiply(1.5));

                if (isGap(front)) {
                    for (int i = 1; i <= 3; i++) {
                        Location landingSpot = front.clone().add(mobLoc.getDirection().multiply(i));
                        if (landingSpot.getBlock().getType().isSolid()) {
                            jumpMob(mob, i);
                            break;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private boolean isGap(Location loc) {
        return loc.getBlock().getType() == Material.AIR &&
                loc.clone().add(0, -1, 0).getBlock().getType() == Material.AIR &&
                loc.clone().add(0, -2, 0).getBlock().getType() == Material.AIR;
    }

    private void jumpMob(LivingEntity mob, int gapSize) {
        Vector velocity = mob.getVelocity();
        velocity.setY(0.6);
        velocity.setX(mob.getLocation().getDirection().getX() * (0.8 + (0.2 * gapSize)));
        velocity.setZ(mob.getLocation().getDirection().getZ() * (0.8 + (0.2 * gapSize)));
        mob.setVelocity(velocity);
    }
}
