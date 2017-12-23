package com.kyproject.justcopyit.commands;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JciCommands extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "jci";
    }

    @Nonnull
    @Override
    public String getUsage(ICommandSender sender) {
        return "/jci <memorycard|JciCommands>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length > 0) {
            switch (args[0]) {
                case "memorycard":
                    this.giveMemorycard(args, sender);
                    break;
                case "?":
                case "help":
                    sender.sendMessage( new TextComponentString("§cUsage: " +  this.getUsage(sender)));
                    break;
                case "reload":
                    this.reloadFitler(sender);
                    break;
                default:
                    break;
            }
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "memorycard", "reload");
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }

    private void reloadFitler(ICommandSender sender) {
        TileEntityBuilder tileEntityBuilder = new TileEntityBuilder();
        TileEntityBuilder.filter = tileEntityBuilder.readJsonFilter();
        StructureTemplate structureTemplate = new StructureTemplate();
        structureTemplate.loadBlockItemFilter();

        sender.sendMessage( new TextComponentString("§2[JCI] Filter is updated!"));
        sender.sendMessage( new TextComponentString("§2[JCI] Layer filter is updated!"));

    }

    private void giveMemorycard(String[] args, ICommandSender sender) {
        if(args.length > 1){
            if(!args[1].equals("?")) {
                getUsage(sender);
                EntityPlayer player = (EntityPlayer) sender;
                World world = ((EntityPlayer) sender).world;
                StructureTemplate structureTemplate = new StructureTemplate();

                NBTTagCompound nbt = structureTemplate.getNBT(args[1]);

                if(nbt != null) {
                    ItemStack item = new ItemStack(ModItems.MEMORY_CARD);
                    item.setTagCompound(nbt);
                    if (player.inventory.getFirstEmptyStack() != -1) {
                        player.inventory.addItemStackToInventory(item);
                    } else {
                        world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, item));
                    }
                } else {
                    JustCopyIt.logger.warn("File doesn't exist!!");
                    sender.sendMessage( new TextComponentString("§cFile doesn't exist!!"));
                }
            } else {
                sender.sendMessage( new TextComponentString("§cUsage: /jci memorycard <file name> <usages>"));
            }
        } else {
            this.getUsage(sender);
            sender.sendMessage( new TextComponentString("§cUsage: /jci memorycard <file name> <usages>"));
        }
    }


}
