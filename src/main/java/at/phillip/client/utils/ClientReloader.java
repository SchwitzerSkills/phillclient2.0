package at.phillip.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipFile;

public class ClientReloader {
    private static URLClassLoader classLoader;
    private static Object entryInstance;

    private static void tell(String s) {
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null) mc.player.sendMessage(Text.of(s), false);
    }

    private static boolean isValidJar(Path path) {
        File f = path.toFile();
        if (!f.exists() || !f.isFile() || !f.canRead()) return false;
        try (ZipFile zf = new ZipFile(f)) { return true; } catch (Exception e) { return false; }
    }

    private static Path resolveJarPath() {
        String raw = System.getProperty("user.home")
                + "/AppData/Roaming/ModrinthApp/profiles/phillclient/mods/phillclient-1.0.0.jar";
        return Path.of(raw);
    }

    private static boolean isParentOwned(String name) {
        if (name.equals("at.phillip.client.utils.ICommand")) return true;
        if (name.equals("at.phillip.client.utils.CommandBus")) return true;
        if (name.equals("at.phillip.client.utils.TickBus")) return true;
        if (name.equals("at.phillip.client.utils.MurderScanner")) return true;
        if (name.equals("at.phillip.client.utils.EventBus")) return true;
        if (name.startsWith("at.phillip.client.utils.EventBus$")) return true;
        return false;
    }

    public static void loadClient() {
        try {
            Path jar = Objects.requireNonNull(resolveJarPath(), "jar path null");
            File jarFile = jar.toFile();

            if (!isValidJar(jar)) {
                tell("§cReload failed: invalid/corrupt JAR ⇒ " + jarFile.getAbsolutePath());
                return;
            }

            if (classLoader != null) {
                try { classLoader.close(); } catch (Exception ignored) {}
            }

            ClassLoader parent = ClientReloader.class.getClassLoader();
            classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, parent) {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    synchronized (getClassLoadingLock(name)) {
                        if (isParentOwned(name)) {
                            return parent.loadClass(name);
                        }
                        Class<?> c = findLoadedClass(name);
                        if (c == null) {
                            try { c = findClass(name); }
                            catch (ClassNotFoundException ignored) { c = parent.loadClass(name); }
                        }
                        if (resolve) resolveClass(c);
                        return c;
                    }
                }
            };

            Class<?> entryClass = classLoader.loadClass("at.phillip.client.Entry");
            entryInstance = entryClass.getDeclaredConstructor().newInstance();
            entryClass.getMethod("init").invoke(entryInstance);

            tell("§aClient reloaded!");
            System.out.println("[PhillClient] reloaded from " + jarFile.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
            tell("§cReload error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
