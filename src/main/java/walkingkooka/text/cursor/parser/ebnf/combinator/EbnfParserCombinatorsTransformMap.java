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

import walkingkooka.collect.map.Maps;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * A read only {@link Map} that also throws an exception if {@link #get(Object)} has no parser.
 */
final class EbnfParserCombinatorsTransformMap<C extends ParserContext> extends AbstractMap<EbnfIdentifierName, Parser<C>> {

    static {
        Maps.registerImmutableType(EbnfParserCombinatorsTransformMap.class);
    }

    static <C extends ParserContext> EbnfParserCombinatorsTransformMap<C> with(final Map<EbnfIdentifierName, Parser<C>> map) {
        return new EbnfParserCombinatorsTransformMap<>(map);
    }

    private EbnfParserCombinatorsTransformMap(final Map<EbnfIdentifierName, Parser<C>> map) {
        super();
        this.map = Maps.readOnly(map);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public Parser<C> get(final Object key) {
        final Parser<C> value = this.map.get(key);
        if(null==value) {
            throw new EbnfParserCombinatorException("Unknown mapping " + key);
        }
        return value;
    }

    @Override
    public Parser<C> getOrDefault(final Object key, final Parser<C> defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }

    @Override
    public Set<Entry<EbnfIdentifierName, Parser<C>>> entrySet() {
        return this.map.entrySet();
    }

    private final Map<EbnfIdentifierName, Parser<C>> map;

    @Override
    public String toString() {
        return this.map.toString();
    }
}
