/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.text.cursor.parser.ebnf;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.visit.Visiting;

import java.util.Map;
import java.util.Set;

/**
 * Collects all rule identifiers and references.
 */
final class GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor extends EbnfParserTokenVisitor {

    static GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor with() {
        return new GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor();
    }

    GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor() {
        super();
    }

    // RULE ........................................................................................................

    @Override
    protected Visiting startVisit(final RuleEbnfParserToken rule) {
        final EbnfIdentifierName identifier = rule.identifier().value();
        Set<RuleEbnfParserToken> rules = this.ruleIdentifiers.get(identifier);
        if (null == rules) {
            rules = Sets.ordered();
            this.ruleIdentifiers.put(identifier, rules);
        }
        rules.add(rule);

        this.accept(rule.assignment()); // RHS.. visiting everything on the RHS to find identifiers which are actually references.
        return Visiting.SKIP;
    }

    // IDENTIFIER ........................................................................................................

    @Override
    protected void visit(final IdentifierEbnfParserToken identifier) {
        this.references.add(identifier.value());
    }

    // HELPERS ......................................................................................................

    final Map<EbnfIdentifierName, Set<RuleEbnfParserToken>> ruleIdentifiers = Maps.ordered();
    final Set<EbnfIdentifierName> references = Sets.ordered();

    @Override
    public String toString() {
        return this.references + " " + this.ruleIdentifiers;
    }
}

