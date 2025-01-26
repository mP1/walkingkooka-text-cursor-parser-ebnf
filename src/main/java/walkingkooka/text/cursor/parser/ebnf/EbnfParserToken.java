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
     * Parses the given EBNF grammar returning the {@link GrammarEbnfParserToken}.
     */
    public static GrammarEbnfParserToken parse(final String text) {
        return EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parseText(
                        text,
                        EbnfParserContexts.basic()
                ).cast(GrammarEbnfParserToken.class);
    }

    public static GrammarEbnfParserToken parseFile(final String text,
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
     * {@see AlternativeEbnfParserToken}
     */
    public static AlternativeEbnfParserToken alternative(final List<ParserToken> tokens, final String text) {
        return AlternativeEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see CommentEbnfParserToken}
     */
    public static CommentEbnfParserToken comment(final String value, final String text) {
        return CommentEbnfParserToken.with(value, text);
    }

    /**
     * {@see ConcatenationEbnfParserToken}
     */
    public static ConcatenationEbnfParserToken concatenation(final List<ParserToken> tokens, final String text) {
        return ConcatenationEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see ExceptionEbnfParserToken}
     */
    public static ExceptionEbnfParserToken exception(final List<ParserToken> tokens, final String text) {
        return ExceptionEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see GrammarEbnfParserToken}
     */
    public static GrammarEbnfParserToken grammar(final List<ParserToken> tokens, final String text) {
        return GrammarEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see GroupEbnfParserToken}
     */
    public static GroupEbnfParserToken group(final List<ParserToken> tokens, final String text) {
        return GroupEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see IdentifierEbnfParserToken}
     */
    public static IdentifierEbnfParserToken identifier(final EbnfIdentifierName value, final String text) {
        return IdentifierEbnfParserToken.with(value, text);
    }

    /**
     * {@see OptionalEbnfParserToken}
     */
    public static OptionalEbnfParserToken optional(final List<ParserToken> tokens, final String text) {
        return OptionalEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see RangeEbnfParserToken}
     */
    public static RangeEbnfParserToken range(final List<ParserToken> tokens, final String text) {
        return RangeEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see RepeatedEbnfParserToken}
     */
    public static RepeatedEbnfParserToken repeated(final List<ParserToken> tokens, final String text) {
        return RepeatedEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see RuleEbnfParserToken}
     */
    public static RuleEbnfParserToken rule(final List<ParserToken> tokens, final String text) {
        return RuleEbnfParserToken.with(tokens, text);
    }

    /**
     * {@see SymbolEbnfParserToken}
     */
    public static SymbolEbnfParserToken symbol(final String value, final String text) {
        return SymbolEbnfParserToken.with(value, text);
    }

    /**
     * {@see TerminalEbnfParserToken}
     */
    public static TerminalEbnfParserToken terminal(final String value, final String text) {
        return TerminalEbnfParserToken.with(value, text);
    }

    /**
     * {@see WhitespaceEbnfParserToken}
     */
    public static WhitespaceEbnfParserToken whitespace(final String value, final String text) {
        return WhitespaceEbnfParserToken.with(value, text);
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
        return this instanceof LeafEbnfParserToken;
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
        return this instanceof AlternativeEbnfParserToken;
    }

    /**
     * Only comment tokens return true
     */
    public final boolean isComment() {
        return this instanceof CommentEbnfParserToken;
    }

    /**
     * Only concatenation tokens return true
     */
    public final boolean isConcatenation() {
        return this instanceof ConcatenationEbnfParserToken;
    }

    /**
     * Only exception tokens return true
     */
    public final boolean isException() {
        return this instanceof ExceptionEbnfParserToken;
    }

    /**
     * Only grouping tokens return true
     */
    public final boolean isGroup() {
        return this instanceof GroupEbnfParserToken;
    }

    /**
     * Only grammar tokens return true
     */
    public final boolean isGrammar() {
        return this instanceof GrammarEbnfParserToken;
    }

    /**
     * Only identifiers return true
     */
    public final boolean isIdentifier() {
        return this instanceof IdentifierEbnfParserToken;
    }

    /**
     * Only optional tokens return true
     */
    public final boolean isOptional() {
        return this instanceof OptionalEbnfParserToken;
    }

    /**
     * Only range tokens return true
     */
    public final boolean isRange() {
        return this instanceof RangeEbnfParserToken;
    }

    /**
     * Only repeating tokens return true
     */
    public final boolean isRepeated() {
        return this instanceof RepeatedEbnfParserToken;
    }

    /**
     * Only rule tokens return true
     */
    public final boolean isRule() {
        return this instanceof RuleEbnfParserToken;
    }

    /**
     * Only symbols tokens return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof SymbolEbnfParserToken || this.isWhitespace();
    }

    /**
     * Only terminals return true
     */
    public final boolean isTerminal() {
        return this instanceof TerminalEbnfParserToken;
    }

    /**
     * Only {@link WhitespaceEbnfParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof WhitespaceEbnfParserToken;
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
