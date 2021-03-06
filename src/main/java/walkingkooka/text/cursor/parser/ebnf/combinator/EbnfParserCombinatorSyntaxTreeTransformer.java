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

import walkingkooka.Context;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfConcatenationParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfExceptionParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGroupParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfOptionalParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRepeatedParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;

/**
 * The {@link Context} that provides callbacks for a grammar that defines multiple {@link Parser parses}.
 * <br>
 * Note if a different parser object is returned by {@link #terminal(EbnfTerminalParserToken, Parser)},
 * it will be ignored by {@link #range(EbnfRangeParserToken, Parser)} which reads the
 * tokens from the range token.
 */
public interface EbnfParserCombinatorSyntaxTreeTransformer<C extends ParserContext> extends Context {

    Parser<C> alternatives(final EbnfAlternativeParserToken token, final Parser<C> parser);

    Parser<C> concatenation(final EbnfConcatenationParserToken token, final Parser<C> parser);

    Parser<C> exception(final EbnfExceptionParserToken token, final Parser<C> parser);

    Parser<C> group(final EbnfGroupParserToken token, final Parser<C> parser);

    Parser<C> identifier(final EbnfIdentifierParserToken token, final Parser<C> parser);

    Parser<C> optional(final EbnfOptionalParserToken token, final Parser<C> parser);

    Parser<C> range(final EbnfRangeParserToken token, final Parser<C> parser);

    Parser<C> repeated(final EbnfRepeatedParserToken token, final Parser<C> parser);

    Parser<C> terminal(final EbnfTerminalParserToken token, final Parser<C> parser);
}
