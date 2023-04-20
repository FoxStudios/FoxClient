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
import io.github.coolcrabs.brachyura.util.Lazy;
import net.fabricmc.mappingio.tree.MappingTree;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class Buildscript extends SimpleFabricProject {

	static final String MC_VERSION = "1.19.4";
	static final String FABRIC_YARN_MAPPINGS = "1.19.4+build.2";
	static final String FABRIC_LOADER = "0.14.19";
	static final String FABRIC_API_VERSION = ".fabric-api:fabric-api:0.78.0+1.19.4";
	static final String FOXCLIENT_VERSION = "1.0.0-alpha";
	@Override
	public VersionMeta createMcVersion() {
		return Minecraft.getVersion(MC_VERSION);
	}

	@Override
	public int getJavaVersion() {
		return 17;
	}

	@Override
	public MappingTree createMappings() {
		return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn(FABRIC_YARN_MAPPINGS)).tree;
	}

	@Override
	public FabricLoader getLoader() {
		return new FabricLoader(FabricMaven.URL, FabricMaven.loader(FABRIC_LOADER));
	}
		String commitHash = "";
	{
		Git git = null;
		try {
			git = Git.open(getProjectDir().toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			commitHash = git.getRepository().parseCommit(git.getRepository().resolve(Constants.HEAD).toObjectId()).getName().substring(0, 8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		git.close();
	}

	@Override
	public Path getBuildJarPath() {
		return getBuildLibsDir().resolve(getModId() + "-" + FOXCLIENT_VERSION  + "-mc-" + MC_VERSION + "#" + commitHash + ".jar");
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

		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + FABRIC_API_VERSION), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		d.addMaven("https://jitpack.io", new MavenId("com.github.Vatuu:discord-rpc:1.6.2"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("org.java-websocket:Java-WebSocket:1.5.3"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven("https://maven.foxes4life.net", new MavenId("net.foxes4life:konfig:1.5.0"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		jij(d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + FABRIC_API_VERSION), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		jij(d.addMaven("https://maven.terraformersmc.com/releases/", new MavenId("com.terraformersmc:modmenu:6.2.0"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
	}
}