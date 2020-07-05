package me.xa5.smoothbedrock.mixin;

import me.xa5.smoothbedrock.SmoothBedrock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Random;

@Mixin(SurfaceChunkGenerator.class)
public abstract class MixinFlatBedrock extends ChunkGenerator {
    @Shadow @Final protected ChunkGeneratorType field_24774;
    ThreadLocal<Identifier> dimId = new ThreadLocal<>();

    public MixinFlatBedrock(BiomeSource biomeSource_1, StructuresConfig chunkGeneratorConfig_1) {
        super(biomeSource_1, chunkGeneratorConfig_1);
    }

    @Inject(method = "buildSurface", at=@At("HEAD"), cancellable = true)
    public void test(ChunkRegion region, Chunk chunk, CallbackInfo info){
        // TODO why is this deprecated? Maybe there's a better way to get the ID.
        dimId.set(region.getWorld().getDimensionRegistryKey().getValue());
    }

    @Inject(method = "buildBedrock", at = @At("HEAD"), cancellable = true)
    private void buildBedrock(Chunk chunk, Random rand, CallbackInfo info) {
        if (SmoothBedrock.getInstance().shouldModifyBedrock(dimId.get())) {


            BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
            int chunkXStart = chunk.getPos().getStartX();
            int chunkZStart = chunk.getPos().getStartZ();
            int minY = field_24774.getBedrockFloorY(); // Overworld: 0. Nether: 0
            int maxY = field_24774.getBedrockCeilingY(); // Overworld: 0. Nether: 127
            Iterator<BlockPos> var9 = BlockPos.iterate(chunkXStart, 0, chunkZStart, chunkXStart + 16, 0, chunkZStart + 16).iterator();
            while (true) {
                BlockPos blockPos_1;
                do {
                    if (!var9.hasNext()) {
                        info.cancel(); // Prevent vanilla code from running.
                        return;
                    }

                    blockPos_1 = (BlockPos) var9.next();
                    if (maxY > 0) {
                        // CavesChunkGeneratorSettings overrides maxY to provide a bedrock roof.
                        // This code will only be run by worlds with a custom maxY.

                        mutableBlockPos.set(blockPos_1.getX(), maxY, blockPos_1.getZ());
                        chunk.setBlockState(mutableBlockPos, Blocks.BEDROCK.getDefaultState(), false);
                    }
                } while (minY >= 256);

                // Generate world floor.
                mutableBlockPos.set(blockPos_1.getX(), minY, blockPos_1.getZ());
                chunk.setBlockState(mutableBlockPos, Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }
}