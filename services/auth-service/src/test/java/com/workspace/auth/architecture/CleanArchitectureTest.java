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

    // ── Package constants — đúng với structure thực tế ──
    private static final String DOMAIN         = "..domain..";
    private static final String APPLICATION    = "..application..";
    private static final String INFRASTRUCTURE = "..infrastructure..";
    private static final String PRESENTATION   = "..presentation..";

    // ════════════════════════════════════════════
    // DOMAIN — tầng trong cùng, không biết ai cả
    // ════════════════════════════════════════════

    @ArchTest
    static final ArchRule domain_must_not_depend_on_application =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(APPLICATION)
                    .because("Domain là core business — không được biết application layer tồn tại");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                    .because("Domain không được biết JPA, database hay bất kỳ framework nào");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_presentation =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(PRESENTATION)
                    .because("Domain không được biết HTTP hay Controller tồn tại");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_spring =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                    .because("Domain phải là pure Java — không import Spring");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_jpa =
            noClasses().that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
                    .because("Domain không được biết JPA — @Entity chỉ xuất hiện ở infrastructure");

    // ════════════════════════════════════════════
    // APPLICATION — biết domain, không biết ra ngoài
    // ════════════════════════════════════════════

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                    .because("Use case không được import JPA repository hay JWT implementation");

    @ArchTest
    static final ArchRule application_must_not_depend_on_presentation =
            noClasses().that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage(PRESENTATION)
                    .because("Use case không được biết Controller hay HTTP tồn tại");

    @ArchTest
    static final ArchRule application_must_not_depend_on_jpa =
            noClasses().that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
                    .because("Use case chỉ dùng domain interface — không biết JPA");

    // ════════════════════════════════════════════
    // INFRASTRUCTURE — biết tất cả nhưng không được
    //                  phụ thuộc presentation
    // ════════════════════════════════════════════

    @ArchTest
    static final ArchRule infrastructure_must_not_depend_on_presentation =
            noClasses().that().resideInAPackage(INFRASTRUCTURE)
                    .should().dependOnClassesThat().resideInAPackage(PRESENTATION)
                    .because("JPA repository không được biết Controller tồn tại");

    // ════════════════════════════════════════════
    // PRESENTATION — biết application, không được
    //                bypass vào infrastructure
    // ════════════════════════════════════════════

    @ArchTest
    static final ArchRule presentation_must_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage(PRESENTATION)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                    .because("Controller chỉ được gọi Use Case — không gọi thẳng JPA repository");

    @ArchTest
    static final ArchRule presentation_must_not_depend_on_domain_repository =
            noClasses().that().resideInAPackage(PRESENTATION)
                    .should().dependOnClassesThat().resideInAPackage("..domain.repository..")
                    .because("Controller không được gọi thẳng domain repository — phải qua use case");
}