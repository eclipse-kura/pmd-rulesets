# pmd-rulesets

Eurotech's PMD rulesets for Kura projects, implementing the Eurotech Java Coding Standard (EJCS).

## Rulesets

| File | Scope |
| --- | --- |
| [`eurotech-java-ruleset-BI.xml`](src/main/resources/eurotech-java-ruleset-BI.xml) | Basic Integrity: every rule except clause 5.11 |
| [`eurotech-java-ruleset-SIL2.xml`](src/main/resources/eurotech-java-ruleset-SIL2.xml) | SIL 2: every rule, including clause 5.11 |

Each ruleset is a single, self-contained file. The SIL 2 ruleset is the Basic Integrity ruleset plus the clause 5.11
rules `EJCS-X10-UncheckedIntegerArithmetic`, `EJCS-X13-UnvalidatedBoundaryParameter` and
`EJCS-X14-AssertForValidation`; every other rule is defined identically in both files (this is enforced by the tests).

The rulesets target PMD 7.27.0.

## Usage

Reference a ruleset from `maven-pmd-plugin` by its raw URL, pinned to a tag or commit:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.28.0</version>
    <configuration>
        <rulesets>
            <ruleset>https://raw.githubusercontent.com/eclipse-kura/pmd-rulesets/<tag-or-commit>/src/main/resources/eurotech-java-ruleset-SIL2.xml</ruleset>
        </rulesets>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>net.sourceforge.pmd</groupId>
            <artifactId>pmd-java</artifactId>
            <version>7.27.0</version>
        </dependency>
    </dependencies>
</plugin>
```

The boundary rules `EJCS-X13` and `EJCS-X14` take a `scope` property, a regex of the packages they apply to (`.*`
by default).

## Layout

```
src/main/resources/                         the rulesets
src/test/java/org/eclipse/kura/pmd/
    TestedRules.java                        rules covered by test files, per ruleset
    Sil2RulesetTest.java                    runs the test files against the SIL 2 ruleset
    BasicIntegrityRulesetTest.java          runs the same test files against the Basic Integrity ruleset
    RulesetIntegrityTest.java               rulesets load cleanly, shared rules are identical, every rule is tested
src/test/resources/org/eclipse/kura/pmd/xml/
    <RuleName>.xml                          test cases of one rule
```

Tests use PMD's [rule testing framework](https://docs.pmd-code.org/latest/pmd_userdocs_extending_testing.html): each
`<RuleName>.xml` holds code snippets with the expected number of violations and their lines. Every custom rule has a
test file, and so does every built-in rule whose properties the rulesets override.

## Building

Requires JDK 17 or later (the test framework uses JUnit 6).

```sh
mvn verify
```

## Adding or changing a rule

1. Edit the rule in `eurotech-java-ruleset-SIL2.xml` and, unless it belongs to clause 5.11 only, make the same change
   in `eurotech-java-ruleset-BI.xml`.
2. Add or update `src/test/resources/org/eclipse/kura/pmd/xml/<RuleName>.xml` with cases that violate the rule and
   cases that comply with it.
3. List a new rule in `TestedRules.java`.
4. Run `mvn verify`.

Rule messages are formatted with `java.text.MessageFormat`: a single quote is an escape character and is dropped from
the output, so write `''` for an apostrophe, or rephrase.
