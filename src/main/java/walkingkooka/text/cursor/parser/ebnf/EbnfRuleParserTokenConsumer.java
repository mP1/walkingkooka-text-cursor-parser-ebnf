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

import java.util.function.Consumer;

final class EbnfRuleParserTokenConsumer implements Consumer<EbnfParserToken> {

    static EbnfRuleParserTokenConsumer with() {
        return new EbnfRuleParserTokenConsumer();
    }

    private EbnfRuleParserTokenConsumer() {
        super();
    }

    @Override
    public void accept(final EbnfParserToken token) {
        if (!token.isNoise()) {
            if (null == this.identifier) {
                if (!token.isIdentifier()) {
                    throw new IllegalArgumentException("Rule expected identifier but got " + token);
                }
                this.identifier = token.cast(EbnfIdentifierParserToken.class);
            } else {
                if (null == this.assignment) {
                    this.assignment = token;
                }
            }
        }
    }

    EbnfIdentifierParserToken identifier;
    EbnfParserToken assignment;
}
