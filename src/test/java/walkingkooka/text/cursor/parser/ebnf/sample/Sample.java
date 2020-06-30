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

package walkingkooka.text.cursor.parser.ebnf.sample;

import org.junit.jupiter.api.Assertions;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Optional;

public final class Sample {
    public static void main(final String[] args) throws Exception {
        final String grammar = "TEST1=\"abc\";";

        final TextCursor grammarFile = TextCursors.charSequence(grammar);
        final Optional<ParserToken> parsed = EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(grammarFile, EbnfParserContexts.basic());

        final EbnfParserToken token = EbnfParserToken.grammar(Lists.of(EbnfParserToken.rule(Lists.of(EbnfParserToken.identifier(EbnfIdentifierName.with("TEST1"), "TEST1"),
                EbnfParserToken.symbol("=", "="),
                EbnfParserToken.terminal("abc", "\"abc\""),
                EbnfParserToken.symbol(";", ";")),
                grammar)), grammar);

        Assertions.assertEquals(Optional.of(token), parsed);
    }
}
