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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorException;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;

/**
 * Represents a token within an EBNF grammar.
 */
public abstract class EbnfParserToken implements ParserToken {

    /**
     * Parses the given EBNF grammar returning the {@link EbnfGrammarParserToken}.
     */
    public static EbnfGrammarParserToken parse(final String text) {
        return EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parseText(
                        text,
                        EbnfParserContexts.basic()
                ).cast(EbnfGrammarParserToken.class);
    }

    public static EbnfGrammarParserToken parseFile(final String text,
                                                   final String filename) {
        Objects.requireNonNull(text, "text");
        CharSequences.failIfNullOrEmpty(filename, "filename");

        try {
            return parse(text);
        } catch (final RuntimeException cause) {
            throw new EbnfParserCombinatorException(
                    "Unable to parse grammar in file " + CharSequences.quoteAndEscape(filename),
                    cause
            );
        }
    }

    /**
     * {@see EbnfAlternativeParserToken}
     */
    public static EbnfAlternativeParserToken alternative(final List<ParserToken> tokens, final String text) {
        return EbnfAlternativeParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfCommentParserToken}
     */
    public static EbnfCommentParserToken comment(final String value, final String text) {
        return EbnfCommentParserToken.with(value, text);
    }

    /**
     * {@see EbnfConcatenationParserToken}
     */
    public static EbnfConcatenationParserToken concatenation(final List<ParserToken> tokens, final String text) {
        return EbnfConcatenationParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfExceptionParserToken}
     */
    public static EbnfExceptionParserToken exception(final List<ParserToken> tokens, final String text) {
        return EbnfExceptionParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfGrammarParserToken}
     */
    public static EbnfGrammarParserToken grammar(final List<ParserToken> tokens, final String text) {
        return EbnfGrammarParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfGroupParserToken}
     */
    public static EbnfGroupParserToken group(final List<ParserToken> tokens, final String text) {
        return EbnfGroupParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfIdentifierParserToken}
     */
    public static EbnfIdentifierParserToken identifier(final EbnfIdentifierName value, final String text) {
        return EbnfIdentifierParserToken.with(value, text);
    }

    /**
     * {@see EbnfOptionalParserToken}
     */
    public static EbnfOptionalParserToken optional(final List<ParserToken> tokens, final String text) {
        return EbnfOptionalParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfRangeParserToken}
     */
    public static EbnfRangeParserToken range(final List<ParserToken> tokens, final String text) {
        return EbnfRangeParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfRepeatedParserToken}
     */
    public static EbnfRepeatedParserToken repeated(final List<ParserToken> tokens, final String text) {
        return EbnfRepeatedParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfRuleParserToken}
     */
    public static EbnfRuleParserToken rule(final List<ParserToken> tokens, final String text) {
        return EbnfRuleParserToken.with(tokens, text);
    }

    /**
     * {@see EbnfSymbolParserToken}
     */
    public static EbnfSymbolParserToken symbol(final String value, final String text) {
        return EbnfSymbolParserToken.with(value, text);
    }

    /**
     * {@see EbnfTerminalParserToken}
     */
    public static EbnfTerminalParserToken terminal(final String value, final String text) {
        return EbnfTerminalParserToken.with(value, text);
    }

    /**
     * {@see EbnfWhitespaceParserToken}
     */
    public static EbnfWhitespaceParserToken whitespace(final String value, final String text) {
        return EbnfWhitespaceParserToken.with(value, text);
    }

    static List<ParserToken> copyAndCheckTokens(final List<ParserToken> tokens) {
        Objects.requireNonNull(tokens, "tokens");

        final List<ParserToken> copy = Lists.immutable(tokens);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Tokens is empty");
        }
        return copy;
    }

    static String checkText(final String text) {
        return Whitespace.failIfNullOrEmptyOrWhitespace(text, "text");
    }

    /**
     * {@see EbnfGrammarParser}
     */
    public static Parser<EbnfParserContext> grammarParser() {
        return EbnfGrammarParser.GRAMMAR;
    }

    /**
     * Package private ctor to limit sub classing.
     */
    EbnfParserToken(final String text) {
        this.text = text;
    }

    @Override
    public final String text() {
        return this.text;
    }

    private final String text;

    /**
     * Value getter, used within equals.
     */
    abstract Object value();

    @Override
    public final boolean isLeaf() {
        return this instanceof EbnfLeafParserToken;
    }

    @Override
    public final boolean isParent() {
        return false == this.isLeaf();
    }

    // isXXX............................................................................................................

    /**
     * Only alternative tokens return true
     */
    public final boolean isAlternative() {
        return this instanceof EbnfAlternativeParserToken;
    }

    /**
     * Only comment tokens return true
     */
    public final boolean isComment() {
        return this instanceof EbnfCommentParserToken;
    }

    /**
     * Only concatenation tokens return true
     */
    public final boolean isConcatenation() {
        return this instanceof EbnfConcatenationParserToken;
    }

    /**
     * Only exception tokens return true
     */
    public final boolean isException() {
        return this instanceof EbnfExceptionParserToken;
    }

    /**
     * Only grouping tokens return true
     */
    public final boolean isGroup() {
        return this instanceof EbnfGroupParserToken;
    }

    /**
     * Only grammar tokens return true
     */
    public final boolean isGrammar() {
        return this instanceof EbnfGrammarParserToken;
    }

    /**
     * Only identifiers return true
     */
    public final boolean isIdentifier() {
        return this instanceof EbnfIdentifierParserToken;
    }

    /**
     * Only optional tokens return true
     */
    public final boolean isOptional() {
        return this instanceof EbnfOptionalParserToken;
    }

    /**
     * Only range tokens return true
     */
    public final boolean isRange() {
        return this instanceof EbnfRangeParserToken;
    }

    /**
     * Only repeating tokens return true
     */
    public final boolean isRepeated() {
        return this instanceof EbnfRepeatedParserToken;
    }

    /**
     * Only rule tokens return true
     */
    public final boolean isRule() {
        return this instanceof EbnfRuleParserToken;
    }

    /**
     * Only symbols tokens return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof EbnfSymbolParserToken || this.isWhitespace();
    }

    /**
     * Only terminals return true
     */
    public final boolean isTerminal() {
        return this instanceof EbnfTerminalParserToken;
    }

    /**
     * Only {@link EbnfWhitespaceParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof EbnfWhitespaceParserToken;
    }

    // EbnfParserTokenVisitor............................................................................................

    @Override
    public final void accept(final ParserTokenVisitor visitor) {
        if (visitor instanceof EbnfParserTokenVisitor) {
            final EbnfParserTokenVisitor visitor2 = Cast.to(visitor);

            if (Visiting.CONTINUE == visitor2.startVisit(this)) {
                this.accept(visitor2);
            }
            visitor2.endVisit(this);
        }
    }

    abstract void accept(final EbnfParserTokenVisitor visitor);

    // Object ...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.text, this.value());
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                null != other && this.getClass() == other.getClass() && this.equals0((EbnfParserToken) other);
    }

    private boolean equals0(final EbnfParserToken other) {
        return this.text.equals(other.text) &&
                this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }
}
