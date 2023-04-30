/*
 * This file was "inspired" (stolen) from https://github.com/IrisShaders/Iris
 * It is licensed under the LGPL-3.0 license.
 * https://github.com/IrisShaders/Iris/blob/1.19.4/LICENSE
 * Modified to add dependencies into the jar thingy
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.coolcrabs.brachyura.processing.ProcessingEntry;
import io.github.coolcrabs.brachyura.processing.ProcessingId;
import io.github.coolcrabs.brachyura.processing.ProcessingSink;
import io.github.coolcrabs.brachyura.processing.Processor;
import io.github.coolcrabs.brachyura.util.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipFile;

public class ResourceProcessor implements Processor {
	private final Buildscript parent;

	public ResourceProcessor(Buildscript parent) {
		this.parent = parent;
	}

	private final String[] ignoredDependencyPaths = new String[] {
			"com/sun/jna", /* fixes foxclient causing issues with simplevoicehat and possibly more due to discord-rpc bundling jna */
	};

	@Override
	public void process(Collection<ProcessingEntry> inputs, ProcessingSink sink) throws IOException {
		for (ProcessingEntry e : inputs) {
			if ("fabric.mod.json".equals(e.id.path)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
				JsonObject fabricModJson;
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(e.in.get(), StandardCharsets.UTF_8))) {
					fabricModJson = gson.fromJson(reader, JsonObject.class);
				}

				fabricModJson.addProperty("version", parent.getVersion());

				Collection<Path> jij = parent.getDependencyJars();
				for (Path jar : jij) {
					ZipFile zip = new ZipFile(jar.toFile());
					zip.stream()
							.filter(a -> Arrays.stream(ignoredDependencyPaths).parallel().noneMatch(a.getName()::contains))
							.forEach(entry -> {
						if (!entry.getName().startsWith("META-INF") && !entry.isDirectory()) {
							sink.sink(() -> {
								try {
									return zip.getInputStream(entry);
								} catch (IOException ex) {
									throw new RuntimeException(ex);
								}
							}, new ProcessingId(entry.getName(), e.id.source));
						}
					});
				}

				sink.sink(() -> GsonUtil.toIs(fabricModJson, gson), e.id);
			} else {
				sink.sink(e.in, e.id);
			}
		}
	}
}
