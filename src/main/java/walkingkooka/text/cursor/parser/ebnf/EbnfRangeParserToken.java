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
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a list of alternative token in the grammar.
 */
final public class EbnfRangeParserToken extends EbnfParentParserToken<EbnfRangeParserToken> {

    static EbnfRangeParserToken with(final List<ParserToken> tokens, final String text) {
        final List<ParserToken> copy = copyAndCheckTokens(tokens);
        checkText(text);

        return new EbnfRangeParserToken(copy, text);
    }

    private EbnfRangeParserToken(final List<ParserToken> tokens,
                                 final String text) {
        super(tokens, text);
        this.checkOnlyTwoTokens();

        final EbnfRangeParserTokenConsumer checker = EbnfRangeParserTokenConsumer.with();
        tokens.stream()
                .filter(t -> t instanceof EbnfParserToken)
                .map(EbnfRangeParserToken::toEbnfParserToken)
                .forEach(checker);

        final EbnfParserToken begin = checker.begin;
        if (null == begin) {
            throw new IllegalArgumentException("Range missing begin|identifier=" + text);
        }
        final EbnfParserToken end = checker.end;
        if (null == end) {
            throw new IllegalArgumentException("Range missing end terminal|identifier=" + text);
        }

        this.begin = begin;
        this.end = end;
    }

    private static EbnfParserToken toEbnfParserToken(final ParserToken token) {
        return token.cast(EbnfParserToken.class);
    }

    public EbnfParserToken begin() {
        return this.begin;
    }

    private final EbnfParserToken begin;

    public EbnfParserToken end() {
        return this.end;
    }

    private final EbnfParserToken end;

    // children.........................................................................................................

    @Override
    public EbnfRangeParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                EbnfRangeParserToken::new
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<EbnfRangeParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                EbnfRangeParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public EbnfRangeParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfParent(
                this,
                predicate,
                EbnfRangeParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public EbnfRangeParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                               final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                EbnfRangeParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public EbnfRangeParserToken replaceIf(final Predicate<ParserToken> predicate,
                                          final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                EbnfRangeParserToken.class
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

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof EbnfRangeParserToken;
    }

}
