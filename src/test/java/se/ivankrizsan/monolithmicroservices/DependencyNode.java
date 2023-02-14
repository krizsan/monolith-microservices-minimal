package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.JavaClass;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class representing a dependency-relation node, that is either sources-ide, from which dependency originates,
 * or destination-side.
 * Dependency nodes are used to store additional information about classes of the application in which to
 * enforce architectural constraints using ArchUnit.
 *
 * @author Ivan Krizsan
 */
@Getter
@Setter
public class DependencyNode {
    /** Class being the source of the dependency. */
    @NonNull
    protected JavaClass javaClass;
    /** Module, if any, in which the above class is located in. */
    protected Optional<String> module;
    /** First-level subpackage, if any, that the above class is located in. */
    protected Optional<String> moduleSubpackage;
    /** Flag indicating that the above class is module-public. */
    protected boolean isModulePublic = false;
    /** Dependency destinations originating from the above class. */
    protected Set<DependencyNode> dependencies = new HashSet<>();

    /**
     * Creates a dependency node with the supplied Java class being the source of the dependency
     * and supplied information about which module, if any, the class is located in.
     *
     * @param inSourceJavaClass Class being the source of the dependency.
     * @param inSourceModule Source module, if any, the class is located in.
     */
    public DependencyNode(final JavaClass inSourceJavaClass, final Optional<String> inSourceModule) {
        javaClass = inSourceJavaClass;
        module = inSourceModule;
    }

    /**
     * Adds the supplied dependency node to the set of dependency destinations originating
     * from the class in this dependency node.
     *
     * @param inDependencyNode Destination dependency node.
     */
    public void addDependency(final DependencyNode inDependencyNode) {
        dependencies.add(inDependencyNode);
    }
}