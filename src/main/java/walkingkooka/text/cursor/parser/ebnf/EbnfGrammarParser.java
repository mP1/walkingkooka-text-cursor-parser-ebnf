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

import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.RequiredParser;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A parser that accepts a grammar and returns a {@link EbnfGrammarParserToken}.
 * <br>
 * <a href="https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form">EBNF</a>
 */
final class EbnfGrammarParser implements Parser<EbnfParserContext>,
        RequiredParser<EbnfParserContext> {

    /**
     * This needs to be initialized before references below to avoid forward reference problems.
     */
    private static final Parser<EbnfParserContext> WHITESPACE_OR_COMMENT = whitespaceOrComment();

    private static Parser<EbnfParserContext> whitespaceOrComment() {
        final Parser<EbnfParserContext> whitespace = Parsers.<EbnfParserContext>charPredicateString(CharPredicates.whitespace(), 1, Integer.MAX_VALUE)
                .transform(EbnfGrammarParser::transformWhitespace)
                .setToString("WHITESPACE");

        final Parser<EbnfParserContext> comment = Parsers.<EbnfParserContext>surround(
                EbnfGrammar.COMMENT_OPEN,
                        EbnfGrammar.COMMENT_CLOSE
                ).transform(EbnfGrammarParser::transformComment)
                .setToString("COMMENT");

        return whitespace.or(comment)
                .repeating();
    }

    private static ParserToken transformWhitespace(final ParserToken token, ParserContext context) {
        return EbnfWhitespaceParserToken.with(((StringParserToken) token).value(), token.text());
    }

    private static ParserToken transformComment(final ParserToken token, ParserContext context) {
        return EbnfCommentParserToken.with(((StringParserToken) token).value(), token.text());
    }

    /**
     * <pre>
     * identifier = letter , { letter | digit | "_" } ;
     * </pre>
     */
    final static Parser<EbnfParserContext> IDENTIFIER = Parsers.character(EbnfIdentifierName.INITIAL)
                .and(
                        Parsers.character(EbnfIdentifierName.PART)
                                .repeating()
                                .orReport(
                                        ParserReporters.basic()
                                )
                ).transform(EbnfGrammarParserIdentifierParserTokenVisitor::ebnfIdentifierParserToken)
                .setToString("IDENTIFIER")
            .cast();

    /**
     * <pre>
     * terminal = "'" , character , { character } , "'"
     *          | '"' , character , { character } , '"' ;
     * </pre>
     * The above definition isnt actually correct, a terminal must be either single or quoted, and supports backslash, and unicode sequences within.
     */
    final static Parser<EbnfParserContext> TERMINAL = EbnfTerminalParser.INSTANCE;

    /**
     * <pre>
     * lhs = identifier ;
     * </pre>
     */
    final static Parser<EbnfParserContext> LHS = IDENTIFIER;

    /**
     * <pre>
     * rhs = identifier
     *      | terminal
     *      | "[" , rhs , "]"/
     *      | "{" , rhs , "}"
     *      | "(" , rhs , ")"
     *      | rhs , "|" , rhs
     *      | rhs , "," , rhs ;
     * </pre>
     */
    static final Parser<EbnfParserContext> RHS = new Parser<>() {

        @Override
        public Optional<ParserToken> parse(final TextCursor cursor,
                                           final EbnfParserContext context) {
            return rhs().parse(cursor, context);
        }

        @Override
        public int minCount() {
            return rhs()
                    .minCount();
        }

        @Override
        public int maxCount() {
            return rhs()
                    .maxCount();
        }

        public String toString() {
            return "rhs";
        }
    };

    /**
     * <pre>
     * "[" , rhs , "]"
     * </pre>
     */
    final static Parser<EbnfParserContext> OPTIONAL = optionalParser();

    private static Parser<EbnfParserContext> optionalParser() {
        final Parser<EbnfParserContext> open = symbol(
                EbnfGrammar.OPTIONAL_OPEN,
                "optional_open"
        );
        final Parser<EbnfParserContext> close = symbol(
                EbnfGrammar.OPTIONAL_CLOSE,
                "optional_close"
        );

        return open.and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(close)
                .transform(filterAndWrap(EbnfParserToken::optional));
    }

    /**
     * <pre>
     * "{" , rhs , "}"
     * </pre>
     */
    final static Parser<EbnfParserContext> REPETITION = repetition();

    private static Parser<EbnfParserContext> repetition() {
        final Parser<EbnfParserContext> open = symbol(
                EbnfGrammar.REPEATITION_OPEN,
                "repetition_open"
        );
        final Parser<EbnfParserContext> close = symbol(
                EbnfGrammar.REPEATITION_CLOSE,
                "repetition_close"
        );

        return open.and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(close)
                .transform(filterAndWrap(EbnfParserToken::repeated));
    }

    /**
     * <pre>
     * "(" , rhs , ")"
     * </pre>
     */
    final static Parser<EbnfParserContext> GROUPING = group();

    private static Parser<EbnfParserContext> group() {
        final Parser<EbnfParserContext> open = symbol(
                EbnfGrammar.GROUP_OPEN,
                "group_open"
        );
        final Parser<EbnfParserContext> close = symbol(
                EbnfGrammar.GROUP_CLOSE,
                "group_close"
        );

        return open
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(close)
                .transform(filterAndWrap(EbnfParserToken::group));
    }

    /**
     * <pre>
     * "(" , rhs , ")"
     * </pre>
     */
    final static Parser<EbnfParserContext> RHS2 = IDENTIFIER
            .or(OPTIONAL)
            .or(REPETITION)
            .or(GROUPING)
            .or(TERMINAL)
            .orReport(ParserReporters.basic());

    /**
     * <pre>
     * rhs , "|" , rhs
     * </pre>
     * To avoid left recursion problems the first rhs is replaced as RHS2
     */
    final static Parser<EbnfParserContext> ALTERNATIVE = alternative();

    private static Parser<EbnfParserContext> alternative() {
        final Parser<EbnfParserContext> separator = symbol(
                EbnfGrammar.ALTERNATIVE,
                "alt_separator"
        );

        final Parser<EbnfParserContext> required = WHITESPACE_OR_COMMENT.optional()
                .and(RHS2)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2);

        final Parser<EbnfParserContext> optionalRepeating = WHITESPACE_OR_COMMENT.optional()
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2)
                .repeating();

        return required
                .and(optionalRepeating)
                .transform(filterAndWrap(EbnfParserToken::alternative));
    }

    /**
     * <pre>
     * | rhs , "," , rhs ;
     * </pre>
     * To avoid left recursion problems the first rhs is replaced as RHS2
     */
    final static Parser<EbnfParserContext> CONCATENATION = concatenation();

    private static Parser<EbnfParserContext> concatenation() {
        final Parser<EbnfParserContext> separator = symbol(
                EbnfGrammar.CONCATENATION,
                "concat_separator"
        );

        final Parser<EbnfParserContext> required = WHITESPACE_OR_COMMENT.optional()
                .and(RHS2)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2);

        final Parser<EbnfParserContext> optionalRepeating = WHITESPACE_OR_COMMENT.optional()
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2)
                .repeating();

        return required.and(optionalRepeating)
                .transform(filterAndWrap(EbnfParserToken::concatenation));
    }

    /**
     * <pre>
     * "-" , rhs
     * </pre>
     */
    final static Parser<EbnfParserContext> EXCEPTION = exception();

    private static Parser<EbnfParserContext> exception() {
        final Parser<EbnfParserContext> separator = symbol(
                EbnfGrammar.EXCEPTION,
                "exception_separator"
        );

        return WHITESPACE_OR_COMMENT.optional()
                .and(RHS2)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2)
                .transform(
                        filterAndWrap(EbnfParserToken::exception)
                );
    }

    /**
     * <pre>
     * range = terminal, '..', terminal
     * </pre>
     */
    final static Parser<EbnfParserContext> RANGE = range();

    private static Parser<EbnfParserContext> range() {
        final Parser<EbnfParserContext> separator = symbol(
                EbnfGrammar.RANGE,
                "range_separator"
        );

        return WHITESPACE_OR_COMMENT.optional()
                .and(RHS2)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(separator)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS2)
                .transform(
                        filterAndWrap(EbnfParserToken::range)
                );
    }

    /**
     * <pre>
     * lhs , "=" , rhs , ";" ;
     * </pre>
     */
    final static Parser<EbnfParserContext> RULE = rule();

    private static Parser<EbnfParserContext> rule() {
        final Parser<EbnfParserContext> assign = symbol(
                EbnfGrammar.ASSIGN,
                "assign"
        ).orReport(ParserReporters.basic());
        final Parser<EbnfParserContext> termination = symbol(
                EbnfGrammar.TERMINATION,
                "termination"
        ).orReport(ParserReporters.basic());

        return WHITESPACE_OR_COMMENT.optional()
                .and(LHS)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(assign)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(RHS)
                .and(WHITESPACE_OR_COMMENT.optional())
                .and(termination)
                .transform(
                        filterAndWrap(EbnfParserToken::rule)
                );
    }

    /**
     * Matches any of the tokens, assumes that any leading or trailing whitespace or comments is handled elsewhere...(parent)
     */
    private static Parser<EbnfParserContext> rhs() {
        if (null == RHS_CACHE) {
            RHS_CACHE = ALTERNATIVE
                    .or(CONCATENATION)
                    .or(OPTIONAL)
                    .or(REPETITION)
                    .or(GROUPING)
                    .or(RANGE) // must be before TERMINAL
                    .or(EXCEPTION)
                    .or(IDENTIFIER) // identifier & terminal are atoms of range, exception, alt and concat and must come after
                    .or(TERMINAL)
                    .orReport(ParserReporters.basic());
        }
        return RHS_CACHE;
    }

    private static Parser<EbnfParserContext> RHS_CACHE;

    /**
     * Creates a parser that matches the given character(s) and wraps it inside a {@link EbnfSymbolParserToken}
     */
    private static Parser<EbnfParserContext> symbol(final String symbol,
                                                    final String name) {
        Parser<EbnfParserContext> parser;

        switch (symbol.length()) {
            case 0:
                throw new IllegalArgumentException("Symbol " + symbol + " is empty");
            case 1:
                parser = Parsers.character(CharPredicates.is(symbol.charAt(0)))
                        .transform(EbnfGrammarParser::transformSymbolCharacter)
                        .setToString(name)
                        .cast();
                break;
            default:
                parser = Parsers.string(symbol, CaseSensitivity.SENSITIVE)
                        .transform(EbnfGrammarParser::transformSymbolString)
                        .setToString(name)
                        .cast();
                break;
        }

        return parser;
    }

    private static ParserToken transformSymbolCharacter(final ParserToken token,
                                                        final ParserContext context) {
        return EbnfSymbolParserToken.with(
                ((CharacterParserToken) token)
                        .value()
                        .toString(),
                token.text()
        );
    }

    private static ParserToken transformSymbolString(final ParserToken token,
                                                     final ParserContext context) {
        return EbnfSymbolParserToken.with(
                ((StringParserToken) token)
                        .value(),
                token.text()
        );
    }

    private static BiFunction<ParserToken, EbnfParserContext, ParserToken> filterAndWrap(final BiFunction<List<ParserToken>, String, ParserToken> wrapper) {
        return (token, context) -> EbnfGrammarParserWrapperParserTokenVisitor.wrap(token, wrapper);
    }

    /**
     * <pre>
     * grammar = { rule } ;
     * </pre>
     */
    final static Parser<EbnfParserContext> GRAMMAR = grammar();

    private static Parser<EbnfParserContext> grammar() {
        return WHITESPACE_OR_COMMENT.optional()
                .and(RULE.orReport(ParserReporters.basic()))
                .and(RULE.repeating().optional())
                .and(WHITESPACE_OR_COMMENT.optional())
                .transform(EbnfGrammarParser::grammarParserToken);
    }

    private static EbnfGrammarParserToken grammarParserToken(final ParserToken sequence, final EbnfParserContext context) {
        return EbnfGrammarParserToken.with(((SequenceParserToken) sequence).flat()
                        .value(),
                sequence.text());
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final EbnfParserContext context) {
        return GRAMMAR.parse(cursor, context);
    }

    @Override
    public String toString() {
        return GRAMMAR.toString();
    }
}
