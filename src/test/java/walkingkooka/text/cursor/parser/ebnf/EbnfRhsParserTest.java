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
import walkingkooka.text.cursor.parser.Parser;

public final class EbnfRhsParserTest extends EbnfParserTestCase2<EbnfParserToken> {

    @Test
    public void testDummy() {
    }

    @Override
    public Parser<EbnfParserContext> createParser() {
        return EbnfGrammarParser.RHS;
    }

    @Override
    String text() {
        return "abc123";
    }

    @Override
    EbnfParserToken token(final String text) {
        return EbnfIdentifierParserToken.with(
                EbnfIdentifierName.with(this.text()),
                text);
    }
}
