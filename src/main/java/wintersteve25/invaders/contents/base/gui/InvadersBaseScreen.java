package wintersteve25.invaders.contents.base.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;
import wintersteve25.invaders.contents.base.ONIBaseContainer;
import wintersteve25.invaders.contents.base.functional.IPressReaction;
import wintersteve25.invaders.contents.base.functional.IRenderComponent;
import wintersteve25.invaders.init.InvadersConfigs;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.TextureElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvadersBaseScreen<T extends ONIBaseContainer> extends ContainerScreen<T> {

    protected int xRenderPos;
    protected int yRenderPos;
    protected int extraHeight;

    protected float renderProgress;
    protected Map<String, List<InvadersButtonData>> btns;
    protected Map<String, List<IRenderComponent>> renderable;
    protected boolean disableTitle;
    protected int titleX;
    protected int titleY;

    protected String currentTab = "default";
    
    private List<Widget> widgetsInTab;

    public InvadersBaseScreen(T container, PlayerInventory inv, ITextComponent name, Map<String, List<InvadersButtonData>> buttons, Map<String, List<IRenderComponent>> renderables, boolean disableTitle, int titleX, int titleY) {
        super(container, inv, name);

        this.btns = buttons;
        this.renderable = renderables;
        this.disableTitle = disableTitle;
        this.titleX = titleX;
        this.titleY = titleY;

        this.renderProgress = 0.01f;
        this.extraHeight = 25;
        
        this.widgetsInTab = new ArrayList<>();
    }

    public InvadersBaseScreen(T container, PlayerInventory inv, ITextComponent name) {
        this(container, inv, name, ImmutableMap.of("default", new ArrayList<>()), ImmutableMap.of("default", new ArrayList<>()), false, 0, 0);
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.getXSize()) / 2;
        this.topPos = (this.height - this.getYSize()) / 2;

        xRenderPos = this.leftPos;
        yRenderPos = this.topPos - extraHeight;

        int initialBtnPos = yRenderPos + 2;

        for (InvadersButtonData button : btns.get(currentTab)) {
            Button btn;
            
            if (button.getX() == 0 && button.getY() == 0) {
                btn = new SpriteButton(this.xRenderPos - 24, initialBtnPos, button, this);
                addButton(btn);
            } else {
                btn = new SpriteButton(button.getX(), button.getY(), button, this);
                addButton(btn);
            }
            
            widgetsInTab.add(btn);
            initialBtnPos += button.getHeight() + 2;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        for (Widget widget : this.buttons) {
            if (widget instanceof Button) {
                ((Button) widget).init(renderProgress);
            }
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        if (minecraft == null) return;
        startRender(renderProgress);

        this.minecraft.getTextureManager().bind(InvadersConstants.Resources.BASE_GUI);
        xRenderPos = this.leftPos;
        yRenderPos = this.topPos - extraHeight;
        blit(pMatrixStack, xRenderPos, yRenderPos, this.getXSize(), this.getYSize() + extraHeight, 81, 43, 350, 430, 512, 512);

        for (IRenderComponent renderable : renderable.get(currentTab)) {
            renderable.render(pMatrixStack, this, this.minecraft, pX, pY, pPartialTicks, this.width, this.height, xRenderPos, yRenderPos, renderProgress);
        }

        if (renderProgress < 1) {
            renderProgress += InvadersConfigs.Client.SHOW_SPEED.get();
        }

        endRender();
    }

    @Override
    protected void renderLabels(MatrixStack pMatrixStack, int x, int y) {
        if (!disableTitle) {
            startRender(renderProgress);
            this.font.drawShadow(pMatrixStack, this.title, (float)this.titleLabelX + titleX, (float)this.titleLabelY - 22 + titleY, 16777215);
            endRender();
        }
    }

    protected int getScaledProgress(float pixels) {
        float totalProgress = menu.getTotalProgress();
        float progress = totalProgress - menu.getProgress();

        if (totalProgress != 0) {
            float result = progress * pixels / totalProgress;
            return Math.round(result);
        }

        return 0;
    }

    public int getXRenderPos() {
        return xRenderPos;
    }

    public int getYRenderPos() {
        return yRenderPos;
    }

    public int getExtraHeight() {
        return extraHeight;
    }

    public float getTitleLabelX() {
        return (float)this.titleLabelX + titleX;
    }

    public float getTitleLabelY() {
        return (float)this.titleLabelY - 22 + titleY;
    }

    public void changeTab(String to) {
        this.children.removeAll(widgetsInTab);
        this.buttons.removeAll(widgetsInTab);
        this.widgetsInTab.clear();
        this.currentTab = to;
        init();
    }

    public static void startRender(float renderProgress) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1F, 1F, 1F, renderProgress);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static abstract class Button extends AbstractButton {
        private final ITextProperties name;

        protected float renderProgress;
        protected InvadersBaseScreen<?> screen;
        protected IPressReaction onPress;

        protected Button(int x, int y, int width, int height, ITextProperties name, InvadersBaseScreen<?> screen, IPressReaction onPress) {
            super(x, y, width, height, StringTextComponent.EMPTY);
            this.name = name;
            this.screen = screen;
            this.onPress = onPress;
        }

        public void init(float renderProgress) {
            this.renderProgress = renderProgress;
        }

        @Override
        public void onPress() {
            onPress.onPress(this, screen, screen.renderable.get(screen.currentTab));
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            Minecraft.getInstance().getTextureManager().bind(InvadersConstants.Resources.WIDGETS);

            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();

            if (this.isHovered()) {
                RenderSystem.color4f(0.2f, 0.5f, 0.3f, renderProgress);
            } else {
                RenderSystem.color4f(1F, 1F, 1F, renderProgress);
            }

            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            blit(matrixStack, this.x, this.y, 20, 20, 0, 0, 128, 128, 1024, 1024);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();

            this.blitOverlay(matrixStack);

            if (this.isHovered()) {
                GuiUtils.drawHoveringText(matrixStack, Lists.newArrayList(name), mouseX, mouseY, screen.width, screen.height, 200, Minecraft.getInstance().font);
            }
        }

        public float getRenderProgress() {
            return renderProgress;
        }

        public void setPosition(int xIn, int yIn) {
            this.x = xIn;
            this.y = yIn;
        }

        protected abstract void blitOverlay(MatrixStack matrixStack);
    }

    public static void endRender() {
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    public static class SpriteButton extends Button {
        protected int u;
        protected int v;
        protected int uSize;
        protected int vSize;

        public SpriteButton(int x, int y, int u, int v, int uSize, int ySize, int width, int height, ITextProperties name, IPressReaction onPress, InvadersBaseScreen<?> screen) {
            super(x, y, width, height, name, screen, onPress);

            this.u = u;
            this.v = v;
            this.uSize = uSize;
            this.vSize = ySize;
        }

        public SpriteButton(int x, int y, TextureElement element, ITextProperties name, IPressReaction onPress, InvadersBaseScreen<?> screen) {
            this(x, y, element.getU(), element.getV(), element.getUSize(), element.getVSize(), element.getWidth(), element.getHeight(), name, onPress, screen);
        }

        public SpriteButton(int x, int y, InvadersButtonData buttonData, InvadersBaseScreen<?> screen) {
            this(x, y, buttonData.getElement().getU(), buttonData.getElement().getV(), buttonData.getElement().getUSize(), buttonData.getElement().getVSize(), buttonData.getWidth(), buttonData.getHeight(), buttonData.getName(), buttonData.getOnPress(), screen);
        }

        protected void setU(int u) {
            this.u = u;
        }

        protected void setV(int v) {
            this.v = v;
        }

        protected void blitOverlay(MatrixStack matrixStack) {
            startRender(renderProgress);
            blit(matrixStack, this.x, this.y, 20, 20, this.u, this.v, this.uSize, this.vSize, 1024, 1024);
            endRender();
        }
    }
}
