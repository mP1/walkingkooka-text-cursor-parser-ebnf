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

import java.util.function.Predicate;

/**
 * Holds the combination of whitespace or comments.
 */
public final class EbnfWhitespaceParserToken extends EbnfLeafParserToken<String> {

    static EbnfWhitespaceParserToken with(final String value, final String text) {
        return new EbnfWhitespaceParserToken(
                checkValue(value),
                CharSequences.failIfNullOrEmpty(text, "text")
        );
    }

    private EbnfWhitespaceParserToken(final String value, final String text) {
        super(value, text);
    }

    // isXXX............................................................................................................

    @Override
    public boolean isNoise() {
        return true;
    }

    // replaceFirstIf...................................................................................................

    @Override
    public EbnfWhitespaceParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                    final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                EbnfWhitespaceParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public EbnfWhitespaceParserToken replaceIf(final Predicate<ParserToken> predicate,
                                               final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                EbnfWhitespaceParserToken.class
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
        return other instanceof EbnfWhitespaceParserToken;
    }
}
