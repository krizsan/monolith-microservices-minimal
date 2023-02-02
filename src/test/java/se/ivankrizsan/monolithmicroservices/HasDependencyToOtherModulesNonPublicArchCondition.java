package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * An ArchUnit condition that checks whether classes have dependencies to non-public parts
 * in other modules.
 *
 * @author Ivan Krizsan
 */
@Slf4j
public class HasDependencyToOtherModulesNonPublicArchCondition extends ArchCondition<JavaClass> {

    /**
     * Creates an instance of the arch condition with the supplied additional parameters.
     *
     * @param inParameters Additional parameters.
     */
    public HasDependencyToOtherModulesNonPublicArchCondition(final Object... inParameters) {
        super("have dependencies to non-public classes in other modules", inParameters);
    }

    @Override
    public void check(final JavaClass inJavaClassToCheck, final ConditionEvents inConditionEvents) {
        log.debug("Checking Java class: {}", inJavaClassToCheck.getName());

        final Optional<String> theSourceModuleOptional = ArchUnitModuleUtils.moduleFromJavaClass(inJavaClassToCheck);
        if (theSourceModuleOptional.isPresent()) {
            final String theSourceModule = theSourceModuleOptional.get();

            for (Dependency theDestinationDependency : inJavaClassToCheck.getDirectDependenciesFromSelf()) {
                final JavaClass theDestinationClass = theDestinationDependency.getTargetClass();
                final boolean theDestinationModuleIsPublic = ArchUnitModuleUtils.isLocatedInModulePublic(theDestinationClass);
                final Optional<String> theDestinationModuleOptional = ArchUnitModuleUtils.moduleFromJavaClass(theDestinationClass);

                if (!theDestinationModuleIsPublic && theDestinationModuleOptional.isPresent()) {
                    final String theDestinationModule = theDestinationModuleOptional.get();
                    if (!theSourceModule.equalsIgnoreCase(theDestinationModule)) {
                        final String theViolationMessage = String.format(
                            "The class %s in the module '%s' has a dependency to the class %s in the module '%s', "
                                + " which is a non-public class in another module.",
                            inJavaClassToCheck.getName(),
                            theSourceModule,
                            theDestinationClass.getName(),
                            theDestinationModule);
                        inConditionEvents.add(SimpleConditionEvent.satisfied(inJavaClassToCheck, theViolationMessage));
                    }
                }
            }
        } else {
            log.warn("Java class '{}' is not located in a module", inJavaClassToCheck);
        }
    }
}
