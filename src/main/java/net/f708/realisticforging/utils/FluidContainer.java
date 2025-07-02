package net.f708.realisticforging.utils;

import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidContainer {
    FluidStack getFluid();
    void setFluid(FluidStack fluid);
    int getCapacity();
    boolean isEmpty();
    boolean isFull();
}