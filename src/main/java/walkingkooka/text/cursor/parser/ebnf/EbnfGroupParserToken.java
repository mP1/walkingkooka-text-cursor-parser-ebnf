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
import java.util.function.Predicate;

/**
 * Represents an grouped token in the grammar.
 */
public final class EbnfGroupParserToken extends EbnfParentParserToken<EbnfGroupParserToken> {

    static EbnfGroupParserToken with(final List<ParserToken> tokens, final String text) {
        return new EbnfGroupParserToken(copyAndCheckTokens(tokens), checkText(text));
    }

    private EbnfGroupParserToken(final List<ParserToken> tokens, final String text) {
        super(tokens, text);
        this.checkOnlyOneToken();
    }

    // children.........................................................................................................

    @Override
    public EbnfGroupParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                EbnfGroupParserToken::new
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public EbnfGroupParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                EbnfGroupParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public EbnfGroupParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                EbnfGroupParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public EbnfGroupParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                               final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                EbnfGroupParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public EbnfGroupParserToken replaceIf(final Predicate<ParserToken> predicate,
                                          final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                EbnfGroupParserToken.class
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
        return other instanceof EbnfGroupParserToken;
    }

}
