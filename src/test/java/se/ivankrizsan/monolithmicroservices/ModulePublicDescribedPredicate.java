package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import lombok.Getter;

import java.util.Optional;

/**
 * Described predicate that determines whether a class is located in a public part of a module.
 *
 * @author Ivan Krizsan
 */
@Getter
public class ModulePublicDescribedPredicate extends DescribedPredicate<JavaClass> {

    /**
     * Creates an instance of the described predicate with the supplied parameters.
     *
     * @param inParameters Optional parameters.
     */
    public ModulePublicDescribedPredicate(final Object... inParameters) {
        super("are located in a public part of a module", inParameters);
    }

    @Override
    public boolean test(final JavaClass inJavaClass) {
        final Optional<String> theModuleNameOptional = ArchUnitModuleUtils.moduleFromJavaClass(inJavaClass);
        final boolean theModuleIsPublicFlag = ArchUnitModuleUtils.isLocatedInModulePublic(inJavaClass);

        return theModuleIsPublicFlag && theModuleNameOptional.isPresent();
    }
}
