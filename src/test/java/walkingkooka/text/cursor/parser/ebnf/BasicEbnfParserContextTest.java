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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.math.MathContext;

public final class BasicEbnfParserContextTest implements ClassTesting2<BasicEbnfParserContext>,
        EbnfParserContextTesting<BasicEbnfParserContext>,
        DecimalNumberContextDelegator {

    @Override
    public void testCurrencySymbol() {
    }

    @Override
    public void testGroupSeparator() {
    }

    @Override
    public void testPercentSymbol() {
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createContext(),
                "decimalSeparator='.' exponentSymbol=\"E\" negativeSign='-' positiveSign='+'");
    }

    @Override
    public BasicEbnfParserContext createContext() {
        return BasicEbnfParserContext.instance();
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
                .decimalNumberDigitCount();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(this.mathContext());
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    // class............................................................................................................

    @Override
    public Class<BasicEbnfParserContext> type() {
        return BasicEbnfParserContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
