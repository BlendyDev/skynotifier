package me.blendy.skynotifier;

import me.blendy.skynotifier.util.ImBadAtNamingClassesUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class BlockDataScanner {
    public static final KeyBinding KEYBINDING = new KeyBinding("Copy block data", Keyboard.KEY_F4, "Skynotifier");
    public static void copyBlockData (EntityPlayerSP player) {
        World world = player.getEntityWorld();
        Vec3 playerPos = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        double reachDistance = 15.0D;

        Vec3 rayTraceEnd = playerPos.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
        MovingObjectPosition result = player.worldObj.rayTraceBlocks(playerPos, rayTraceEnd);

        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = result.getBlockPos();
            IBlockState blockState = player.worldObj.getBlockState(blockPos);
            TileEntity tileEntity = world.getTileEntity(blockPos);
            int meta = blockState.getBlock().getMetaFromState(blockState);
            if (tileEntity instanceof IInventory) {
                NBTTagCompound tileData = new NBTTagCompound();
                tileEntity.writeToNBT(tileData);
                ImBadAtNamingClassesUtil.copyToClipboard(meta + " " + tileData);
                player.addChatMessage(new ChatComponentText("§aCopied tile entity NBT data!"));
            } else {
                ImBadAtNamingClassesUtil.copyToClipboard(String.valueOf(meta));
                player.addChatMessage(new ChatComponentText("§aCopied block metadata!"));
            }
        } else player.addChatMessage(new ChatComponentText("§cCouldn't find a block to copy data from."));

    }
}
