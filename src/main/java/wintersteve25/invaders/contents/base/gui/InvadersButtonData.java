package wintersteve25.invaders.contents.base.gui;

import net.minecraft.util.text.ITextProperties;
import wintersteve25.invaders.contents.base.functional.IPressReaction;
import wintersteve25.invaders.utils.TextureElement;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InvadersButtonData {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final TextureElement element;
    private final ITextProperties name;
    private final IPressReaction onPress;

    public InvadersButtonData(int x, int y, TextureElement element, ITextProperties name, IPressReaction onPress) {
        this.x = x;
        this.y = y;
        this.width = element.getWidth();
        this.height = element.getHeight();
        this.element = element;
        this.name = name;
        this.onPress = onPress;
    }

    public InvadersButtonData(TextureElement element, ITextProperties name, IPressReaction onPress) {
        this(0, 0, element, name, onPress);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TextureElement getElement() {
        return element;
    }

    public ITextProperties getName() {
        return name;
    }

    public IPressReaction getOnPress() {
        return onPress;
    }
}
