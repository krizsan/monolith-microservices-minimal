package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Function;

/**
 * Tests the application's adherence to the proposed structure which purpose
 * is to allow for easier refactoring into microservices.
 *
 * @author Ivan Krizsan
 */
@Slf4j
public class ArchUnitTests {
    public static final String APPLICATION_ROOT_PACKAGE = "se.ivankrizsan";

    /**
     * Lists the dependencies of the classes in this example application contained
     * in the specified root package.
     */
    @Test
    public void listClassDependenciesTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        for (JavaClass theJavaClass : theClassesToCheck) {
            final Set<Dependency> theClassDependencies =  theJavaClass.getDirectDependenciesFromSelf();
            log.info("Class {} has the following dependencies:", theJavaClass.getName());
            theClassDependencies
                .stream()
                .map((Function<Dependency, Object>) inClassDependency -> inClassDependency.getTargetClass().getName())
                .sorted()
                .distinct()
                .forEach(inClassDependency -> log.info("    {}", inClassDependency));
        }
    }
}
