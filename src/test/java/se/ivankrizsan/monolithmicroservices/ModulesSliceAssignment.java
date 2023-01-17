package se.ivankrizsan.monolithmicroservices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@code SliceAssignment} which maps classes in modules to ArchUnit slices.
 *
 * @author Ivan Krizsan
 */
public class ModulesSliceAssignment implements SliceAssignment {
    /* Constant(s): */
    /** Package in which module packages are located. */
    public static final String MODULES_ROOT_PACKAGE_NAME = "modules";
    /** Regular expression used to find names of modules given a package name. */
    public static final String MODULE_NAME_REGEXP =
        "\\." + MODULES_ROOT_PACKAGE_NAME + "\\.([a-z0-9_]*)";
    /** Regular expression used to find names of firstl-level subpackages in modules given a package name. */
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
    /** Name of API first-level subpackage in module that should be accessible from anywhere. */
    public static final String MODULE_API_PACKAGE_NAME = "api";
    /** Name of configuration first-level subpackage in module that should be accessible from anywhere. */
    public static final String MODULE_CONFIGURATION_PACKAGE_NAME = "configuration";


    @Override
    public String getDescription() {
        return "non-public parts of modules";
    }

    @Override
    public SliceIdentifier getIdentifierOf(final JavaClass inJavaClass) {
        /* Does the supplied class belong to a module? */
        final String theJavaClassPackageName = inJavaClass.getPackageName();
        final Matcher theModuleNameMatcher = MODULE_NAME_REGEXP_PATTERN.matcher(theJavaClassPackageName);
        final boolean theModuleNameFoundFlag = theModuleNameMatcher.find();
        if (theModuleNameFoundFlag) {
            final String theModuleName = theModuleNameMatcher.group(MODULE_NAME_REGEXP_GROUP_INDEX);

            /*
             * Does the supplied class belong to a package in the module to which access
             * from anywhere is allowed?
             */
            final Matcher theModuleSubpackageMatcher = MODULE_NAME_WITH_SUBPACKAGES_REGEXP_PATTERN
                .matcher(theJavaClassPackageName);
            final boolean theModuleSubpackageNameFoundFlag = theModuleSubpackageMatcher.find();
            if (theModuleSubpackageNameFoundFlag) {
                final String theModuleSubpackageName = theModuleSubpackageMatcher
                    .group(MODULE_SUBPACKAGE_REGEXP_GROUP_INDEX);

                if (MODULE_API_PACKAGE_NAME.equalsIgnoreCase(theModuleSubpackageName)
                    || MODULE_CONFIGURATION_PACKAGE_NAME.equalsIgnoreCase(theModuleSubpackageName)) {
                    /*
                     * Supplied class is located in a package in a module to which access from anywhere
                     * is allowed and should thus not belong to any slice.
                     */
                    return SliceIdentifier.ignore();
                } else {
                    /*
                     * Supplied package is located in a module and access to the class should not be
                     * allowed from other modules.
                     */
                    return SliceIdentifier.of(theModuleName);
                }
            }
        }

        /* Ignore all classes that does not belong to the application. */
        return SliceIdentifier.ignore();
    }
}
