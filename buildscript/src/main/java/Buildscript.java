import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.SimpleFabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyCollector;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyFlag;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends SimpleFabricProject {

	@Override
	public VersionMeta createMcVersion() {
		return Minecraft.getVersion("1.19.4");
	}

	@Override
	public int getJavaVersion() {
		return 17;
	}

	@Override
	public MappingTree createMappings() {
		return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.19.4+build.2")).tree;
	}

	@Override
	public FabricLoader getLoader() {
		return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.14.19"));
	}

	@Override
	public void getModDependencies(ModDependencyCollector d) {
		String[][] fapiModules = new String[][] {
				{"fabric-api-base", "0.4.23+9ff28bcef4"},
				{"fabric-key-binding-api-v1", "1.0.32+c477957ef4"},
				{"fabric-lifecycle-events-v1", "2.2.14+5da15ca1f4"},
				{"fabric-resource-loader-v0", "0.11.1+1e1fb126f4"},
				{"fabric-screen-api-v1", "1.0.44+8c25edb4f4"},

		};
		for (String[] module : fapiModules) {
			d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module[0], module[1]), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		}

		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api:fabric-api:0.78.0+1.19.4"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		d.addMaven("https://jitpack.io", new MavenId("com.github.Vatuu:discord-rpc:1.6.2"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("org.java-websocket:Java-WebSocket:1.5.3"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven("https://maven.foxes4life.net", new MavenId("net.foxes4life:konfig:1.5.0"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		jij(d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api:fabric-api:0.78.0+1.19.4"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		jij(d.addMaven("https://maven.terraformersmc.com/releases/", new MavenId("com.terraformersmc:modmenu:6.2.0"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
	}
}