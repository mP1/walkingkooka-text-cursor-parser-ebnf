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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.MapTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.cursor.parser.FakeParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EbnfParserCombinatorsTransformMapTest implements MapTesting2<EbnfParserCombinatorsTransformMap<FakeParserContext>, EbnfIdentifierName, Parser<FakeParserContext>>,
        TypeNameTesting<EbnfParserCombinatorsTransformMap<FakeParserContext>> {

    @Test
    public void testGet() {
        this.getAndCheck(this.identifierName, this.parser);
    }

    @Test
    public void testGetUnknownFails() {
        assertThrows(EbnfParserCombinatorException.class, () -> this.createMap().get(EbnfIdentifierName.with("unknown")));
    }

    @Test
    public void testGetDefault() {
        final Parser<FakeParserContext> defaultParser = Parsers.fake();
        Assertions.assertSame(defaultParser,
                this.createMap().getOrDefault(EbnfIdentifierName.with("unknown"), defaultParser));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createMap(), this.wrappedMap().toString());
    }

    @Override
    public EbnfParserCombinatorsTransformMap createMap() {
        return EbnfParserCombinatorsTransformMap.with(this.wrappedMap());
    }


    private Map<EbnfIdentifierName, Parser<FakeParserContext>> wrappedMap() {
        final Map<EbnfIdentifierName, Parser<FakeParserContext>> map = Maps.ordered();
        map.put(this.identifierName, this.parser);
        return map;
    }

    private final EbnfIdentifierName identifierName = EbnfIdentifierName.with("parser1");

    private final Parser<FakeParserContext> parser = Parsers.fake();

    // ClassTesting.....................................................................................................

    @Override
    public Class<EbnfParserCombinatorsTransformMap<FakeParserContext>> type() {
        return Cast.to(EbnfParserCombinatorsTransformMap.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return EbnfParserCombinators.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Map.class.getSimpleName();
    }
}
