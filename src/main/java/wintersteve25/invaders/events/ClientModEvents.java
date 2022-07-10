package wintersteve25.invaders.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.client.entities.HubEntityRenderer;
import wintersteve25.invaders.contents.base.builders.InvadersGuiBuilder;
import wintersteve25.invaders.contents.base.functional.IRenderComponent;
import wintersteve25.invaders.contents.base.gui.InvadersButtonData;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;
import wintersteve25.invaders.contents.blocks.core.hub.HubTeamListRenderable;
import wintersteve25.invaders.init.InvadersBlocks;
import wintersteve25.invaders.init.InvadersEntities;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.TextureElement;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

@Mod.EventBusSubscriber(modid = Invaders.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientPreInit(FMLClientSetupEvent event) {
        // hub
        new InvadersGuiBuilder()
                .addButton(new InvadersButtonData(new TextureElement(136, 0, 128, 128, 20, 20), TranslationHelper.gui(InvadersConstants.LangKeys.HUB_LIST_BTN), (btn, scr, renderables) -> scr.changeTab("teams_list")))
                .addButton(new InvadersButtonData(new TextureElement(0, 0, 1, 1, 20, 20), new TranslationTextComponent("hi"), (btn, scr, renderables) -> {}))
                .addComponent(IRenderComponent.renderPlayer(screen -> 85, screen -> 90, 30, false))
                .addComponent(IRenderComponent.renderText(screen -> (int) screen.getTitleLabelX() - 20, screen -> (int) screen.getTitleLabelY() + 37, TextFormatting.AQUA.getColor(), screen -> {
                    TileEntity tile = screen.getMenu().getTileEntity();
                    World world = Minecraft.getInstance().level;
                    if (tile instanceof HubBE && world != null) {
                        PlayerEntity player = world.getPlayerByUUID(((HubBE) tile).getOwner());
                        if (player != null) {
                            return TranslationHelper.gui(InvadersConstants.LangKeys.HUB_OWNER, player.getDisplayName());
                        }
                    }
                    return new StringTextComponent("");
                }, false))
                .addComponent(IRenderComponent.renderText(screen -> (int) screen.getTitleLabelX() - 25, screen -> (int) screen.getTitleLabelY() + 160, TextFormatting.WHITE.getColor(), screen -> {
                    TileEntity tileEntity = screen.getMenu().getTileEntity();
                    if (!(tileEntity instanceof HubBE)) return StringTextComponent.EMPTY;
                    HubBE hubBE = (HubBE) tileEntity;
                    return TranslationHelper.gui(InvadersConstants.LangKeys.HUB_HEALTH, hubBE.getHealth(), hubBE.getMaxHealth());
                }, false))
                .addButton("teams_list", new InvadersButtonData(new TextureElement(279, 0, 128, 128, 32, 32), new TranslationTextComponent("???"), ((button, screen, currentRenderables) -> screen.changeTab("default"))))
                .addComponent("teams_list", new HubTeamListRenderable())
                .titlePos(60, 0)
                .build(InvadersBlocks.Core.HUB_CONTAINER.get());

        RenderingRegistry.registerEntityRenderingHandler(InvadersEntities.HUB.get(), HubEntityRenderer::new);
    }
}
