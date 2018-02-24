package com.kyproject.justcopyit.commands;

import com.kyproject.justcopyit.JustCopyIt;
import com.kyproject.justcopyit.init.Filters;
import com.kyproject.justcopyit.init.ModItems;
import com.kyproject.justcopyit.templates.StructureTemplate;
import com.kyproject.justcopyit.tileentity.TileEntityBuilder;
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
        return "/jci <memorycard|reload>";
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
        Filters filter = new Filters();
        TileEntityBuilder.filter = filter.readJsonFilter();
        TileEntityBuilder.blacklist = filter.readJsonBlacklist();
        StructureTemplate structureTemplate = new StructureTemplate();
        structureTemplate.loadBlockLayerFilter();

        sender.sendMessage( new TextComponentString("§2[JCI] Filter is updated!"));
        sender.sendMessage( new TextComponentString("§2[JCI] Layer filter is updated!"));
        sender.sendMessage( new TextComponentString("§2[JCI] Blacklist is updated!"));

    }

    private void giveMemorycard(String[] args, ICommandSender sender) {
        if(args.length > 1) {
            if(!args[1].equals("?") && !args[1].equals("help")) {
                EntityPlayer player = (EntityPlayer) sender;
                World world = ((EntityPlayer) sender).world;
                StructureTemplate structureTemplate = new StructureTemplate();

                NBTTagCompound nbt = structureTemplate.getNBT(args[1]);

                if(nbt != null) {
                    if(args.length > 2) {
                        if(this.tryParseInt(args[2]) != null) {
                            int durability = Integer.parseInt(args[2]);
                            if(durability <= 0) {
                                nbt.setInteger("durability", -1);
                            } else {
                                nbt.setInteger("durability", durability);
                            }
                        } else {
                            sender.sendMessage( new TextComponentString("§cUsage: /jci memorycard <file name> <usages> <creative>"));
                        }
                    } else {
                        if(nbt.hasKey("durability")) {
                            nbt.setInteger("durability", nbt.getInteger("durability"));
                        } else {
                            nbt.setInteger("durability", -1);
                        }
                    }


                    ItemStack item;
                    if(args.length > 3) {
                        if(args[3].equals("true")) {
                            item = new ItemStack(ModItems.BLUEPRINT_CREATIVE);
                        } else {
                            item = new ItemStack(ModItems.BLUEPRINT);
                        }
                    } else {
                        item = new ItemStack(ModItems.BLUEPRINT);
                    }

                    item.setTagCompound(nbt);
                    if (player.inventory.getFirstEmptyStack() != -1) {
                        player.inventory.addItemStackToInventory(item);
                    } else {
                        world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, item));
                    }
                } else {
                    JustCopyIt.logger.warn("[JCI] File doesn't exist!!");
                    sender.sendMessage( new TextComponentString("§c[JCI] File doesn't exist"));
                }
            } else {
                sender.sendMessage( new TextComponentString("§cUsage: /jci memorycard <file name> <usages> <creative>"));
            }
        } else {
            this.getUsage(sender);
            sender.sendMessage( new TextComponentString("§cUsage: /jci memorycard <file name> <usages> <creative>"));
        }
    }

    private Integer tryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
