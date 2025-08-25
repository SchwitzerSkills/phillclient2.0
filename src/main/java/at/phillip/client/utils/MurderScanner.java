package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents; // Yarn 1.21.x
import net.minecraft.text.Text;

import java.util.*;

public final class MurderScanner {
    private static boolean active = false;
    private static int requiredCount = 1;

    private static final Set<UUID> found  = new HashSet<>();
    private static final Set<UUID> marked = new HashSet<>();

    // --- NEAR feature state ---
    private static boolean nearEnabled = false;
    private static double nearRadius = 8.0; // meters/blocks
    private static final long NEAR_COOLDOWN_MS = 2500;
    private static final Map<UUID, Long> lastNearAlert = new HashMap<>();
    private static int nearTickStride = 5; // check every 5 ticks
    private static int tickCounter = 0;

    private static final Set<Item> SCAN_ITEMS = new LinkedHashSet<>(List.of(
            Items.IRON_SWORD, Items.CHEST, Items.ENDER_CHEST, Items.STONE_SWORD, Items.IRON_SHOVEL, Items.STICK, Items.WOODEN_AXE,
            Items.WOODEN_SWORD, Items.DEAD_BUSH, Items.SUGAR_CANE, Items.STONE_SHOVEL, Items.BLAZE_ROD, Items.DIAMOND_SHOVEL, Items.QUARTZ,
            Items.PUMPKIN_PIE, Items.GOLDEN_PICKAXE, Items.LEATHER, Items.NAME_TAG, Items.CHARCOAL, Items.FLINT, Items.BONE,
            Items.GOLDEN_CARROT, Items.COOKIE, Items.DIAMOND_AXE, Items.ROSE_BUSH, Items.PRISMARINE_SHARD, Items.COOKED_BEEF, Items.NETHER_BRICK,
            Items.COOKED_CHICKEN, Items.MUSIC_DISC_BLOCKS, Items.GOLDEN_HOE, Items.LAPIS_LAZULI, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.DIAMOND_HOE,
            Items.SHEARS, Items.SALMON, Items.RED_DYE, Items.BREAD, Items.OAK_BOAT, Items.GLISTERING_MELON_SLICE, Items.BOOK,
            Items.JUNGLE_SAPLING, Items.GOLDEN_AXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_SHOVEL
    ));

    private MurderScanner() {}

    public static boolean isActive() { return active; }
    public static boolean isMarked(UUID id) { return marked.contains(id); }

    public static void start(int amount) {
        requiredCount = Math.max(1, amount);
        found.clear();
        active = true;
    }

    public static void stop() {
        found.clear();
        marked.clear();
        active = false;
        lastNearAlert.clear();
    }

    public static void nearToggle(double radius) {
        if (nearEnabled) {
            nearEnabled = false;
            lastNearAlert.clear();
        } else {
            nearEnabled = true;
            nearRadius = Math.max(1.0, Math.min(64.0, radius));
            lastNearAlert.clear();
        }
    }
    public static boolean isNearEnabled() { return nearEnabled; }

    public static void onClientTick() {
        if (active) {
            var mc = MinecraftClient.getInstance();
            if (mc != null && mc.world != null && mc.player != null) {
                ClientPlayerEntity me = mc.player;

                for (PlayerEntity p : mc.world.getPlayers()) {
                    if (p == null || p == me) continue;
                    UUID id = p.getUuid();
                    if (found.contains(id)) continue;

                    var stack = p.getMainHandStack();
                    if (stack.isEmpty() || !SCAN_ITEMS.contains(stack.getItem())) continue;

                    found.add(id);
                    marked.add(id);

                    me.sendMessage(Text.of("§cPlayer §4" + p.getName().getString()
                            + " §cis Murderer (§7Item: " + stack.getName().getString() + "§c)."), false);

                    if (found.size() >= requiredCount) {
                        active = false;
                        me.sendMessage(Text.of("§aAll §2" + requiredCount
                                + " §aMurderers detected. §8(Scan stopped; Glow persists until §6#murder stop§8 or server switch)"), false);
                        break;
                    }
                }
            }
        }
        onClientTickNear();
    }

    private static void onClientTickNear() {
        if (!nearEnabled) return;

        tickCounter++;
        if ((tickCounter % nearTickStride) != 0) return;

        var mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null || mc.player == null) return;

        ClientPlayerEntity me = mc.player;
        double r2 = nearRadius * nearRadius;
        long now = System.currentTimeMillis();

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == null || p == me) continue;
            UUID id = p.getUuid();

            boolean likelyMurderer = marked.contains(id);
            if (!likelyMurderer) {
                var stack = p.getMainHandStack();
                likelyMurderer = !stack.isEmpty() && SCAN_ITEMS.contains(stack.getItem());
            }
            if (!likelyMurderer) continue;

            double dist2 = me.squaredDistanceTo(p);
            if (dist2 > r2) continue;

            Long last = lastNearAlert.get(id);
            if (last != null && (now - last) < NEAR_COOLDOWN_MS) continue;
            lastNearAlert.put(id, now);

            double dist = Math.sqrt(dist2);
            String name = p.getName().getString();

            me.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.05F);

            me.sendMessage(Text.of("§c⚠ Murder near: §4" + name + " §7(" + String.format("%.1f", dist) + "m)"), true); // true=Actionbar
        }
    }
}
