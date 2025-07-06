package net.f708.realisticforging.data;

import net.f708.realisticforging.RealisticForging;
import net.f708.realisticforging.network.packets.PacketSmithingCombo;
import net.f708.realisticforging.network.packets.PacketTriggerPlayerSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class SmithingHammerComboData implements INBTSerializable<CompoundTag> {

    private int combo;

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public void descreaseCombo(){
        this.combo--;
    }

    public void increaseCombo(){
        this.combo++;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("combo", this.combo);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        combo = nbt.getInt("combo");
    }

    public void syncData(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new PacketSmithingCombo(this));
        }
    }
}
