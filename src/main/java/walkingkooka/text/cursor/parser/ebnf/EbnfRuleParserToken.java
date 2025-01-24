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
public final class EbnfRuleParserToken extends EbnfParentParserToken<EbnfRuleParserToken> {

    static EbnfRuleParserToken with(final List<ParserToken> tokens, final String text) {
        final List<ParserToken> copy = copyAndCheckTokens(tokens);
        checkText(text);

        return new EbnfRuleParserToken(copy,
                text);
    }

    private EbnfRuleParserToken(final List<ParserToken> tokens,
                                final String text) {
        super(tokens, text);

        final EbnfRuleParserTokenConsumer checker = EbnfRuleParserTokenConsumer.with();
        tokens.stream()
                .filter(t -> t instanceof EbnfParserToken)
                .map(EbnfRuleParserToken::toEbnfParserToken)
                .forEach(checker);

        final EbnfIdentifierParserToken identifier = checker.identifier;
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

    public EbnfIdentifierParserToken identifier() {
        return this.identifier;
    }

    private final EbnfIdentifierParserToken identifier;

    public EbnfParserToken assignment() {
        return this.assignment;
    }

    private final EbnfParserToken assignment;

    // children.........................................................................................................

    @Override
    public EbnfRuleParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                EbnfRuleParserToken::new
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
