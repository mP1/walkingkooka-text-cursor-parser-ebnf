/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testEbnfParserTokenParse() {
        final String grammar = "TEST1=\"abc\";";

        this.checkEquals(
                EbnfParserToken.grammar(
                        Lists.of(
                                EbnfParserToken.rule(
                                        Lists.<ParserToken>of( // type param required by gwtc
                                                EbnfParserToken.identifier(
                                                        EbnfIdentifierName.with("TEST1"),
                                                        "TEST1"
                                                ),
                                                EbnfParserToken.symbol("=", "="),
                                                EbnfParserToken.terminal("abc", "\"abc\""),
                                                EbnfParserToken.symbol(";", ";")
                                        ),
                                        grammar
                                )
                        ),
                        grammar
                ),
                EbnfParserToken.parse(grammar)
        );
    }

    private void checkEquals(final Object expected,
                             final Object actual) {
        Assert.assertEquals(
                expected,
                actual
        );
    }
}
