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
 * Represents a single rule definition within a grammar.
 */
public final class RuleEbnfParserToken extends ParentEbnfParserToken {

    static RuleEbnfParserToken with(final List<ParserToken> tokens, final String text) {
        final List<ParserToken> copy = copyAndCheckTokens(tokens);
        checkText(text);

        return new RuleEbnfParserToken(copy,
                text);
    }

    private RuleEbnfParserToken(final List<ParserToken> tokens,
                                final String text) {
        super(tokens, text);

        final RuleEbnfParserTokenConsumer checker = RuleEbnfParserTokenConsumer.with();
        tokens.stream()
                .filter(t -> t instanceof EbnfParserToken)
                .map(RuleEbnfParserToken::toEbnfParserToken)
                .forEach(checker);

        final IdentifierEbnfParserToken identifier = checker.identifier;
        if (null == identifier) {
            throw new IllegalArgumentException("Rule missing Identifier on lhs=" + text);
        }
        final EbnfParserToken assignment = checker.assignment;
        if (null == assignment) {
            throw new IllegalArgumentException("Rule missing assignment on rhs=" + text);
        }

        this.identifier = identifier;
        this.assignment = assignment;
    }

    private static EbnfParserToken toEbnfParserToken(final ParserToken token) {
        return token.cast(EbnfParserToken.class);
    }

    public IdentifierEbnfParserToken identifier() {
        return this.identifier;
    }

    private final IdentifierEbnfParserToken identifier;

    public EbnfParserToken assignment() {
        return this.assignment;
    }

    private final EbnfParserToken assignment;

    // children.........................................................................................................

    @Override
    public RuleEbnfParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                RuleEbnfParserToken::new
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
