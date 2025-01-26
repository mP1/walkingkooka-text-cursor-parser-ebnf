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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GrammarEbnfParserTokenDuplicateIdentifiersExceptionTest implements ClassTesting<GrammarEbnfParserTokenDuplicateIdentifiersException>,
        ThrowableTesting,
        ToStringTesting<GrammarEbnfParserTokenDuplicateIdentifiersException> {

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testWithNullDuplicatesFails() {
        assertThrows(NullPointerException.class, () -> new GrammarEbnfParserTokenDuplicateIdentifiersException("message 123", null));
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testWithEmptyDuplicatesFails() {
        assertThrows(IllegalArgumentException.class, () -> new GrammarEbnfParserTokenDuplicateIdentifiersException("message 123", Sets.empty()));
    }

    @Test
    public void testWith() {
        final String message = "message 123";
        final Set<RuleEbnfParserToken> duplicates = this.duplicates();
        final GrammarEbnfParserTokenDuplicateIdentifiersException exception = new GrammarEbnfParserTokenDuplicateIdentifiersException(message, duplicates);
        checkMessage(exception, message);
        this.checkEquals(duplicates, exception.duplicates(), "duplicates");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(new GrammarEbnfParserTokenDuplicateIdentifiersException("abc 123", this.duplicates()),
                "Unknown duplicates=[abc]");
    }

    private Set<RuleEbnfParserToken> duplicates() {
        return Sets.of(
                RuleEbnfParserToken.with(Lists.of(EbnfParserToken.identifier(EbnfIdentifierName.with("abc"), "abc"), EbnfParserToken.terminal("def", "def")),
                        "abc"));
    }

    @Override
    public Class<GrammarEbnfParserTokenDuplicateIdentifiersException> type() {
        return GrammarEbnfParserTokenDuplicateIdentifiersException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
