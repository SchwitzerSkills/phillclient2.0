package at.phillip.client.mixins;

import at.phillip.client.utils.MurderScanner;
import at.phillip.client.utils.PlayerESP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void phillclient$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof PlayerEntity) {
            UUID id = self.getUuid();
            if (MurderScanner.isMarked(id)) {
                cir.setReturnValue(true);
                return;
            }

            if (PlayerESP.shouldGlow(self)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void phillclient$customGlowColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity)(Object)this;
        if (!(self instanceof PlayerEntity)) return;

        // 1) Murder → ROT
        if (at.phillip.client.utils.MurderScanner.isMarked(self.getUuid())) {
            cir.setReturnValue(0xFF0000); // RGB (kein Alpha)
            return;
        }

        // 2) PlayerESP → BLAU
        if (at.phillip.client.utils.PlayerESP.shouldGlow(self)) {
            cir.setReturnValue(0x3A66FF); // angenehmes Blau
            return;
        }

        // 3) sonst Vanilla-Teamfarbe beibehalten (nicht canceln)
    }
}
