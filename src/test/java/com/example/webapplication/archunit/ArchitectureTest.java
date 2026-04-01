package com.example.webapplication.archunit;

import com.example.webapplication.bootstrap.UserDataLoader;
import com.example.webapplication.security.jwt.JwtService;
import com.example.webapplication.service.UserProfileFacade;
import com.example.webapplication.service.UserService;
import com.example.webapplication.service.impl.UserServiceImpl;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private static final String BASE_PACKAGE = "com.example.webapplication";
    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("Layered architecture for main code")
    void layeredArchitecture_mainOnly() {
        layeredArchitecture()
                .consideringAllDependencies()
                // Ignore framework and JDK dependencies; only project-internal architecture is validated here.
                .ignoreDependency(
                        alwaysTrue(),
                        resideInAnyPackage("java..", "javax..", "jakarta..", "org..", "io..", "lombok..", "..dto..", "..entities..", "..exception..")
                )
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repositories..")
                .layer("Security").definedBy("..security..")
                .layer("Bootstrap").definedBy("..bootstrap..")
                .layer("Config").definedBy("..config..")
                .layer("InfrastructureProperties").definedBy("..infrastructure.properties..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Controller").mayOnlyAccessLayers("Service", "Security", "InfrastructureProperties")
                .whereLayer("Service").mayOnlyAccessLayers("Service", "Repository", "InfrastructureProperties")
                .whereLayer("Repository").mayNotAccessAnyLayer()
                .whereLayer("Security").mayOnlyAccessLayers("Service", "Repository", "InfrastructureProperties")
                .whereLayer("Bootstrap").mayOnlyAccessLayers("Service", "Security")
                .whereLayer("Config").mayOnlyAccessLayers("Security", "InfrastructureProperties")
                .whereLayer("InfrastructureProperties").mayNotAccessAnyLayer()
                .check(classes);
    }

    @Test
    @DisplayName("PasswordEncoder usage is restricted to approved packages")
    void passwordEncoderIsRestrictedToApprovedPackages() {
        noClasses()
                .that().resideOutsideOfPackages(
                        "..controller..",
                        "..service.impl..",
                        "..bootstrap..",
                        "..config.security.."
                )
                .should().dependOnClassesThat().haveFullyQualifiedName(PasswordEncoder.class.getName())
                .check(classes);
    }

    @Test
    @DisplayName("API controllers must not depend on PasswordEncoder")
    void apiControllersMustNotUsePasswordEncoder() {
        noClasses()
                .that().resideInAPackage("..controller.api..")
                .should().dependOnClassesThat().haveFullyQualifiedName(PasswordEncoder.class.getName())
                .check(classes);
    }

    @Test
    @DisplayName("Only approved classes may depend on UserService outside controllers")
    void onlyApprovedClassesMayDependOnUserServiceOutsideControllers() {
        noClasses()
                .that().resideInAnyPackage("..security..", "..service..", "..bootstrap..")
                .and().areNotAssignableTo(JwtService.class)
                .and().areNotAssignableTo(UserProfileFacade.class)
                .and().areNotAssignableTo(UserDataLoader.class)
                .and().areNotAssignableTo(UserServiceImpl.class)
                .should().dependOnClassesThat().areAssignableTo(UserService.class)
                .check(classes);
    }

    @Test
    @DisplayName("No direct dependencies on *Config classes outside config package")
    void noDirectConfigDependenciesOutsideConfigPackage() {
        noClasses()
                .that().resideOutsideOfPackage("..config..")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("Config")
                .check(classes);
    }
}

