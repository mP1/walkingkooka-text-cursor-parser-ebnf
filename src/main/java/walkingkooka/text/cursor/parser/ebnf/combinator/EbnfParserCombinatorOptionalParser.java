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

import walkingkooka.Cast;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Parser} used to tag a real {@link Parser} as OPTIONAL within the process of transforming a grammar into parsers.
 * This parser always throws {@link UnsupportedOperationException} and references should never be seen outside.
 */
final class EbnfParserCombinatorOptionalParser<C extends ParserContext> implements Parser<C> {

    static <C extends ParserContext> Parser<C> unwrapIfNecessary(final Parser<C> parser) {
        Objects.requireNonNull(parser, "parser");

        Parser<C> notOptional = parser;

        if (parser instanceof EbnfParserCombinatorOptionalParser) {
            final EbnfParserCombinatorOptionalParser<C> optional = Cast.to(parser);
            notOptional = optional.parser;
        }

        return notOptional;
    }

    static <C extends ParserContext> EbnfParserCombinatorOptionalParser<C> with(final Parser<C> parser,
                                                                                final EbnfParserToken token) {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(token, "token");

        return parser instanceof EbnfParserCombinatorOptionalParser ?
                (EbnfParserCombinatorOptionalParser<C>) parser :
                new EbnfParserCombinatorOptionalParser<>(
                        parser,
                        token
                );
    }

    private EbnfParserCombinatorOptionalParser(final Parser<C> parser,
                                               final EbnfParserToken token) {
        this.parser = parser;
        this.token = token;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final C context) {
        throw new UnsupportedOperationException(this.token.toString());
    }

    @Override
    public int minCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int maxCount() {
        throw new UnsupportedOperationException();
    }

    final Parser<C> parser;

    @Override
    public String toString() {
        return this.token.toString();
    }

    private final EbnfParserToken token;
}
