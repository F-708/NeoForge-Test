package net.f708.examplemod.utils;

import net.minecraft.world.InteractionHand;

public class Helpers {

    public static boolean isRH(InteractionHand hand){
        return hand == InteractionHand.MAIN_HAND;
    }

    public static boolean isLH(InteractionHand hand){
        return hand == InteractionHand.OFF_HAND;
    }


}
