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

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ParentEbnfParserTokenTestCase2<T extends ParentEbnfParserToken<T>> extends ParentEbnfParserTokenTestCase<T> {

    ParentEbnfParserTokenTestCase2() {
        super();
    }

    @Test
    public final void testWithOnlyCommentsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken(
                        this.text(),
                        this.comment1(), this.comment2()
                )
        );
    }

    @Test
    public final void testWithOnlySymbolsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken(
                        this.text(),
                        symbol("a"),
                        symbol("z")
                )
        );
    }

    @Test
    public final void testWithOnlyWhitespaceFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken(
                        this.text(),
                        this.whitespace("   "),
                        this.whitespace(" ")
                )
        );
    }

    @Test
    public final void testWithOnlyCommentsSymbolsWhitespaceFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken(
                        this.text(),
                        this.comment1(),
                        symbol("2"),
                        this.whitespace("   ")
                )
        );
    }

    @Test
    public final void testWithOnlyCommentWhitespaceFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken(
                        this.text(),
                        this.whitespace("   "),
                        this.whitespace(" ")
                )
        );
    }
}
