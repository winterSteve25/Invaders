package wintersteve25.invaders.contents.base.functional;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import wintersteve25.invaders.contents.base.gui.InvadersBaseScreen;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface IRenderComponent {
    static IRenderComponent renderPlayer(Function<InvadersBaseScreen<?>, Integer> x, Function<InvadersBaseScreen<?>, Integer> y, int scale, boolean absoluteValue) {
        return (matrixStack, screen, mc, mouseX, mouseY, partialTicks, windowWidth, windowHeight, renderX, renderY, renderProgress) -> {
            if (mc.player != null) {
                int x1 = x.apply(screen);
                int y1 = y.apply(screen);

                int x2 = absoluteValue ? x1 : renderX + x1;
                int y2 = absoluteValue ? y1 : renderY + y1;

                InvadersBaseScreen.startRender(renderProgress);
                InventoryScreen.renderEntityInInventory(x2, y2, scale, (float)(x2 - mouseX), (float)(y2 - 30 - mouseY), mc.player);
                InvadersBaseScreen.endRender();
            }
        };
    }
    static IRenderComponent renderText(Function<InvadersBaseScreen<?>, Integer> x, Function<InvadersBaseScreen<?>, Integer> y, int color, Function<InvadersBaseScreen<?>, ITextComponent> text, boolean absoluteValue) {
        return (matrixStack, screen, mc, mouseX, mouseY, partialTicks, windowWidth, windowHeight, renderX, renderY, renderProgress) -> {
            int x1 = x.apply(screen);
            int y1 = y.apply(screen);

            int x2 = absoluteValue ? x1 : renderX + x1;
            int y2 = absoluteValue ? y1 : renderY + y1;

            InvadersBaseScreen.startRender(renderProgress);
            mc.font.drawShadow(matrixStack, text.apply(screen), x2, y2, color);
            InvadersBaseScreen.endRender();
        };
    }

    void render(MatrixStack matrixStack, InvadersBaseScreen<?> screen, @Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks, int windowWidth, int windowHeight, int renderX, int renderY, float renderProgress);
}
