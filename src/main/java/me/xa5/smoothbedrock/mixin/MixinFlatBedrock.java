package me.xa5.smoothbedrock.mixin;

import me.xa5.smoothbedrock.SmoothBedrock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public abstract class MixinFlatBedrock extends ChunkGenerator {
    @Shadow
    @Final
    protected Supplier<ChunkGeneratorSettings> settings;
    @Shadow
    @Final
    private int worldHeight; // worldHeight
    @Shadow
    @Final
    protected ChunkRandom random;
    ThreadLocal<Identifier> dimId = new ThreadLocal<>();

    public MixinFlatBedrock(BiomeSource biomeSource_1, StructuresConfig chunkGeneratorConfig_1) {
        super(biomeSource_1, chunkGeneratorConfig_1);
    }

    @Inject(method = "buildSurface", at = @At("HEAD"), cancellable = true)
    public void test(ChunkRegion region, Chunk chunk, CallbackInfo info) {
        // TODO why is this deprecated? Maybe there's a better way to get the ID.
        dimId.set(region.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(region.getDimension()));
    }

    @Inject(method = "buildBedrock", at = @At("HEAD"), cancellable = true)
    private void buildBedrock(Chunk chunk, Random rand, CallbackInfo info) {
        if (SmoothBedrock.getInstance().shouldModifyBedrock(dimId.get())) {
            info.cancel();

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int chunkStartX = chunk.getPos().getStartX();
            int chunkStartZ = chunk.getPos().getStartZ();
            int bedrockFloor = this.settings.get().getBedrockFloorY();
            int bedrockRoof = worldHeight - 1 - this.settings.get().getBedrockCeilingY();
            boolean generateRoof = bedrockRoof + 4 >= 0 && bedrockRoof < this.worldHeight;
            boolean generateFloor = bedrockFloor + 4 >= 0 && bedrockFloor < this.worldHeight;

            if (generateFloor || generateRoof) {
                Iterator<BlockPos> chunkBlocks = BlockPos.iterate(chunkStartX, 0, chunkStartZ, chunkStartX + 15, 0, chunkStartZ + 15).iterator();
                while (true) {
                    BlockPos blockPos;
                    do {
                        if (!chunkBlocks.hasNext()) {
                            return;
                        }

                        blockPos = chunkBlocks.next();
                        if (generateRoof) {
                            chunk.setBlockState(mutable.set(blockPos.getX(), bedrockRoof, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                        }
                    } while (!generateFloor);

                    chunk.setBlockState(mutable.set(blockPos.getX(), bedrockFloor, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                }
            }
        }
    }
}