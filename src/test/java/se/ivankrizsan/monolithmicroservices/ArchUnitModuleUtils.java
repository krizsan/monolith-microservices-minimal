package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.JavaClass;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing utility methods related to modules.
 *
 * @author Ivan Krizsan
 */
public final class ArchUnitModuleUtils {
    /* Constant(s): */
    /** Package in which module packages are located. */
    public static final String MODULES_ROOT_PACKAGE_NAME = "modules";
    /** Regular expression used to find names of modules given a package name. */
    public static final String MODULE_NAME_REGEXP =
        "\\." + MODULES_ROOT_PACKAGE_NAME + "\\.([a-z0-9_]*)";
    /** Regular expression used to find names of first-level subpackages in modules given a package name. */
    public static final String MODULE_NAME_WITH_SUBPACKAGES_REGEXP =
        MODULE_NAME_REGEXP + "\\.([a-z0-9_]*)";
    public static final Pattern MODULE_NAME_REGEXP_PATTERN = Pattern.compile(MODULE_NAME_REGEXP,
        Pattern.CASE_INSENSITIVE);
    public static final Pattern MODULE_NAME_WITH_SUBPACKAGES_REGEXP_PATTERN =
        Pattern.compile(MODULE_NAME_WITH_SUBPACKAGES_REGEXP, Pattern.CASE_INSENSITIVE);
    /** Group in which module name can be found when there is a regexp match for a package name. */
    public static final int MODULE_NAME_REGEXP_GROUP_INDEX = 1;
    /**
     * Group in which module first-level subpackage name can be found when there is a regexp
     * match for a package name.
     */
    public static final int MODULE_SUBPACKAGE_REGEXP_GROUP_INDEX = 2;
    /** Name of first-level subpackages in modules that may be accessed from anywhere. */
    public static final List<String> MODULE_PUBLIC_PACKAGES = Arrays.asList("api", "configuration");

    /**
     * Determines whether the supplied Java class belongs to a module and, if so,
     * finds the name of the module the class belongs to.
     *
     * @param inJavaClass Java class which to determine whether it belongs to a module.
     * @return Optional containing name of module or empty if the class does not belong to a module.
     */
    public static Optional<String> moduleFromJavaClass(final JavaClass inJavaClass) {
        final String theJavaClassPackageName = inJavaClass.getPackageName();
        final Matcher theModuleNameMatcher = MODULE_NAME_REGEXP_PATTERN.matcher(theJavaClassPackageName);
        final boolean theModuleNameFoundFlag = theModuleNameMatcher.find();
        if (theModuleNameFoundFlag) {
            final String theModuleName = theModuleNameMatcher.group(MODULE_NAME_REGEXP_GROUP_INDEX);
            return Optional.of(theModuleName);
        }

        return Optional.empty();
    }

    /**
     * Determines whether the supplied Java class belongs to a module and, if so, finds the name
     * of the package in the module immediately below the module package that the class belongs to.
     * Example: The class se.ivankrizsan.modules.modone.api.ServiceInterface belongs to the module "modone"
     * and the module subpackage is "api".
     *
     * @param inJavaClass Java class which to determine whether it belongs to a module subpackage.
     * @return Optional containing name of module subpackage or empty if the class is not located
     * in a module subpackage.
     */
    public static Optional<String> moduleSubpackageFromJavaClass(final JavaClass inJavaClass) {
        final String theJavaClassPackageName = inJavaClass.getPackageName();
        final Matcher theModuleSubpackageMatcher = MODULE_NAME_WITH_SUBPACKAGES_REGEXP_PATTERN
            .matcher(theJavaClassPackageName);
        final boolean theModuleSubpackageNameFoundFlag = theModuleSubpackageMatcher.find();
        if (theModuleSubpackageNameFoundFlag) {
            final String theModuleSubpackageName = theModuleSubpackageMatcher
                .group(MODULE_SUBPACKAGE_REGEXP_GROUP_INDEX).toLowerCase();
            return Optional.of(theModuleSubpackageName);
        }

        return Optional.empty();
    }

    /**
     * Determines whether supplied class is located in a public package of a module.
     *
     * @param inJavaClass Java class which to determine whether it belongs to a module public package.
     * @return True if class belongs to a module public package, false otherwise.
     */
    public static boolean isLocatedInModulePublic(final JavaClass inJavaClass) {
        boolean theInModulePublicFlag = false;

        final Optional<String> theModuleSubpackageOptional = moduleSubpackageFromJavaClass(inJavaClass);
        if (theModuleSubpackageOptional.isPresent()) {
            final String theModuleSubpackage = theModuleSubpackageOptional.get();
            theInModulePublicFlag = MODULE_PUBLIC_PACKAGES.contains(theModuleSubpackage);
        }

        return theInModulePublicFlag;
    }
}
