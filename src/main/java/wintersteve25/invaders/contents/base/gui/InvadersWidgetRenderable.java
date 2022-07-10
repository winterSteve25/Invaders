package wintersteve25.invaders.contents.base.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import wintersteve25.invaders.contents.base.functional.IRenderComponent;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.TextureElement;

import javax.annotation.Nonnull;

public class InvadersWidgetRenderable implements IRenderComponent {

    private final TextureElement element;
    private final int x;
    private final int y;

    public InvadersWidgetRenderable(TextureElement element, int x, int y) {
        this.element = element;
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(MatrixStack matrixStack, InvadersBaseScreen<?> screen, @Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks, int windowWidth, int windowHeight, int renderX, int renderY, float renderProgress) {
        InvadersBaseScreen.startRender(renderProgress);
        bindWidgetsTexture();
        AbstractGui.blit(matrixStack, this.x, this.y, element.getWidth(), element.getHeight(), element.getU(), element.getV(), element.getUSize(), element.getVSize(), 1024, 1024);
        InvadersBaseScreen.endRender();
    }

    public static void bindWidgetsTexture() {
        bindTexture(InvadersConstants.Resources.WIDGETS);
    }

    public static void bindTexture(ResourceLocation resourceLocation) {
        Minecraft.getInstance().getTextureManager().bind(resourceLocation);
    }
}
