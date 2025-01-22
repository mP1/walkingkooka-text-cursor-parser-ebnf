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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;

import java.util.Objects;
import java.util.Optional;

/**
 * A proxy for a {@link Parser} that is used mostly for definitions with self references.
 */
final class EbnfParserCombinatorProxyParser<C extends ParserContext> implements Parser<C> {

    static <C extends ParserContext> EbnfParserCombinatorProxyParser<C> with(final EbnfIdentifierParserToken identifier) {
        return new EbnfParserCombinatorProxyParser<>(
                Objects.requireNonNull(identifier, "identifier")
        );
    }

    private EbnfParserCombinatorProxyParser(final EbnfIdentifierParserToken identifier) {
        this.identifier = identifier;
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final C context) {
        return this.parser.parse(cursor, context);
    }

    final EbnfIdentifierParserToken identifier;

    void setParser(final Parser<C> parser) {
        if (null == parser) {
            throw new EbnfParserCombinatorException("Duplicate parser " + this.identifier);
        }
        this.parser = parser;
    }

    /**
     * The actual parser is once its rule is eventually visited completely.
     */
    private Parser<C> parser;

    /**
     * If the parser is present return {@link Object#toString()} otherwise return the {@link #identifier}.
     */
    @Override
    public String toString() {
        final Parser<C> parser = this.parser;

        return null != parser ?
                parser.toString() :
                this.identifier.toString();
    }
}
