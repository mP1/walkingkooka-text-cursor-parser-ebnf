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

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorException;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EbnfParserTokenTest implements ParseStringTesting<EbnfParserToken>,
        ClassTesting<EbnfParserToken>,
        TreePrintableTesting {

    // parse............................................................................................................

    @Test
    public void testParseWithInvalidGrammarFails() {
        final String text = "Hello= \"text\"; Bad!";

        this.parseStringFails(
                text,
                new InvalidCharacterException(
                        text,
                        text.indexOf("!")
                ).appendToMessage("expected assign")
        );
    }

    @Test
    public void testParse() {
        final String text = "Hello=\"text\";";

        this.parseStringAndCheck(
                text,
                EbnfParserToken.grammar(
                        Lists.of(
                                EbnfParserToken.rule(
                                        Lists.of(
                                                EbnfParserToken.identifier(
                                                        EbnfIdentifierName.with("Hello"),
                                                        "Hello"
                                                ),
                                                EbnfParserToken.symbol("=", "="),
                                                EbnfParserToken.terminal(
                                                        "\"text\"",
                                                        "text"
                                                ),
                                                EbnfParserToken.symbol(";", ";")
                                        ),
                                        text
                                )
                        ),
                        text
                )
        );
    }

    @Override
    public GrammarEbnfParserToken parseString(final String text) {
        return EbnfParserToken.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // parseFile........................................................................................................

    private final static String FILENAME = "File123";

    @Test
    public void testParseFileWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> EbnfParserToken.parseFile(
                        null,
                        FILENAME
                )
        );
    }

    @Test
    public void testParseFileWithNullFilenameFails() {
        assertThrows(
                NullPointerException.class,
                () -> EbnfParserToken.parseFile(
                        "",
                        null
                )
        );
    }

    @Test
    public void testParseFileWithEmptyFilenameFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> EbnfParserToken.parseFile(
                        "",
                        ""
                )
        );
    }

    @Test
    public void testParseFileWithBadGrammarFails() {
        final EbnfParserCombinatorException thrown = assertThrows(
                EbnfParserCombinatorException.class,
                () -> EbnfParserToken.parseFile(
                        "Hello=\"123\";!",
                        FILENAME
                )
        );

        this.checkEquals(
                "Unable to parse grammar in file \"File123\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseFile() {
        final String text = "Hello=\"text\";";

        this.checkEquals(
                EbnfParserToken.grammar(
                        Lists.of(
                                EbnfParserToken.rule(
                                        Lists.of(
                                                EbnfParserToken.identifier(
                                                        EbnfIdentifierName.with("Hello"),
                                                        "Hello"
                                                ),
                                                EbnfParserToken.symbol("=", "="),
                                                EbnfParserToken.terminal(
                                                        "\"text\"",
                                                        "text"
                                                ),
                                                EbnfParserToken.symbol(";", ";")
                                        ),
                                        text
                                )
                        ),
                        text
                ),
                EbnfParserToken.parseFile(
                        text,
                        FILENAME
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<EbnfParserToken> type() {
        return EbnfParserToken.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
