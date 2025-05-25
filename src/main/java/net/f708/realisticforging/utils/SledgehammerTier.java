package net.f708.realisticforging.utils;

import java.util.Random;

public enum SledgehammerTier {
    IRON(2, 3, false),     // Железная: 2-3 блока, без глубины
    DIAMOND(3, 4, false),  // Алмазная: 3-4 блока, без глубины
    NETHERITE(4, 5, true); // Незеритовая: 4-5 блоков, один в глубину

    private final int minBlocks;
    private final int maxBlocks;
    private final boolean allowDepth;

    SledgehammerTier(int minBlocks, int maxBlocks, boolean allowDepth) {
        this.minBlocks = minBlocks;
        this.maxBlocks = maxBlocks;
        this.allowDepth = allowDepth;
    }

    public int getRandomBlockCount(Random random) {
        return random.nextInt(minBlocks, maxBlocks + 1);
    }
}
