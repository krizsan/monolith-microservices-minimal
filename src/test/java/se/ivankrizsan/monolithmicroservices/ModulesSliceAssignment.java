package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;

import java.util.Optional;

/**
 * A {@code SliceAssignment} which maps non-public classes in modules to ArchUnit slices.
 *
 * @author Ivan Krizsan
 */
public class ModulesSliceAssignment implements SliceAssignment {
    @Override
    public String getDescription() {
        return "non-public parts of modules";
    }

    @Override
    public SliceIdentifier getIdentifierOf(final JavaClass inJavaClass) {
        /* Does the supplied class belong to a module? */
        final Optional<String> theModuleNameOptional = ArchUnitModuleUtils.moduleFromJavaClass(inJavaClass);
        if (theModuleNameOptional.isPresent()) {
            /*
             * Does the supplied class belong to a package in the module to which access
             * from anywhere is allowed?
             */
            if (ArchUnitModuleUtils.isLocatedInModulePublic(inJavaClass)) {
                /*
                 * Supplied class is located in a package in a module to which access from anywhere
                 * is allowed and public parts of modules are not to be included in slices so this class
                 * should not belong to any slice.
                 */
                return SliceIdentifier.ignore();
            } else {
                /*
                 * Supplied package is located in a module and access to the class should not be
                 * allowed from other modules.
                 */
                final String theModuleName = theModuleNameOptional.get();
                return SliceIdentifier.of(theModuleName);
            }
        }

        /* Ignore all classes that does not belong to the application. */
        return SliceIdentifier.ignore();
    }
}
