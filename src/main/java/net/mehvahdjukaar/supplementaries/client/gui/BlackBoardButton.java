package net.mehvahdjukaar.supplementaries.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.common.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.SoundEvents;


public class BlackBoardButton extends AbstractGui implements IRenderable, IGuiEventListener {

    public int u;
    public int v;
    public int x;
    public int y;
    public static final int WIDTH =6;
    private boolean wasHovered;
    protected boolean isHovered;
    public boolean on = false;
    private boolean focused;

    private final BlackBoardButton.IDraggable onDragged;

    protected final BlackBoardButton.IPressable onPress;

    public BlackBoardButton(int center_x, int center_y, int u, int v, BlackBoardButton.IPressable pressedAction,
                            BlackBoardButton.IDraggable dragAction) {
        this.x = center_x-((8-u)* WIDTH);
        this.y = center_y-((-v)* WIDTH);
        this.u = u;
        this.v = v;
        this.onPress = pressedAction;
        this.onDragged = dragAction;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = this.isMouseOver(mouseX,mouseY);
        this.renderButton(matrixStack);
        this.wasHovered = this.isHovered();

    }


    public void renderButton(MatrixStack matrixStack) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(Textures.BLACKBOARD_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int b = this.on?16:0;


        RenderSystem.color4f(1, 1, 1, 1);
        blit(matrixStack, this.x, this.y, (this.u+b)* WIDTH, this.v* WIDTH, WIDTH, WIDTH,32* WIDTH,16* WIDTH);

    }

    public void renderTooltip(MatrixStack matrixStack) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1);
        blit(matrixStack, this.x-1, this.y-1, 16* WIDTH, 0, WIDTH +2 , WIDTH +2,32* WIDTH,16* WIDTH);
        this.renderButton(matrixStack);
    }

    //toggle
    public void onClick(double mouseX, double mouseY) {
        this.on = !this.on;
        this.onPress.onPress(this.u,this.v,this.on);

    }

    public void onRelease(double mouseX, double mouseY) {}

    //set
    protected void onDrag(double mouseX, double mouseY, boolean on) {
        this.on=on;
        this.onPress.onPress(this.u,this.v,this.on);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            boolean flag = this.isMouseOver(mouseX, mouseY);
            if (flag) {
                this.playDownSound(Minecraft.getInstance().getSoundHandler());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int a = this.u;
        if (this.isValidClickButton(button)) {
            this.onDragged.onPress(mouseX,mouseY, this.on);
            return true;
        } else {
            return false;
        }
    }

    public boolean isHovered() {
        return this.isHovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean focus) {
        this.focused = !this.focused;
        //this.onFocusedChanged(this.focused);
        return this.focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + WIDTH) && mouseY < (double)(this.y + WIDTH);
    }


    public void playDownSound(SoundHandler handler) {
        handler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }



    public interface IPressable {
        void onPress(int x, int y, boolean on);
    }


    public interface IDraggable {
        void onPress(double mouseX, double mouseY, boolean on);
    }

}

