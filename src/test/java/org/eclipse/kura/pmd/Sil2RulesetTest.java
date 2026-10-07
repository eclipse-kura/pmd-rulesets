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
 * Runs the XML test cases in {@code xml/<RuleName>.xml} against the rules of the SIL 2 ruleset.
 */
class Sil2RulesetTest extends SimpleAggregatorTst {

    @Override
    protected void setUp() {
        for (String rule : TestedRules.SIL2) {
            addRule(TestedRules.SIL2_RULESET, rule);
        }
    }
}
