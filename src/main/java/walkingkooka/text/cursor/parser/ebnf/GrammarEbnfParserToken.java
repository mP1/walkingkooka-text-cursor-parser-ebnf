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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorGrammarTransformer;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinators;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A grammar holds all the rules and is the root of the graph. Note the {@link #value()} will contain a mixture of rules,
 * comments and whitespace.
 */
public final class GrammarEbnfParserToken extends ParentEbnfParserToken {

    static GrammarEbnfParserToken with(final List<ParserToken> tokens, final String text) {
        Objects.requireNonNull(tokens, "tokens");
        checkText(text);

        return new GrammarEbnfParserToken(Lists.immutable(tokens),
                text);
    }

    private GrammarEbnfParserToken(final List<ParserToken> tokens, final String text) {
        super(tokens, text);

        final Optional<ParserToken> firstRule = tokens.stream()
                .filter(t -> t instanceof RuleEbnfParserToken)
                .findFirst();
        if (!firstRule.isPresent()) {
            throw new IllegalArgumentException("Grammar requires at least 1 rule=" + tokens);
        }
    }

    /**
     * Constant to be passed to {@link #checkIdentifiers(Set)} if no external references exist.
     */
    public final static Set<EbnfIdentifierName> NO_EXTERNALS = Sets.empty();

    /**
     * Verifies that all identifiers that appear on the RHS of all rules, must be valid.
     */
    public void checkIdentifiers(final Set<EbnfIdentifierName> external) {
        Objects.requireNonNull(external, "external");

        final GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor visitor = GrammarEbnfParserTokenReferenceCollectorEbnfParserTokenVisitor.with();
        visitor.accept(this);

        final Map<EbnfIdentifierName, Set<RuleEbnfParserToken>> identifiers = visitor.ruleIdentifiers;
        final Set<RuleEbnfParserToken> duplicates = Sets.ordered();

        identifiers.values()
                .stream()
                .filter(e -> e.size() > 1)
                .forEach(duplicates::addAll);
        if (!duplicates.isEmpty()) {
            throw new GrammarEbnfParserTokenDuplicateIdentifiersException(duplicates.size() + " rules with the same identifier=" + duplicates, duplicates);
        }

        final Set<EbnfIdentifierName> missing = SortedSets.tree();
        missing.addAll(visitor.references);
        missing.removeAll(identifiers.keySet());
        missing.removeAll(external);

        if (!missing.isEmpty()) {
            throw new EbnfGrammarParserTokenInvalidReferencesException(missing.size() + " invalid (unknown) references=" + missing, missing);
        }
    }

    public <C extends ParserContext> Function<EbnfIdentifierName, Optional<Parser<C>>> combinator(final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                                                                                  final EbnfParserCombinatorGrammarTransformer<C> transformer) {
        return EbnfParserCombinators.transform(
                this,
                identifierToParser,
                transformer
        );
    }

    /**
     * Identical in functionality to {@link #combinator(Function, EbnfParserCombinatorGrammarTransformer)}, except the function return will throw if the parser requested is not found.
     */
    public <C extends ParserContext> Function<EbnfIdentifierName, Parser<C>> combinatorForFile(final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                                                                               final EbnfParserCombinatorGrammarTransformer<C> transformer,
                                                                                               final String filename) {
        return EbnfParserCombinators.transformForFile(
                this,
                identifierToParser,
                transformer,
                filename
        );
    }

    // children.........................................................................................................

    @Override
    public GrammarEbnfParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                GrammarEbnfParserToken::new
        );
    }

    // EbnfParserTokenVisitor............................................................................................

    @Override
    public void accept(final EbnfParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }
}
