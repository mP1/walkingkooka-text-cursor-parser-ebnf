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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Simple example of EBNF grammar
 * <pre>
 * optional       [ token ]
 * concat         token1 , token2, token3
 * alternate      token1 | token2 | token3
 * exception      token1 - token2
 * group.         ( group )
 * repetition     { token }
 * rule           identifier = token
 * </pre>
 */
public final class EbnfParserCombinators implements PublicStaticHelper {

    /**
     * Accepts a {@link EbnfGrammarParserToken} and function that may be used to query parsers given an {@link EbnfIdentifierName}.
     */
    public static <C extends ParserContext> Function<EbnfIdentifierName, Optional<Parser<C>>> transform(final EbnfGrammarParserToken grammar,
                                                                                                        final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                                                                                        final EbnfParserCombinatorGrammarTransformer<C> transformer) {
        Objects.requireNonNull(grammar, "grammar");
        Objects.requireNonNull(identifierToParser, "identifierToParser");
        Objects.requireNonNull(transformer, "syntaxTreeTransformer");

        final EbnfParserCombinatorContext<C> context = EbnfParserCombinatorContext.with(
                identifierToParser,
                transformer
        );

        EbnfParserCombinatorsPrepareEbnfParserTokenVisitor.with(context)
                .accept(grammar);

        context.tryCreatingParsers(false); // ignoreCycles=false
        context.insertProxyParsersIfEbnfIdentifierParserToken();
        context.tryCreatingParsers(true); // ignoreCycles=true
        context.fixIdentifierToProxyWithoutParser();
        context.fixProxyParsers();

        return context.nameToParser();
    }

    /**
     * Accepts a {@link EbnfGrammarParserToken} and function that may be used to query parsers given an {@link EbnfIdentifierName}.
     */
    public static <C extends ParserContext> Function<EbnfIdentifierName, Parser<C>> transformForFile(final EbnfGrammarParserToken grammar,
                                                                                                     final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                                                                                     final EbnfParserCombinatorGrammarTransformer<C> transformer,
                                                                                                     final String filename) {
        Objects.requireNonNull(grammar, "grammar");
        Objects.requireNonNull(identifierToParser, "identifierToParser");
        Objects.requireNonNull(transformer, "syntaxTreeTransformer");
        CharSequences.failIfNullOrEmpty(filename, "filename");

        final Function<EbnfIdentifierName, Optional<Parser<C>>> parsers = transform(
                grammar,
                identifierToParser,
                transformer
        );
        return (n) -> parsers.apply(n)
                .orElseThrow(() -> new EbnfParserCombinatorException("Missing parser " + CharSequences.quoteAndEscape(n.value()) + " in " + CharSequences.quoteAndEscape(filename)));
    }

    /**
     * Stop creation
     */
    private EbnfParserCombinators() {
        throw new UnsupportedOperationException();
    }
}
