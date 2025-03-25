package net.kinoko2k.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.*;

public class MobBreakBlockAI implements Listener {
    private final JavaPlugin plugin;

    private static final Set<EntityType> BLOCK_BREAKING_MOBS = EnumSet.of(
            ZOMBIE, HUSK, DROWNED,
            SKELETON, STRAY, WITHER_SKELETON
    );

    private static final Set<Material> UNBREAKABLE_BLOCKS = EnumSet.of(
            BEDROCK, OBSIDIAN, END_PORTAL_FRAME,
            BARRIER, NETHER_PORTAL, REINFORCED_DEEPSLATE
    );

    public MobBreakBlockAI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobTargetPlayer(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();
            if (BLOCK_BREAKING_MOBS.contains(mob.getType())) {
                startBreakingBlocks(mob, (Player) event.getTarget());
            }
        }
    }

    private void startBreakingBlocks(Monster mob, Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mob.isValid() || target == null || !target.isOnline()) {
                    cancel();
                    return;
                }

                Location mobLoc = mob.getLocation();
                Location frontLoc = mobLoc.clone().add(mobLoc.getDirection().multiply(1));
                Block frontBlock = frontLoc.getBlock();

                if (!frontBlock.getType().isAir() && !UNBREAKABLE_BLOCKS.contains(frontBlock.getType())) {
                    breakBlock(frontBlock);
                }
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }

    private void breakBlock(Block block) {
        new BukkitRunnable() {
            int progress = 0;
            final int maxProgress = 5;

            @Override
            public void run() {
                if (progress >= maxProgress) {
                    block.breakNaturally();
                    cancel();
                    return;
                }
                progress++;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
