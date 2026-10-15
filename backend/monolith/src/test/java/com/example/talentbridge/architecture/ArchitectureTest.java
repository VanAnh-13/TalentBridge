package com.example.talentbridge.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Clean Architecture dependency rules.
 *
 * <pre>
 *   presentation -> application -> domain <- infrastructure (implements domain ports)
 * </pre>
 *
 * The codebase is migrated module by module; a module is added to {@link #MIGRATED} in the same commit that
 * moves it, from then on the rules are enforced for it. Legacy packages (controller/service/model/...) are
 * not covered until their module is migrated.
 */
class ArchitectureTest {

    private static final String BASE = "com.example.talentbridge";

    /** Modules that follow the four-layer structure. Keep sorted. */
    private static final List<String> MIGRATED = List.of("access", "auth", "chat", "company", "digest", "interview", "job", "matching", "resume", "shared", "skill", "subscriber", "user");

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    private static String pkg(String module, String layer) {
        return BASE + "." + module + "." + layer + "..";
    }

    @TestFactory
    Stream<DynamicTest> layerRules() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String m : MIGRATED) {
            tests.add(DynamicTest.dynamicTest(m + ".domain is framework free", () ->
                    classes().that().resideInAPackage(pkg(m, "domain"))
                            .should().onlyDependOnClassesThat().resideInAnyPackage(
                                    "java..", "lombok..", pkg(m, "domain"), pkg("shared", "domain"))
                            .because("the domain must not know about Spring, JPA, HTTP or other modules")
                            .allowEmptyShould(true).check(classes)));

            tests.add(DynamicTest.dynamicTest(m + ".application depends on neither adapters nor frameworks", () ->
                    noClasses().that().resideInAPackage(pkg(m, "application"))
                            .should().dependOnClassesThat().resideInAnyPackage(
                                    BASE + ".." + "infrastructure..", BASE + ".." + "presentation..",
                                    "jakarta.persistence..", "jakarta.servlet..", "jakarta.validation..",
                                    "org.springframework.data..", "org.springframework.web..",
                                    "org.springframework.http..", "org.springframework.security..",
                                    "org.hibernate..", "com.fasterxml..", "io.swagger..")
                            .because("use cases talk to the outside world only through ports")
                            .allowEmptyShould(true).check(classes)));

            tests.add(DynamicTest.dynamicTest(m + ".presentation does not reach into infrastructure", () ->
                    noClasses().that().resideInAPackage(pkg(m, "presentation"))
                            .should().dependOnClassesThat().resideInAPackage(BASE + ".." + "infrastructure..")
                            .allowEmptyShould(true).check(classes)));

            tests.add(DynamicTest.dynamicTest(m + ".infrastructure does not depend on presentation", () ->
                    noClasses().that().resideInAPackage(pkg(m, "infrastructure"))
                            .should().dependOnClassesThat().resideInAPackage(BASE + ".." + "presentation..")
                            .allowEmptyShould(true).check(classes)));

            tests.add(DynamicTest.dynamicTest(m + ".domain and application do not depend on the outer layers of other modules", () ->
                    noClasses().that().resideInAnyPackage(pkg(m, "domain"), pkg(m, "application"))
                            .should().dependOnClassesThat().resideInAnyPackage(otherModuleAdapters(m))
                            .allowEmptyShould(true).check(classes)));
        }
        return tests.stream();
    }

    private static String[] otherModuleAdapters(String self) {
        return MIGRATED.stream()
                .filter(o -> !o.equals(self) && !o.equals("shared"))
                .flatMap(o -> Stream.of(pkg(o, "infrastructure"), pkg(o, "presentation")))
                .toArray(String[]::new);
    }

    @TestFactory
    Stream<DynamicTest> modulesAreIsolated() {
        // A module may use another module's domain/application API, never its adapters.
        // One exception: persistence associations (@ManyToMany, ...) must name the other module's JPA entity class.
        List<DynamicTest> tests = new ArrayList<>();
        for (String m : MIGRATED) {
            if (m.equals("shared")) continue;
            tests.add(DynamicTest.dynamicTest(m + " (outside infrastructure) does not use other modules' adapters", () ->
                    noClasses().that().resideInAPackage(BASE + "." + m + "..")
                            .and().resideOutsideOfPackage(pkg(m, "infrastructure"))
                            .should().dependOnClassesThat().resideInAnyPackage(otherModuleAdapters(m))
                            .allowEmptyShould(true).check(classes)));

            tests.add(DynamicTest.dynamicTest(m + ".infrastructure only shares other modules' JPA entities", () ->
                    noClasses().that().resideInAPackage(pkg(m, "infrastructure"))
                            .should().dependOnClassesThat(
                                    resideInAnyPackage(otherModuleAdapters(m))
                                            .and(DescribedPredicate.not(simpleNameEndingWith("JpaEntity"))))
                            .allowEmptyShould(true).check(classes)));
        }
        return tests.stream();
    }
}
