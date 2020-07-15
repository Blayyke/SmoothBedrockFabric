package me.xa5.smoothbedrock.mixin;

import me.xa5.smoothbedrock.SmoothBedrock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
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
    @Shadow
    @Final
    protected ChunkGeneratorType field_24774;
    @Shadow
    @Final
    private int field_24779; // worldHeight
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
        dimId.set(region.getWorld().getDimensionRegistryKey().getValue());
    }

    @Inject(method = "buildBedrock", at = @At("HEAD"), cancellable = true)
    private void buildBedrock(Chunk chunk, Random rand, CallbackInfo info) {
        if (SmoothBedrock.getInstance().shouldModifyBedrock(dimId.get())) {
            info.cancel();

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int chunkStartX = chunk.getPos().getStartX();
            int chunkStartZ = chunk.getPos().getStartZ();
            int bedrockFloor = this.field_24774.getBedrockFloorY();
            int bedrockRoof = field_24779 - 1 - this.field_24774.getBedrockCeilingY();
            boolean generateRoof = bedrockRoof + 4 >= 0 && bedrockRoof < this.field_24779;
            boolean generateFloor = bedrockFloor + 4 >= 0 && bedrockFloor < this.field_24779;

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