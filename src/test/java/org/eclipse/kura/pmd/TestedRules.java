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

import java.util.List;
import java.util.stream.Stream;

/**
 * Rules covered by an XML test file: every custom rule, and every referenced built-in rule whose properties the
 * rulesets override. {@link RulesetIntegrityTest} fails if a ruleset gains such a rule that is not listed here.
 */
final class TestedRules {

    static final String BI_RULESET = "eurotech-java-ruleset-BI.xml";
    static final String SIL2_RULESET = "eurotech-java-ruleset-SIL2.xml";

    /** Built-in rules referenced with overridden properties. */
    static final List<String> CONFIGURED_BUILT_IN = List.of(
            "LabeledStatement",
            "CognitiveComplexity",
            "AvoidReassigningLoopVariables",
            "AvoidCatchingGenericException",
            "EmptyCatchBlock");

    /** Custom rules defined in both rulesets. */
    static final List<String> CUSTOM_SHARED = List.of(
            "EJCS-X01-LoopWithMultipleExits",
            "EJCS-X02-JumpOutOfFinally",
            "EJCS-X03-DirectRecursion",
            "EJCS-X04-FloatingPointLoopCounter",
            "EJCS-X05-FinalizeOverride",
            "EJCS-X06-InterruptedExceptionSwallowed",
            "EJCS-X07-LockNotReleasedInFinally",
            "EJCS-X08-WaitOutsideLoop",
            "EJCS-X09-LocalShadowsField",
            "EJCS-X11-FloatingPointEquality",
            "EJCS-X12-BigDecimalFromDouble",
            "EJCS-X15-DiscardedStatusResult",
            "EJCS-X17-PublicMutableField");

    /** Custom rules of clause 5.11, defined in the SIL 2 ruleset only. */
    static final List<String> CUSTOM_SIL2_ONLY = List.of(
            "EJCS-X10-UncheckedIntegerArithmetic",
            "EJCS-X13-UnvalidatedBoundaryParameter",
            "EJCS-X14-AssertForValidation");

    static final List<String> BI = concat(CONFIGURED_BUILT_IN, CUSTOM_SHARED);
    static final List<String> SIL2 = concat(CONFIGURED_BUILT_IN, CUSTOM_SHARED, CUSTOM_SIL2_ONLY);

    private TestedRules() {
    }

    @SafeVarargs
    private static List<String> concat(List<String>... lists) {
        return Stream.of(lists).flatMap(List::stream).toList();
    }
}
