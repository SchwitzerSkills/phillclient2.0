// at/phillip/client/utils/TextSanitizer.java
package at.phillip.client.utils;

import net.minecraft.text.Text;
import java.util.regex.Pattern;

public final class TextSanitizer {
    // „mini8E00D“, „m89ABC“, sowie Token nach dem Datum (z.B. „08/25/25 L17D“)
    private static final Pattern GAME_ID =
            Pattern.compile("\\bmini[0-9A-F]{4,6}\\b|\\bm[0-9A-F]{5,6}\\b|(?<=\\d{2}/\\d{2}/\\d{2}\\s)[A-Z0-9]{3,6}\\b");

    public static String sanitizeRaw(String s) {
        String out = s;
        if (HideName.isEnabled()) {
            String me = HideName.myName();
            if (me != null && !me.isEmpty()) out = out.replace(me, "You");
        }
        return out;
    }

    public static Text sanitize(Text t) {
        if (t == null) return null;
        String s = t.getString();
        String out = sanitizeRaw(s);
        return out.equals(s) ? t : Text.literal(out);
    }
}
