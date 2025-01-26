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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticFactoryTesting;
import walkingkooka.text.cursor.parser.ParserTokenTesting;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class EbnfParserTokenTestCase<T extends EbnfParserToken> implements ClassTesting2<T>,
        IsMethodTesting<T>,
        ParserTokenTesting<T> {

    EbnfParserTokenTestCase() {
        super();
    }

    @Test
    @Override
    public final void testPublicStaticFactoryMethod() {
        PublicStaticFactoryTesting.checkFactoryMethods(EbnfParserToken.class,
                "",
                EbnfParserToken.class.getSimpleName(),
                this.type());
    }

    @Test
    public final void testWithEmptyTextFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createToken("")
        );
    }

    static SymbolEbnfParserToken symbol(final String s) {
        return EbnfParserToken.symbol(s, s);
    }

    // isMethodTesting2.....................................................................................

    @Override
    public final T createIsMethodObject() {
        return this.createToken();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return EbnfParserToken.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isLeaf") ||
                m.equals("isNoise") ||
                m.equals("isParent") ||
                m.equals("isEmpty") ||
                m.equals("isNotEmpty");
    }

    // ClassTestCase.................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public final String typeNameSuffix() {
        return EbnfParserToken.class.getSimpleName();
    }
}
