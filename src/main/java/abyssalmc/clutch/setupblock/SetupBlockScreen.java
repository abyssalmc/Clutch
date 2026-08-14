package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

// BLOCK GUI (rendering textures, slots, buttons, handles input)

public class SetupBlockScreen extends HandledScreen<SetupBlockScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(Clutch.MOD_ID, "textures/setupgui.png");

    private TextFieldWidget slotField;
    private TextFieldWidget offsetField;

    public SetupBlockScreen(SetupBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 222;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;


        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Paste Inventory"),
                button -> {
                    if (this.handler.getPos() != null) {
                        ClientPlayNetworking.send(new PasteInventoryPayload(this.handler.getPos()));
                    }
                }
        ).dimensions(x + 184, y + 16, 100, 18).build());

        this.slotField = new TextFieldWidget(
                this.textRenderer,
                x + 184, y + 54,
                100, 16,
                Text.literal("Input Field")
        );
        this.offsetField = new TextFieldWidget(
                this.textRenderer,
                x + 184, y + 92,
                100, 16,
                Text.literal("Input Field")
        );

        this.slotField.setMaxLength(1);
        this.slotField.setPlaceholder(Text.literal("§8<id>"));
        this.offsetField.setPlaceholder(Text.literal("§8<x> <y>"));

        this.slotField.setTextPredicate(text -> text.matches("[0-8]*"));
        this.offsetField.setTextPredicate(text -> text.matches("[0-9 ]*"));

        this.slotField.setText(this.handler.getSlotText());
        this.offsetField.setText(this.handler.getOffsetText());

        this.slotField.setChangedListener(text -> sendFieldsUpdate());
        this.offsetField.setChangedListener(text -> sendFieldsUpdate());


        this.addDrawableChild(this.slotField);
        this.addDrawableChild(this.offsetField);
    }

    private void sendFieldsUpdate() {
        if (this.handler.getPos() != null) {
            ClientPlayNetworking.send(new UpdateFieldsPayload(
                    this.handler.getPos(),
                    this.slotField.getText(),
                    this.offsetField.getText()
            ));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (this.getFocused() instanceof ButtonWidget) {
            this.setFocused(null);
        }
        if (!this.slotField.isMouseOver(mouseX, mouseY)) {
            this.slotField.setFocused(false);
        }
        if (!this.offsetField.isMouseOver(mouseX, mouseY)) {
            this.offsetField.setFocused(false);
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.slotField.isFocused() || this.offsetField.isFocused()) {
            if (this.slotField.keyPressed(keyCode, scanCode, modifiers) || this.offsetField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        context.drawText(this.textRenderer, Text.literal("Reset Inventory"), 8, 38, 0x404040, false);
        context.drawText(this.textRenderer, Text.literal("Reset Slot"), 184, 44, 0xAAAAAA, false);
        context.drawText(this.textRenderer, Text.literal("Cursor Offset"), 184, 82, 0xAAAAAA, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        super.drawMouseoverTooltip(context, x, y);

        if (this.focusedSlot instanceof SetupBlockScreenHandler.TrashSlot) {
            context.drawTooltip(
                    this.textRenderer,
                    Text.translatable("inventory.binSlot"),
                    x, y
            );
        }
    }
}