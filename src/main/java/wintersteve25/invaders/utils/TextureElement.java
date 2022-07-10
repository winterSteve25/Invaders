package wintersteve25.invaders.utils;

import net.minecraft.util.ResourceLocation;

public class TextureElement {
    private final int U;
    private final int V;
    private final int USize;
    private final int VSize;
    private final int width;
    private final int height;
    private final ResourceLocation textureLocation;

    public TextureElement(int U, int V, int uSize, int vSize, int width, int height) {
        this.U = U;
        this.V = V;
        USize = uSize;
        VSize = vSize;
        this.width = width;
        this.height = height;
        textureLocation = InvadersConstants.Resources.WIDGETS;
    }

    public TextureElement(int U, int V, int uSize, int vSize, int width, int height, ResourceLocation textureLocation) {
        this.U = U;
        this.V = V;
        this.USize = uSize;
        this.VSize = vSize;
        this.width = width;
        this.height = height;
        this.textureLocation = textureLocation;
    }

    public static TextureElement createSlot(int U, int V) {
        return new TextureElement(U, V, 16, 16, 18, 18);
    }

    public static TextureElement createDefault(int U, int V) {
        return new TextureElement(U, V, 16, 16, 16, 16);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getU() {
        return U;
    }

    public int getV() {
        return V;
    }

    public int getUSize() {
        return USize;
    }

    public int getVSize() {
        return VSize;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }
}
