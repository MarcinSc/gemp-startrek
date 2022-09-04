package com.gempukku.startrek.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gempukku.libgdx.graph.plugin.lighting3d.Lighting3DPluginRuntimeInitializer;
import com.gempukku.libgdx.graph.plugin.models.ModelsPluginRuntimeInitializer;
import com.gempukku.libgdx.graph.plugin.particles.ParticlesPluginRuntimeInitializer;
import com.gempukku.libgdx.graph.plugin.ui.UIPluginRuntimeInitializer;
import com.gempukku.startrek.StarTrekGameSceneProvider;
import com.gempukku.startrek.test.DualGameApplication;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
        StarTrekGameSceneProvider provider1 = new StarTrekGameSceneProvider();
        StarTrekGameSceneProvider provider2 = new StarTrekGameSceneProvider();
        return new Lwjgl3Application(new DualGameApplication(provider1, provider2), getDefaultConfiguration());
    }

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        UIPluginRuntimeInitializer.register();
        ModelsPluginRuntimeInitializer.register();
        Lighting3DPluginRuntimeInitializer.register();
        ParticlesPluginRuntimeInitializer.register();

        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Gemp-StarTrek");
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(640, 480);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		return configuration;
	}
}