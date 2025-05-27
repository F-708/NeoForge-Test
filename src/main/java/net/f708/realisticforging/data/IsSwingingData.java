package net.f708.realisticforging.data;

import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class IsSwingingData implements INBTSerializable<CompoundTag> {

    private boolean isSwinging = false;

    public boolean isSwinging() {
        return isSwinging;
    }
    public void setSwinging(boolean isSwinging) {
        this.isSwinging = isSwinging;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isSwinging", isSwinging);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        isSwinging = nbt.getBoolean("isSwinging");
    }

    public void syncData(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new PacketTriggerPlayerSwing(this));
        }
    }
}
