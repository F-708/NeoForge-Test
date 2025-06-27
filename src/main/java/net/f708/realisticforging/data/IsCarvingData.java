package net.f708.realisticforging.data;

import net.f708.realisticforging.network.packets.PacketTriggerPlayerCarving;
import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class IsCarvingData implements INBTSerializable<CompoundTag> {

    private boolean isCarving = false;

    public boolean isCarving() {
        return isCarving;
    }
    public void setCarving(boolean isCarving) {
        this.isCarving = isCarving;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isCarving", isCarving);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        isCarving = nbt.getBoolean("isCarving");
    }

    public void syncData(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerCarving(this));
        }
    }
}
