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

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Holds the terminal token portion of the rhs of a rule.
 */
public final class EbnfTerminalParserToken extends EbnfLeafParserToken<String> {

    static EbnfTerminalParserToken with(final String value, final String text) {
        checkValue(value);
        checkText(text);

        return new EbnfTerminalParserToken(value, text);
    }

    private EbnfTerminalParserToken(final String value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<EbnfTerminalParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                EbnfTerminalParserToken.class
        );
    }

    // removeIf........................................................................................................

    @Override
    public Optional<EbnfTerminalParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                EbnfTerminalParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public EbnfTerminalParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                  final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                EbnfTerminalParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public EbnfTerminalParserToken replaceIf(final Predicate<ParserToken> predicate,
                                             final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                EbnfTerminalParserToken.class
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
        return other instanceof EbnfTerminalParserToken;
    }
}
