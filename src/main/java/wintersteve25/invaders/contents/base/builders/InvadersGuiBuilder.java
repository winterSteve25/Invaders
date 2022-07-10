package wintersteve25.invaders.contents.base.builders;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.ContainerType;
import wintersteve25.invaders.contents.base.ONIBaseContainer;
import wintersteve25.invaders.contents.base.functional.IRenderComponent;
import wintersteve25.invaders.contents.base.gui.InvadersBaseScreen;
import wintersteve25.invaders.contents.base.gui.InvadersButtonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvadersGuiBuilder {

    private final Map<String, List<InvadersButtonData>> buttons;
    private final Map<String, List<IRenderComponent>> renderables;
    private int titleX = 0;
    private int titleY = 0;
    private boolean disableTitle = false;

    public InvadersGuiBuilder() {
        buttons = new HashMap<>();
        renderables = new HashMap<>();
        buttons.put("default", new ArrayList<>());
        renderables.put("default", new ArrayList<>());
    }

    public InvadersGuiBuilder addButton(String tab, InvadersButtonData button) {
        if (!buttons.containsKey(tab)) {
            buttons.put(tab, new ArrayList<>());
        }

        buttons.get(tab).add(button);

        return this;
    }

    public InvadersGuiBuilder addButton(InvadersButtonData button) {
        return addButton("default", button);
    }

    public InvadersGuiBuilder addComponent(String tab, IRenderComponent renderable) {
        if (!renderables.containsKey(tab)) {
            renderables.put(tab, new ArrayList<>());
        }

        renderables.get(tab).add(renderable);

        return this;
    }

    public InvadersGuiBuilder addComponent(IRenderComponent renderable) {
        return addComponent("default", renderable);
    }

    public InvadersGuiBuilder titlePos(int xMod, int yMod) {
        this.titleX = xMod;
        this.titleY = yMod;
        return this;
    }

    public InvadersGuiBuilder disableTitle() {
        disableTitle = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <M extends ONIBaseContainer, U extends Screen & IHasContainer<M>> ScreenManager.IScreenFactory<M, U> buildNewScreenInstance() {
        return (container, inventory, title) -> (U) new InvadersBaseScreen<ONIBaseContainer>(container, inventory, title, buttons, renderables, disableTitle, titleX, titleY);
    }

    public void build(ContainerType<? extends ONIBaseContainer> containerType) {
        ScreenManager.register(containerType, buildNewScreenInstance());
    }
}
