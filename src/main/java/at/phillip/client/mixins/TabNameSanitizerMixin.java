// at/phillip/client/mixins/TabNameSanitizerMixin.java
package at.phillip.client.mixins;

import at.phillip.client.utils.HideName;
import at.phillip.client.utils.TextSanitizer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class TabNameSanitizerMixin {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true, require = 0)
    private void phill$sanitizeTabName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!HideName.isEnabled()) return;
        var orig = cir.getReturnValue();
        cir.setReturnValue(TextSanitizer.sanitize(orig));
    }
}
