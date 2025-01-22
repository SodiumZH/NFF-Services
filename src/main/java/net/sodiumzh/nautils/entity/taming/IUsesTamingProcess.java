package net.sodiumzh.nautils.entity.taming;

import net.minecraft.world.entity.TamableAnimal;
import net.sodiumzh.nautils.annotation.DontOverride;
import net.sodiumzh.nautils.entity.IMobSpecific;
import net.sodiumzh.nautils.entity.anger.CMobAngerHandler;
import net.sodiumzh.nautils.entity.anger.MobAngerHandler;
import net.sodiumzh.nautils.entity.anger.MobAngerRules;
import net.sodiumzh.nautils.registries.NaUtilsCaps;

/**
 * Only for {@link TamableAnimal}, indicating that the mob should use NaUtils' taming process.
 */
public interface IUsesTamingProcess extends IMobSpecific<TamableAnimal> {

    /**
     * Override this method to define which process this mob should use.
     */
    public VanillaAnimalTamingProcess getProcess();

    @DontOverride
    public default CMobAngerHandler getAngerHandler() {
        return asMob().getCapability(NaUtilsCaps.CAP_VANILLA_TAMABLE_ANIMAL_ANGER_HANDLER)
                .orElseGet(() -> new MobAngerHandler(this.asMob(), MobAngerRules.NO_ANGER.get()));
    }

    @DontOverride
    public default CVanillaAnimalTamingProcessHandler getProcessHandler() {
        return this.asMob().getCapability(NaUtilsCaps.CAP_VANILLA_ANIMAL_TAMING_PROCESS_HANDLER_CAPABILITY)
                .orElseGet(() -> new CVanillaAnimalTamingProcessHandler.Impl(this));
    }

    public default MobAngerRules getTamingAngerRules() {
        return MobAngerRules.ATTACKER.get();
    }
}
