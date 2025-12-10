package com.inditex.site.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "com.inditex.site";

    private static final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

    @Test
    void domain_should_not_depend_on_any_other_layer() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..application..",
                        "..infrastructure..",
                        "..adapter..",
                        "org.springframework.."
                )
                .check(classes);
    }

    @Test
    void application_should_not_depend_on_infrastructure_or_adapters() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..infrastructure..",
                        "..adapter.."
                )
                .check(classes);
    }

    @Test
    void adapters_in_should_only_depend_on_application_and_domain() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..adapter.in..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "..adapter.in..",
                        "..application..",
                        "..domain..",
                        "java..",
                        "reactor..",
                        "org.springframework..",
                        "io.swagger..",
                        "jakarta.validation..",
                        "com.fasterxml.jackson..",
                        "lombok..",
                        "org.mapstruct.."
                )
                .check(classes);
    }

    @Test
    void adapters_out_should_only_depend_on_domain_and_external_tech() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..adapter.out..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "..adapter.out..",
                        "..domain..",
                        "..infrastructure..",
                        "java..",
                        "reactor..",
                        "org.springframework..",
                        "com.github.benmanes.caffeine..",
                        "io.github.resilience4j..",
                        "org.slf4j..",
                        "lombok..",
                        "org.mapstruct.."
                )
                .check(classes);
    }
}
