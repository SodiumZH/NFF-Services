package net.sodiumzh.nfu.entity.taming;

import net.minecraft.world.entity.TamableAnimal;
import net.sodiumzh.nfu.annotation.DontOverride;
import net.sodiumzh.nfu.entity.IMobSpecific;
import net.sodiumzh.nfu.entity.anger.CMobAngerHandler;
import net.sodiumzh.nfu.entity.anger.MobAngerHandler;
import net.sodiumzh.nfu.entity.anger.MobAngerRules;
import net.sodiumzh.nfu.registry.NFUCaps;

/**
 * Only for {@link TamableAnimal}, indicating that the mob should use NFU' taming process.
 */
public interface IUsesTamingProcess extends IMobSpecific<TamableAnimal> {

    /**
     * Override this method to define which process this mob should use.
     */
    public VanillaAnimalTamingProcess getProcess();

    @DontOverride
    public default CMobAngerHandler getAngerHandler() {
        return asMob().getCapability(NFUCaps.CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER)
                .orElseGet(() -> new MobAngerHandler(this.asMob(), MobAngerRules.NO_ANGER.get()));
    }

    @DontOverride
    public default CVanillaAnimalTamingProcessHandler getProcessHandler() {
        return this.asMob().getCapability(NFUCaps.CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY)
                .orElseGet(() -> new CVanillaAnimalTamingProcessHandler.Impl(this));
    }

    public default MobAngerRules getTamingAngerRules() {
        return MobAngerRules.ATTACKER.get();
    }
}
