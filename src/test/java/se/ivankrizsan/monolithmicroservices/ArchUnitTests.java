package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.PackageMatcher;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

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
            final Set<Dependency> theClassDependencies = theJavaClass.getDirectDependenciesFromSelf();
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
     * each class belongs,if any, to having applied the {@code ModulesSliceAssignment} to group the
     * application classes into slices.
     */
    @Test
    public void listClassSliceTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .importPackages(APPLICATION_ROOT_PACKAGE);

        /* Maps Java classes to slices specifying which classes belong to a slice. */
        final SliceAssignment theClassToModulesSliceMapper = new ModulesSliceAssignment();

        /* List the slices and the classes belonging to each slice. */
        for (JavaClass theJavaClass : theClassesToCheck) {
            final SliceIdentifier theJavaClassSlice = theClassToModulesSliceMapper.getIdentifierOf(theJavaClass);
            final String theJavaClassName = String.format("%-120s", theJavaClass.getName());
            log.info("{} - {}", theJavaClassName, theJavaClassSlice.toString());
        }
    }

    /**
     * Ensures that there is no access between non-public parts of modules.
     * Each ArchUnit slice consists of the non-public part of each module.
     * There should be no dependencies between slices.
     */
    @Test
    public void accessRuleOneTest() {
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

    /**
     * Ensures that:
     * <li>Application code not belonging to any module may not have dependencies to non-public parts of modules.</li>
     * <li>Code in modules are not allowed to have dependencies to application code not belonging to any module.</li>
     * <p>
     * The application code is divided in three layers:
     * <li>Public Module - Parts of modules that may be accessed from other modules and external, non-module, code.</li>
     * <li>Non-public Module - Parts of modules that may not be accessed from anywhere but the public part of the same module.</li>
     * <li>Non-module - Code outside of modules</li>
     * <p>
     * <br/><b>Note!</b> No control of access between non-public parts of modules is performed - this is verified in the
     * {@code noAccessBetweenNonPublicPartsOfModulesTest}.<br/>
     * In addition, no control is made that all dependencies from public parts of modules to non-public parts
     * of modules are within one and the same module. That is a dependency from the public part of a module
     * to the non-public part of a module is not allowed to cross module boundaries.
     */
    @Test
    public void accessRulesTwoAndThreeTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        Architectures
            .layeredArchitecture()
            .consideringAllDependencies()
            .layer("PublicModule").definedBy(createModulesPublicClassesPredicate())
            .layer("NonPublicModule").definedBy(createModulesNonPublicClassesPredicate())
            .layer("NonModule").definedBy(createNonModulesClassesPredicate())
            .whereLayer("PublicModule").mayOnlyBeAccessedByLayers("NonModule", "NonPublicModule")
            .whereLayer("NonPublicModule").mayOnlyBeAccessedByLayers("PublicModule")
            .whereLayer("NonModule").mayNotBeAccessedByAnyLayer()
            .check(theClassesToCheck);
    }

    /**
     * Ensures that no there are no dependencies from classes located in public part of modules
     * to classes located in non-public parts of other modules.
     * This test is a complement to the {@code accessRulesTwoAndThreeTest} as far as access rule three
     * is concerned.
     */
    @Test
    public void accessRuleThreeTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        final ModulePublicDescribedPredicate resideInModulePublicPart = new ModulePublicDescribedPredicate();
        final ArchCondition<JavaClass> dependOnClassesInAnotherModulesNonPublicPart =
            new HasDependencyToOtherModulesNonPublicArchCondition(
                "has dependency to class in other module's non-public part");

        ArchRuleDefinition
            .noClasses()
            .that(resideInModulePublicPart)
            .should(dependOnClassesInAnotherModulesNonPublicPart)
            .check(theClassesToCheck);
    }

    /**
     * Lists all the classes in the application that are considered non-public module classes.
     * Such classes are located in modules but not in packages which are designated to contain
     * classes that may be accessed from outside the module, i.e. the 'api' and 'configuration' packages.
     */
    @Test
    public void nonPublicModuleClassesTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        final Predicate<JavaClass> theModuleNonPublicPredicate = createModulesNonPublicClassesPredicate();

        final Set<JavaClass> theNonPublicModuleClasses = new HashSet<>();
        for (JavaClass theJavaClass : theClassesToCheck) {
            if (theModuleNonPublicPredicate.test(theJavaClass)) {
                theNonPublicModuleClasses.add(theJavaClass);
            }
        }

        log.info("Non-public classes in modules: ");
        for (JavaClass theModuleClass : theNonPublicModuleClasses) {
            log.info("   {}", theModuleClass);
        }
    }

    /**
     * Lists all the classes in the application that do not belong to a module.
     */
    @Test
    public void listNonModuleClassesTest() {
        final JavaClasses theClassesToCheck = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(APPLICATION_ROOT_PACKAGE);

        final Predicate<JavaClass> theModuleNonPublicPredicate = createNonModulesClassesPredicate();

        final Set<JavaClass> theNonPublicModuleClasses = new HashSet<>();
        for (JavaClass theJavaClass : theClassesToCheck) {
            if (theModuleNonPublicPredicate.test(theJavaClass)) {
                theNonPublicModuleClasses.add(theJavaClass);
            }
        }

        log.info("Classes not belonging to any module: ");
        for (JavaClass theModuleClass : theNonPublicModuleClasses) {
            log.info("   {}", theModuleClass);
        }
    }

    /**
     * Tests a package-matching expression against a package.
     * Will log the result of the attempted matching and always succeed.
     * Can be used to verify package-matching expressions.
     */
    @Test
    public void packageMatchingExpressionTest() {
        final String thePackage = "se.ivankrizsan.monolithmicroservices.modules.shoppingcart.api";
        final String thePackageMatchingExpression = "..modules.(*).[api|configuration]..";

        final PackageMatcher thePackageMatcher = PackageMatcher.of(thePackageMatchingExpression);
        final boolean thePackageMatchedFlag = thePackageMatcher.matches(thePackage);
        log.info("Attempted matching of package '{}' with the expression '{}' rendered the result: {}",
            thePackage, thePackageMatchingExpression, thePackageMatchedFlag);
    }

    /**
     * Creates an ArchUnit predicate that will select classes that:
     * - Are located in a package at least one level below the 'modules' package.
     * That is, are located in a module.
     * - Are not located in a package named 'api' or 'configuration' in a module.
     *
     * @return Predicate that will select non-public module classes.
     */
    private DescribedPredicate<JavaClass> createModulesNonPublicClassesPredicate() {
        final DescribedPredicate<JavaClass> theResideInModulePredicate =
            JavaClass.Predicates.resideInAPackage("..modules.(*)..");
        final DescribedPredicate<JavaClass> theModulePublicPredicate =
            JavaClass.Predicates.resideInAPackage("..modules.(*).[api|configuration]..");
        final DescribedPredicate<JavaClass> theNotModulePublicPredicate =
            DescribedPredicate.not(theModulePublicPredicate);
        return theResideInModulePredicate.and(theNotModulePublicPredicate);
    }

    /**
     * Creates an ArchUnit predicate that will select classes that does not belong to any module.
     *
     * @return Predicate that will select non-module classes.
     */
    private DescribedPredicate<JavaClass> createNonModulesClassesPredicate() {
        final DescribedPredicate<JavaClass> theResideInModulePredicate =
            JavaClass.Predicates.resideInAPackage("..modules.(*)..");
        return DescribedPredicate.not(theResideInModulePredicate);
    }

    /**
     * Creates an ArchUnit predicate that will select public classes in modules.
     *
     * @return Predicate that will select public module classes.
     */
    private DescribedPredicate<JavaClass> createModulesPublicClassesPredicate() {
        return JavaClass.Predicates.resideInAPackage("..modules.(*).[api|configuration]..");
    }
}
