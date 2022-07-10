package wintersteve25.invaders.contents.base.functional;

import wintersteve25.invaders.contents.base.gui.InvadersBaseScreen;

import java.util.List;

public interface IPressReaction {
    void onPress(InvadersBaseScreen.Button button, InvadersBaseScreen<?> screen, List<IRenderComponent> currentRenderables);
}
