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

package walkingkooka.text.cursor.parser.ebnf.combinator;

import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserBuilder;
import walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfConcatenationParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfExceptionParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGroupParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfOptionalParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserTokenVisitor;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRepeatedParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRuleParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Transforms all {@link EbnfParserToken} into {@link EbnfParserCombinatorsProxy}.
 */
final class EbnfParserCombinatorsTransformEbnfParserTokenVisitor<C extends ParserContext> extends EbnfParserTokenVisitor {

    static <C extends ParserContext> void transform(final EbnfParserToken token,
                                                    final EbnfParserCombinatorsProxy<C> proxy,
                                                    final EbnfParserCombinatorContext<C> context) {
        final EbnfParserCombinatorsTransformEbnfParserTokenVisitor<C> visitor = new EbnfParserCombinatorsTransformEbnfParserTokenVisitor<>(
                proxy,
                context
        );
        visitor.accept(token);
    }

    // @VisibleForTesting
    EbnfParserCombinatorsTransformEbnfParserTokenVisitor(final EbnfParserCombinatorsProxy<C> proxy,
                                                         final EbnfParserCombinatorContext<C> context) {
        super();

        this.proxy = proxy;
        this.context = context;
    }

    // GRAMMAR..........................................................................................................

    @Override
    protected Visiting startVisit(final EbnfGrammarParserToken token) {
        throw new UnsupportedOperationException(token.toString());
    }

    // RULE ............................................................................................................

    @Override
    protected Visiting startVisit(final EbnfRuleParserToken token) {
        final EbnfParserToken assignment = token.assignment();

        this.tryCreateAndTransformParser(
                token,
                (final EbnfRuleParserToken rule) -> {
                    Parser<C> parser = null;

                    final EbnfParserCombinatorsProxyGet<C> ruleGot = this.context.proxy(rule);
                    if (null == ruleGot.proxy.parser) {
                        final EbnfParserCombinatorsProxyGet<C> assignmentGot = this.context.proxy(assignment);
                        parser = assignmentGot.proxy.parser;
                    }

                    return Optional.ofNullable(parser);
                }, // parser
                this.context.transformer::rule
        );

        return Visiting.SKIP;
    }

    // ALT .............................................................................................................

    @Override
    protected Visiting startVisit(final EbnfAlternativeParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfAlternativeParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                (final EbnfAlternativeParserToken a) -> this.proxy.childParsers()
                        .map(this::alternatives), // parser
                this.context.transformer::alternatives
        );
    }

    private Parser<C> alternatives(final List<Parser<C>> parsers) {
        final StringBuilder b = new StringBuilder();
        int count = 0;
        String separator = "";

        for (final Parser<C> parser : parsers) {
            if (parser instanceof EbnfParserCombinatorOptionalParser) {
                count++;

                b.append(separator);
                b.append(parser);

                separator = ", ";
            }
        }

        if (count > 0) {
            throw new EbnfParserCombinatorException("Alternatives given " + count + " optional(s) expected 0 got " + b);
        }

        return Parsers.alternatives(parsers);
    }

    // CONCAT ..........................................................................................................

    @Override
    protected Visiting startVisit(final EbnfConcatenationParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfConcatenationParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                (c) -> {
                    SequenceParserBuilder<C> b = Parsers.sequenceParserBuilder();

                    for (final EbnfParserCombinatorsProxy<C> child : this.proxy.children) {
                        final Optional<Parser<C>> maybeChildParser = child.parser();
                        if (maybeChildParser.isPresent()) {
                            final Parser<C> childParser = maybeChildParser.get();
                            if (childParser instanceof EbnfParserCombinatorOptionalParser) {
                                b.optional(
                                        EbnfParserCombinatorOptionalParser.unwrapIfNecessary(childParser)
                                );
                            } else {
                                b.required(childParser);
                            }
                        } else {
                            b = null;
                            break;
                        }
                    }

                    return Optional.ofNullable(
                            null != b ?
                                    b.build() :
                                    null
                    );
                }, // parser
                this.context.transformer::concatenation
        );
    }

    // EXCEPTION ........................................................................................................

    @Override
    protected Visiting startVisit(final EbnfExceptionParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfExceptionParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                this::exceptionParser, // parser
                this.context.transformer::exception
        );
    }

    private Optional<Parser<C>> exceptionParser(final EbnfExceptionParserToken token) {
        Parser<C> parsers = null;

        final List<Parser<C>> childParsers = this.proxy.childParsers()
                .orElse(null);
        if (null != childParsers) {
            final int count = childParsers.size();
            switch (count) {
                case 2:
                    final Parser<C> left = childParsers.get(0);
                    if (left instanceof EbnfParserCombinatorOptionalParser) {
                        throw new EbnfParserCombinatorException("Exception left must not be optional got " + token.token());
                    }
                    final Parser<C> right = childParsers.get(1);
                    if (right instanceof EbnfParserCombinatorOptionalParser) {
                        throw new EbnfParserCombinatorException("Exception right must not be optional got " + token.token());
                    }

                    parsers = Parsers.andNot(
                            left,
                            right
                    );
                    break;
                default:
                    throw new EbnfParserCombinatorException("Exception got " + count + " expected 2 parsers for " + this.proxy.token);
            }
        }

        return Optional.ofNullable(parsers);
    }

    // GROUP ...........................................................................................................

    @Override
    protected Visiting startVisit(final EbnfGroupParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfGroupParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                t -> this.firstChildParser(),
                this.context.transformer::group
        );
    }

    // OPT .............................................................................................................

    @Override
    protected Visiting startVisit(final EbnfOptionalParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfOptionalParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                t -> this.firstChildParser(),
                this.context.transformer::optional
        );
    }

    // REPEAT ..........................................................................................................

    @Override
    protected Visiting startVisit(final EbnfRepeatedParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfRepeatedParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                (t) -> this.firstChildParser()
                        .map(Parser::repeating), // parser
                this.context.transformer::repeated
        );
    }

    // IDENTIFIER ......................................................................................................

    @Override
    protected void visit(final EbnfIdentifierParserToken token) {
        this.tryCreateAndTransformParser(
                token,
                this.context::tryIdentifierParser, // parser
                this.context.transformer::identifier
        );
    }

    // TERMINAL ........................................................................................................

    /**
     * The {@link EbnfParserCombinatorsProxy#parser} for this token should have already been created by {@link EbnfParserCombinatorsPrepareEbnfParserTokenVisitor#visit(EbnfTerminalParserToken)}.
     */
    @Override
    protected void visit(final EbnfTerminalParserToken token) {
        throw new EbnfParserCombinatorException("Terminal should already have parser created in prepare phase=" + token);
    }

    private Optional<Parser<C>> firstChildParser() {
        return this.proxy.firstChildParser();
    }

    private <T extends EbnfParserToken> boolean tryCreateAndTransformParser(final T token,
                                                                            final Function<T, Optional<Parser<C>>> parserProvider,
                                                                            final BiFunction<T, Parser<C>, Parser<C>> transformer) {
        boolean set = false;

        if (null == this.proxy.parser) {
            final Optional<Parser<C>> maybeParser = parserProvider.apply(token);
            if (maybeParser.isPresent()) {

                Parser<C> parser = maybeParser.get();
                final boolean isOptional = parser instanceof EbnfParserCombinatorOptionalParser;
                parser = transformer.apply(
                        token,
                        EbnfParserCombinatorOptionalParser.unwrapIfNecessary(parser)
                );
                if (token.isOptional() || isOptional) {
                    parser = EbnfParserCombinatorOptionalParser.with(
                            parser,
                            token
                    );
                }

                this.proxy.setParser(parser);
                set = true;
            }
        }

        return set;
    }

    // RANGE ...........................................................................................................

    // range is different & and has a different endVisit
    @Override
    protected Visiting startVisit(final EbnfRangeParserToken token) {
        return Visiting.SKIP; // dont visit children
    }

    @Override
    protected void endVisit(final EbnfRangeParserToken token) {
        final EbnfParserCombinatorContext<C> context = this.context;

        final Optional<String> maybeBeginText = context.terminal(
                token,
                true // range-begin
        );
        if (maybeBeginText.isPresent()) {
            final Optional<String> maybeEndText = context.terminal(
                    token,
                    false // range-end
            );
            if (maybeEndText.isPresent()) {
                final String beginText = maybeBeginText.get();
                final String endText = maybeEndText.get();

                this.proxy.setParser(
                        context.transformer.range(
                                token,
                                beginText,
                                endText
                        )
                );
            }
        }
    }

    /**
     * Supplies the {@link Parser} for all child proxies.
     */
    private final EbnfParserCombinatorsProxy<C> proxy;

    /**
     * Contains the parser returned by {@link EbnfParserCombinatorSyntaxTreeTransformer} visitXXX and the response
     */
//    private Parser<C> parser;

    private final EbnfParserCombinatorContext<C> context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.proxy.token + " " + this.context.toString();
    }
}
