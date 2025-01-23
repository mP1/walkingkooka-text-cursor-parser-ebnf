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

package walkingkooka.text.cursor.parser.ebnf.combinator;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A proxy for each {@link EbnfParserToken} in a grammar.
 */
final class EbnfParserCombinatorsProxy<C extends ParserContext> {

    static <C extends ParserContext> EbnfParserCombinatorsProxy<C> with(final EbnfParserToken token,
                                                                        final EbnfParserCombinatorContext<C> context) {
        return new EbnfParserCombinatorsProxy<>(
                Objects.requireNonNull(token, "token"),
                Objects.requireNonNull(context, "context")
        );
    }

    private EbnfParserCombinatorsProxy(final EbnfParserToken token,
                                       final EbnfParserCombinatorContext<C> context) {
        this.token = token;
        this.context = context;
    }

    Optional<List<Parser<C>>> childParsers() {
        List<Parser<C>> parsers = Lists.array();

        for (final EbnfParserCombinatorsProxy<C> child : this.children) {
            final Optional<Parser<C>> childParser = child.parser();
            if (false == childParser.isPresent()) {
                parsers = null;
                break;
            }

            parsers.add(childParser.get());
        }

        return Optional.ofNullable(parsers);
    }

    Optional<Parser<C>> firstChildParser() {
        return this.childParsers()
                .map(this::firstChildParserOnly);
    }

    private Parser<C> firstChildParserOnly(final List<Parser<C>> parsers) {
        final int count = parsers.size();

        switch (count) {
            case 0:
                throw new EbnfParserCombinatorException("Missing parsers for " + this.token);
            case 1:
                return parsers.get(0);
            default:
                throw new EbnfParserCombinatorException("Expected 1 but got " + count + " parsers for " + this.token);
        }
    }

    /**
     * Lazily transforms the {@link #token} if necessary.
     */
    Optional<Parser<C>> parser() {
        if (null == this.parser) {
            if (this.context.add(this)) {
                EbnfParserCombinatorsTransformEbnfParserTokenVisitor.transform(
                        this.token,
                        this,
                        this.context
                );
            }
        }
        return Optional.ofNullable(this.parser);
    }

    /**
     * The token within the grammar.
     */
    final EbnfParserToken token;

    private final EbnfParserCombinatorContext<C> context;

    /**
     * Proxies for each child. This can contain between one and many with examples of the later being an {@link walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken}.
     */
    final List<EbnfParserCombinatorsProxy<C>> children = Lists.array();

    void setParser(final Parser<C> parser) {
        if (null != this.parser) {
            throw new EbnfParserCombinatorException("Attempt to replace parser=" + this);
        }

        this.parser = parser;
        this.context.missingParserCreated(this.token);
    }

    /**
     * The parser equivalent produced by the given {@link EbnfParserCombinatorGrammarTransformer}.
     * For {@link EbnfIdentifierParserToken} this will be prepopulated with a {@link EbnfParserCombinatorProxyParser}.
     */
    Parser<C> parser;

    @Override
    public String toString() {
        return this.token.getClass().getSimpleName() + "=" + this.token + " parser=" + this.parser;
    }
}
