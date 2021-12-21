package tr.edu.gazi.kutuphanem;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("tr.edu.gazi.kutuphanem");

        noClasses()
            .that()
            .resideInAnyPackage("tr.edu.gazi.kutuphanem.service..")
            .or()
            .resideInAnyPackage("tr.edu.gazi.kutuphanem.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..tr.edu.gazi.kutuphanem.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
