package net.f708.realisticforging.data;

import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class SmithingHammerComboData implements INBTSerializable<CompoundTag> {

    private int combo = 0;
    private int trueCombo = 0;

    public int getCombo() {
        return combo;
    }

    public int getTrueCombo() {
        return trueCombo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public void setTrueCombo(int trueCombo) {
        this.trueCombo = trueCombo;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("combo", 0);
        tag.putInt("trueCombo", 0);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        combo = nbt.getInt("combo");
        trueCombo = nbt.getInt("trueCombo");
    }

    public void syncData(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer){
//            PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(this));
        }
    }
}
