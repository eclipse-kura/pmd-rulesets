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

import net.sourceforge.pmd.test.SimpleAggregatorTst;

/**
 * Runs the XML test cases in {@code xml/<RuleName>.xml} against the rules of the Basic Integrity ruleset. The test
 * cases are shared with {@link Sil2RulesetTest}, so a rule copied into both rulesets is checked in both copies.
 */
class BasicIntegrityRulesetTest extends SimpleAggregatorTst {

    @Override
    protected void setUp() {
        for (String rule : TestedRules.BI) {
            addRule(TestedRules.BI_RULESET, rule);
        }
    }
}
