package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
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

    /**
     * Lists the classes of the application, including test-classes, and the slice to which
     * each class belongs to, if any.
     */
    @Test
    public void listClassSliceTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .importPackages(APPLICATION_ROOT_PACKAGE);

        /* Maps Java classes to slices specifying which classes belong to a slice. */
        final SliceAssignment theClassToModulesSliceMapper = new ModulesSliceAssignment();

        for (JavaClass theJavaClass : theClassesToCheck) {
            final SliceIdentifier theJavaClassSlice = theClassToModulesSliceMapper.getIdentifierOf(theJavaClass);
            final String theJavaClassName = String.format("%-120s", theJavaClass.getName());
            log.info("{} - {}", theJavaClassName, theJavaClassSlice.toString());
        }
    }

    /**
     * Ensures that there is no access to non-public parts of modules from code in other
     * modules.
     * TODO Want to ensure that there is no access to non-public parts of modules from
     * code that does not belong to a module.
     * TODO Want to ensure that there is no access to non-public parts of other modules
     * from code that belongs to the api or configuration packages of one module.
     */
    @Test
    public void noAccessToModuleNonPublicCodeTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        /* Maps Java classes to slices specifying which classes belong to a slice. */
        final SliceAssignment theClassToModulesSliceMapper = new ModulesSliceAssignment();

        SlicesRuleDefinition.slices()
            .assignedFrom(theClassToModulesSliceMapper)
            .should()
            .notDependOnEachOther()
            .because("this violates module encapsulation")
            .check(theClassesToCheck);
    }
}
