package kr.doka.lab.discordsync;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class DiscordSyncPluginLoader implements PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());

        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-core:0.52.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-dao:0.52.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-jdbc:0.52.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.exposed:exposed-java-time:0.52.0"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:5.1.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mariadb.jdbc:mariadb-java-client:3.3.3"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("org.slf4j:slf4j-simple:2.0.16"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("io.javalin:javalin:6.7.0"), null));

        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.kittinunf.fuel:fuel:2.3.1"), null));

        classpathBuilder.addLibrary(resolver);
    }
}
