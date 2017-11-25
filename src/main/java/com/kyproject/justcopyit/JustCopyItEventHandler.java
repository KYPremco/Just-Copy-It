package com.kyproject.justcopyit;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class JustCopyItEventHandler {


    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {

    }


    @SubscribeEvent
    public void livingDrops(LivingDropsEvent event) {

    }

    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {

    }

    @SubscribeEvent
    public void itemPickup(PlayerEvent.ItemPickupEvent event) {

    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {

    }

    @SubscribeEvent
    public void itemTooltip(ItemTooltipEvent event) {

    }

    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void harvestEvent(BlockEvent.HarvestDropsEvent event) {


    }

}
