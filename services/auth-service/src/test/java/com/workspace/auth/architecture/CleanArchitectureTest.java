package com.workspace.auth.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.workspace.auth",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class CleanArchitectureTest {

    // Layer 1 (Entities) — phải pure, không phụ thuộc gì cả
    @ArchTest
    static final ArchRule entities_should_not_depend_on_usecases =
            noClasses().that().resideInAPackage("..entities..")
                    .should().dependOnClassesThat().resideInAPackage("..usecases..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..entities..")
                    .should().dependOnClassesThat().resideInAPackage("..adapters..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_frameworks =
            noClasses().that().resideInAPackage("..entities..")
                    .should().dependOnClassesThat().resideInAPackage("..frameworks..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_spring =
            noClasses().that().resideInAPackage("..entities..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_jpa =
            noClasses().that().resideInAPackage("..entities..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    // Layer 2 (Use Cases) — chỉ được phụ thuộc Entities
    @ArchTest
    static final ArchRule usecases_should_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..usecases..")
                    .should().dependOnClassesThat().resideInAPackage("..adapters..");

    @ArchTest
    static final ArchRule usecases_should_not_depend_on_frameworks =
            noClasses().that().resideInAPackage("..usecases..")
                    .should().dependOnClassesThat().resideInAPackage("..frameworks..");

    @ArchTest
    static final ArchRule usecases_should_not_depend_on_jpa =
            noClasses().that().resideInAPackage("..usecases..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    // Layer 3 (Adapters) — không được phụ thuộc Frameworks (config)
    // (Adapter dùng Spring/JPA là OK vì đó là chi tiết kỹ thuật của adapter)
    @ArchTest
    static final ArchRule adapters_should_not_depend_on_frameworks =
            noClasses().that().resideInAPackage("..adapters..")
                    .should().dependOnClassesThat().resideInAPackage("..frameworks..");
}