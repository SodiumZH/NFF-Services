package net.sodiumzh.nff.services.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sodiumzh.nff.services.entity.taming.INFFTamed;
import net.sodiumzh.nfu.entity.MobRespawnInfo;
import org.jetbrains.annotations.Nullable;

public class NFFMobRespawnInfo extends MobRespawnInfo {
    @Override
    protected void afterRespawn(Mob mob, Level level, @Nullable Player player) {
        if (mob instanceof INFFTamed b)
        {
            //b.setInventoryFromMob();
            b.updateAnchor();
            b.setInit();
        }
    }
}
