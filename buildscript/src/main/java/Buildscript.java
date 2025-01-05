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
import io.github.coolcrabs.brachyura.processing.ProcessorChain;
import net.fabricmc.mappingio.tree.MappingTree;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Buildscript extends SimpleFabricProject {

	static final String MC_VERSION         = "1.20.2";
	static final String FOXCLIENT_VERSION  = "0.2.1-dev";
	static final String YARN_MAPPINGS      = "1.20.2+build.4";
	static final String FABRIC_LOADER      = "0.14.24";
	static final String FABRIC_API_VERSION = "0.90.4+1.20.2";
	static final String MODMENU_VERSION    = "8.0.0";
	static final String KONFIG_VERSION     = "1.5.0";
	static final String JAVA_WS_VERSION    = "1.5.3";

	private final ArrayList<Path> dependencyJars = new ArrayList<>();

	public ArrayList<Path> getDependencyJars() {
		return dependencyJars;
	}

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
		return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn(YARN_MAPPINGS)).tree;
	}

	@Override
	public FabricLoader getLoader() {
		return new FabricLoader(FabricMaven.URL, FabricMaven.loader(FABRIC_LOADER));
	}

	@Override
	public String getVersion() {
		return FOXCLIENT_VERSION + "-" + MC_VERSION + "-rev." + getGitVersionInformation();
	}

	public String getGitVersionInformation() {
		Git git;

		String branch;
		String hash;
		boolean dirty;

		try {
			git = Git.open(getProjectDir().toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			hash = git.getRepository().parseCommit(git.getRepository().resolve(Constants.HEAD).toObjectId()).getName().substring(0, 8);
			branch = git.getRepository().getBranch();
			dirty = !git.status().call().isClean();
		} catch (IOException | GitAPIException e) {
			throw new RuntimeException(e);
		}
		git.close();

		return branch + "." + hash + (dirty ? "-dirty" : "");
	}

	@Override
	public Path getBuildJarPath() {
		return getBuildLibsDir().resolve(getModId() + "-" + getVersion() + ".jar");
	}

	private void bundle(JavaJarDependency d) {
		dependencyJars.add(d.jar);
	}

	@Override
	public ProcessorChain resourcesProcessingChain() {
		return new ProcessorChain(super.resourcesProcessingChain(), new ResourceProcessor(this));
	}

	@Override
	public void getModDependencies(ModDependencyCollector d) {
		String[] fapiModules = new String[] {
				"fabric-api-base", "fabric-key-binding-api-v1", "fabric-lifecycle-events-v1", "fabric-resource-loader-v0", "fabric-screen-api-v1"
		};

		HashMap<String, String> foundFapiModules = new HashMap<>();
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/" + FABRIC_API_VERSION + "/fabric-api-" + FABRIC_API_VERSION + ".pom"))
					.header("User-Agent", "brachyuri/0.0.0 (FoxClient Build Script)")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// what the fuck is an XML parser RAHHHHH
			String body = response.body();
			body = body.replace("<", "$$$").replace(">", "$$$").replace("/", "")
					.replace("$$$dependency$$$", "").replace("\r", "").replace("\n", "")
					.replace(" ", "")
					.replace("\t", "");
			body = body.substring(body.indexOf("$$$dependencies$$$") + 18);

			String[] items = body.split("\\$\\$\\$\\$\\$\\$");
			String artifactId = "";
			String version = "";

			for (String item : items) {
				String type = item.substring(item.lastIndexOf("$$$") + 3);
				System.out.println(type + " " + item);
				item = item.replace("$$$"+type, "").replace(type+"$$$", "");

				if (type.equals("artifactId")) {
					artifactId = item;
				}
				if (type.equals("version")) {
					version = item;
				}
				// scope is always the last entries per dependency. hopefully.
				if (type.equals("scope")) {
					foundFapiModules.put(artifactId, version);
				}
			}
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}

		for (String module : fapiModules) {
			if (!foundFapiModules.containsKey(module)) {
				throw new RuntimeException("could not find fapi module" + module);
			}
			String moduleVer = foundFapiModules.get(module);
			System.out.println("--> " + module + ": " + moduleVer);

			d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module, moduleVer), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		}

		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api:fabric-api:" + FABRIC_API_VERSION), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		// the version for discord-rpc is hardcoded because it's not getting updated anymore anyway
		bundle(d.addMaven("https://jitpack.io", new MavenId("com.github.Vatuu:discord-rpc:1.6.2"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE));
		bundle(d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("org.java-websocket:Java-WebSocket:" + JAVA_WS_VERSION), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE));
		bundle(d.addMaven("https://maven.foxes4life.net", new MavenId("net.foxes4life:konfig:" + KONFIG_VERSION), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE));

		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api:fabric-api:" + FABRIC_API_VERSION), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME);
		d.addMaven("https://maven.terraformersmc.com/releases/", new MavenId("com.terraformersmc:modmenu:" + MODMENU_VERSION), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME);
	}
}