package com.example.talentbridge.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** The same layer rules as the monolith: presentation -> application -> domain <- infrastructure. */
class ArchitectureTest {

    private static final String BASE = "com.example.talentbridge";
    private static final List<String> MODULES = List.of("notification", "realtime");

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(BASE);
    }

    private static String pkg(String module, String layer) {
        return BASE + "." + module + "." + layer + "..";
    }

    @TestFactory
    Stream<DynamicTest> layers() {
        return MODULES.stream().flatMap(m -> Stream.of(
                DynamicTest.dynamicTest(m + ".domain is framework free", () -> classes().that().resideInAPackage(pkg(m, "domain"))
                        .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "lombok..", pkg(m, "domain"), pkg("shared", "domain"))
                        .allowEmptyShould(true).check(classes)),
                DynamicTest.dynamicTest(m + ".application depends on neither adapters nor frameworks", () -> noClasses()
                        .that().resideInAPackage(pkg(m, "application"))
                        .should().dependOnClassesThat().resideInAnyPackage(BASE + "..infrastructure..", BASE + "..presentation..",
                                BASE + ".events..", "jakarta.persistence..", "org.springframework.data..", "org.springframework.web..",
                                "org.springframework.messaging..", "org.springframework.kafka..", "com.fasterxml..")
                        .allowEmptyShould(true).check(classes)),
                DynamicTest.dynamicTest(m + ".presentation does not reach into infrastructure", () -> noClasses()
                        .that().resideInAPackage(pkg(m, "presentation"))
                        .should().dependOnClassesThat().resideInAPackage(BASE + "..infrastructure..")
                        .allowEmptyShould(true).check(classes)),
                DynamicTest.dynamicTest(m + " does not use another module's adapters", () -> noClasses()
                        .that().resideInAPackage(BASE + "." + m + "..")
                        .should().dependOnClassesThat().resideInAnyPackage(MODULES.stream().filter(o -> !o.equals(m))
                                .flatMap(o -> Stream.of(pkg(o, "infrastructure"), pkg(o, "presentation"))).toArray(String[]::new))
                        .allowEmptyShould(true).check(classes))));
    }
}
