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

import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds any of the symbols that separate actual tokens, such as the parens around a grouping.
 */
final public class EbnfSymbolParserToken extends EbnfLeafParserToken<String> {

    static EbnfSymbolParserToken with(final String symbol, final String text) {
        return new EbnfSymbolParserToken(
                CharSequences.failIfNullOrEmpty(symbol, "symbol"),
                checkText(text)
        );
    }

    private EbnfSymbolParserToken(final String symbol, final String text) {
        super(symbol, text);
    }

    // isXXX............................................................................................................

    @Override
    public boolean isNoise() {
        return true;
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<EbnfSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                EbnfSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<EbnfSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                EbnfSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public EbnfSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                EbnfSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public EbnfSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                           final Function<ParserToken, ParserToken> token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                EbnfSymbolParserToken.class
        );
    }

    // EbnfParserTokenVisitor............................................................................................

    @Override
    public void accept(final EbnfParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof EbnfSymbolParserToken;
    }
}
