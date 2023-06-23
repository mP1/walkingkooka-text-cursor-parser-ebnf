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

/**
 * Represents an repeated token in the grammar.
 */
public final class EbnfRepeatedParserToken extends EbnfParentParserToken<EbnfRepeatedParserToken> {

    static EbnfRepeatedParserToken with(final List<ParserToken> tokens, final String text) {
        return new EbnfRepeatedParserToken(copyAndCheckTokens(tokens), checkText(text));
    }

    private EbnfRepeatedParserToken(final List<ParserToken> tokens, final String text) {
        super(tokens, text);
        this.checkOnlyOneToken();
    }

    // children.........................................................................................................

    @Override
    public EbnfRepeatedParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                EbnfRepeatedParserToken::new
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
        return other instanceof EbnfRepeatedParserToken;
    }

}
