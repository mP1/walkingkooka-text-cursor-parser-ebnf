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
import walkingkooka.text.cursor.parser.ebnf.AlternativeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.ConcatenationEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.ExceptionEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.GroupEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.IdentifierEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.OptionalEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RangeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RepeatedEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RuleEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.TerminalEbnfParserToken;

/**
 * The {@link Context} that provides callbacks for a grammar that defines multiple {@link Parser parses}.
 * <br>
 * Note if a different parser object is returned by {@link #terminal(TerminalEbnfParserToken, Parser)},
 * it will be ignored by {@link #range(RangeEbnfParserToken, String, String)} which reads the
 * tokens from the range token.
 * <br>
 * Note the {@link EbnfParserToken#toString()} may be set upon the {@link Parser} if the text definition from the grammar
 * file should be kept.
 */
public interface EbnfParserCombinatorGrammarTransformer<C extends ParserContext> extends Context {

    Parser<C> alternatives(final AlternativeEbnfParserToken token, final Parser<C> parser);

    Parser<C> concatenation(final ConcatenationEbnfParserToken token, final Parser<C> parser);

    Parser<C> exception(final ExceptionEbnfParserToken token, final Parser<C> parser);

    Parser<C> group(final GroupEbnfParserToken token, final Parser<C> parser);

    Parser<C> identifier(final IdentifierEbnfParserToken token, final Parser<C> parser);

    Parser<C> optional(final OptionalEbnfParserToken token, final Parser<C> parser);

    Parser<C> range(final RangeEbnfParserToken token,
                    final String beginText,
                    final String endText);

    Parser<C> repeated(final RepeatedEbnfParserToken token, final Parser<C> parser);

    Parser<C> rule(final RuleEbnfParserToken token, final Parser<C> parser);

    Parser<C> terminal(final TerminalEbnfParserToken token, final Parser<C> parser);
}
