/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Eurotech
 *******************************************************************************/
package org.eclipse.kura.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.event.Level;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Checks on the rulesets as a whole: they load cleanly, the rules they share are identical, and every rule that needs
 * a test has one.
 */
class RulesetIntegrityTest {

    @ParameterizedTest
    @ValueSource(strings = { TestedRules.BI_RULESET, TestedRules.SIL2_RULESET })
    void rulesetLoadsWithoutWarnings(String ruleset) {
        RecordingReporter reporter = new RecordingReporter();
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setReporter(reporter);

        RuleSet rules = RuleSetLoader.fromPmdConfig(configuration).warnDeprecated(true).loadFromResource(ruleset);

        assertEquals(List.of(), reporter.messages, "messages reported while loading " + ruleset);
        for (Rule rule : rules.getRules()) {
            assertNull(rule.dysfunctionReason(), rule.getName() + " is dysfunctional");
        }
    }

    @Test
    void basicIntegrityRulesAreIdenticalInSil2() {
        RuleSet bi = load(TestedRules.BI_RULESET);
        RuleSet sil2 = load(TestedRules.SIL2_RULESET);

        for (Rule biRule : bi.getRules()) {
            Rule sil2Rule = sil2.getRuleByName(biRule.getName());
            assertNotNull(sil2Rule, biRule.getName() + " is missing from " + TestedRules.SIL2_RULESET);
            assertEquals(definition(biRule), definition(sil2Rule), biRule.getName() + " differs between the rulesets");
        }
    }

    @Test
    void sil2AddsOnlyTheClause511Rules() {
        List<String> biNames = names(load(TestedRules.BI_RULESET));
        List<String> sil2Only = names(load(TestedRules.SIL2_RULESET)).stream()
                .filter(name -> !biNames.contains(name))
                .collect(Collectors.toList());

        assertEquals(TestedRules.CUSTOM_SIL2_ONLY, sil2Only);
    }

    @ParameterizedTest
    @ValueSource(strings = { TestedRules.BI_RULESET, TestedRules.SIL2_RULESET })
    void everyCustomOrConfiguredRuleIsTested(String ruleset) {
        List<String> tested = TestedRules.BI_RULESET.equals(ruleset) ? TestedRules.BI : TestedRules.SIL2;

        for (Rule rule : load(ruleset).getRules()) {
            boolean custom = !(rule instanceof RuleReference);
            boolean configured = rule instanceof RuleReference
                    && !((RuleReference) rule).getOverriddenPropertyDescriptors().isEmpty();
            if (custom || configured) {
                assertTrue(tested.contains(rule.getName()), rule.getName() + " is not listed in TestedRules");
                assertNotNull(getClass().getResource("xml/" + rule.getName() + ".xml"),
                        "no test file xml/" + rule.getName() + ".xml");
            }
        }
    }

    private static RuleSet load(String ruleset) {
        return new RuleSetLoader().loadFromResource(ruleset);
    }

    private static List<String> names(RuleSet ruleset) {
        return ruleset.getRules().stream().map(Rule::getName).collect(Collectors.toList());
    }

    private static Map<String, String> definition(Rule rule) {
        Map<String, String> definition = new LinkedHashMap<>();
        definition.put("class", rule.getRuleClass());
        definition.put("language", rule.getLanguage().getId());
        definition.put("since", rule.getSince());
        definition.put("priority", String.valueOf(rule.getPriority()));
        definition.put("message", rule.getMessage());
        definition.put("description", rule.getDescription());
        Map<String, String> properties = new TreeMap<>();
        rule.getPropertiesByPropertyDescriptor()
                .forEach((descriptor, value) -> properties.put(descriptor.name(), String.valueOf(value)));
        definition.put("properties", properties.toString());
        return definition;
    }

    private static final class RecordingReporter implements PmdReporter {

        private final List<String> messages = new ArrayList<>();
        private int errors;

        @Override
        public boolean isLoggable(Level level) {
            return level.compareTo(Level.WARN) <= 0;
        }

        @Override
        public void logEx(Level level, String message, Object[] formatArgs, Throwable error) {
            if (isLoggable(level)) {
                messages.add(level + ": " + MessageFormat.format(String.valueOf(message), formatArgs)
                        + (error == null ? "" : " (" + error + ")"));
                if (level == Level.ERROR) {
                    errors++;
                }
            }
        }

        @Override
        public int numErrors() {
            return errors;
        }
    }
}
