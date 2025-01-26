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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTesting2;

public abstract class EbnfParserTestCase<T extends EbnfParserToken> implements ParserTesting2<Parser<EbnfParserContext>, EbnfParserContext> {

    final static String COMMENT1 = "(*comment-1*)";
    final static String COMMENT2 = "(*comment-2*)";

    final static String IDENTIFIER1_TEXT = "abc123";
    final static String IDENTIFIER2_TEXT = "def456";
    final static String IDENTIFIER3_TEXT = "ghi789";

    final static EbnfIdentifierName IDENTIFIER1 = EbnfIdentifierName.with(IDENTIFIER1_TEXT);
    final static EbnfIdentifierName IDENTIFIER2 = EbnfIdentifierName.with(IDENTIFIER2_TEXT);
    final static EbnfIdentifierName IDENTIFIER3 = EbnfIdentifierName.with(IDENTIFIER3_TEXT);

    final static String WHITESPACE1 = " ";
    final static String WHITESPACE2 = "   ";

    final static String TERMINAL1 = "terminal456";
    final static String TERMINAL1_TEXT = '"' + TERMINAL1 + '"';

    final static String TERMINAL2 = "terminal789";
    final static String TERMINAL2_TEXT = '"' + TERMINAL2 + '"';

    final static String ASSIGNMENT = "=";
    final static String TERMINATOR = ";";
    final static String CONCAT = ",";
    final static String ALTERNATIVE = "|";

    final static String BETWEEN = "..";

    final static String EXCEPTION = "-";

    final static String OPEN_GROUP = "(";
    final static String CLOSE_GROUP = ")";

    final static String OPEN_OPTIONAL = "[";
    final static String CLOSE_OPTIONAL = "]";

    final static String OPEN_REPEAT = "{";
    final static String CLOSE_REPEAT = "}";

    EbnfParserTestCase() {
        super();
    }

    @Override
    public final EbnfParserContext createContext() {
        return EbnfParserContexts.basic();
    }

    final EbnfParserToken comment1() {
        return EbnfParserToken.comment(COMMENT1, COMMENT1);
    }

    final EbnfParserToken comment2() {
        return EbnfParserToken.comment(COMMENT2, COMMENT2);
    }

    final EbnfParserToken identifier1() {
        return EbnfParserToken.identifier(IDENTIFIER1, IDENTIFIER1_TEXT);
    }

    final EbnfParserToken identifier2() {
        return EbnfParserToken.identifier(IDENTIFIER2, IDENTIFIER2_TEXT);
    }

    final EbnfParserToken identifier3() {
        return EbnfParserToken.identifier(IDENTIFIER3, IDENTIFIER3_TEXT);
    }

    final EbnfParserToken whitespace1() {
        return EbnfParserToken.whitespace(WHITESPACE1, WHITESPACE1);
    }

    final EbnfParserToken whitespace2() {
        return EbnfParserToken.whitespace(WHITESPACE2, WHITESPACE2);
    }

    static SymbolEbnfParserToken symbol(final String symbol) {
        return EbnfParserToken.symbol(symbol, symbol);
    }

    static RuleEbnfParserToken rule(final String text, final EbnfParserToken... tokens) {
        return EbnfParserToken.rule(Lists.of(tokens), text);
    }

    static RangeEbnfParserToken range(final String text, final EbnfParserToken... tokens) {
        return EbnfParserToken.range(Lists.of(tokens), text);
    }

    static SymbolEbnfParserToken assignmentToken() {
        return symbol(ASSIGNMENT);
    }

    static SymbolEbnfParserToken altToken() {
        return symbol(ALTERNATIVE);
    }

    static SymbolEbnfParserToken concatToken() {
        return symbol(CONCAT);
    }

    static SymbolEbnfParserToken exceptionToken() {
        return symbol(EXCEPTION);
    }

    static TerminalEbnfParserToken terminal1() {
        return EbnfParserToken.terminal(TERMINAL1, TERMINAL1_TEXT);
    }

    static TerminalEbnfParserToken terminal2() {
        return EbnfParserToken.terminal(TERMINAL2, TERMINAL2_TEXT);
    }

    static SymbolEbnfParserToken terminatorToken() {
        return symbol(";");
    }

    static SymbolEbnfParserToken between() {
        return symbol(BETWEEN);
    }

    static SymbolEbnfParserToken openGroupToken() {
        return symbol(OPEN_GROUP);
    }

    static SymbolEbnfParserToken closeGroupToken() {
        return symbol(CLOSE_GROUP);
    }

    static SymbolEbnfParserToken openOptionalToken() {
        return symbol(OPEN_OPTIONAL);
    }

    static SymbolEbnfParserToken closeOptionalToken() {
        return symbol(CLOSE_OPTIONAL);
    }

    static SymbolEbnfParserToken openRepeatToken() {
        return symbol(OPEN_REPEAT);
    }

    static SymbolEbnfParserToken closeRepeatToken() {
        return symbol(CLOSE_REPEAT);
    }

}
