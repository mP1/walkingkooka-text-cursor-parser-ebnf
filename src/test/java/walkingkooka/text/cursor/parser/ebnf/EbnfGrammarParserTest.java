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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;

// TODO EbnfGrammarParser - ClassTesting2
public final class EbnfGrammarParserTest extends EbnfParserTestCase<EbnfGrammarParserToken> {

    private final static String RULE1 = IDENTIFIER1 + "=" + TERMINAL1_TEXT + ";";
    private final static String RULE2 = IDENTIFIER2 + "=" + TERMINAL2_TEXT + ";";

    @Test
    public void testParseCompact2() {
        final String text = RULE1;

        this.parseAndCheck(
                text,
                grammar(
                        text,
                        rule1()
                ),
                text
        );
    }

    @Test
    public void testParseCompact() {
        final String text = RULE1 + RULE2;

        this.parseAndCheck(
                text,
                grammar(
                        text,
                        rule1(),
                        rule2()
                ),
                text
        );
    }


    @Test
    public void testParseWhitespaceBetweenRules() {
        final String text = RULE1 + WHITESPACE1 + RULE2;

        this.parseAndCheck(
                text,
                grammar(
                        text,
                        rule1(),
                        rule(WHITESPACE1 + RULE2, whitespace1(), identifier2(), assignmentToken(), terminal2(), terminatorToken())
                ),
                text
        );
    }

    @Test
    public void testWhitespaceAfterRules() {
        final String text = RULE1 + RULE2 + WHITESPACE1;

        this.parseAndCheck(
                text,
                grammar(
                        text,
                        rule1(),
                        rule2(),
                        whitespace1()
                ),
                text
        );
    }

    @Test
    public void testRuleWithRange() {
        final String rule2Text = IDENTIFIER2 + ASSIGNMENT + TERMINAL1_TEXT + BETWEEN + TERMINAL2_TEXT + TERMINATOR;
        final String text = RULE1 + rule2Text;

        this.parseAndCheck(
                text,
                grammar(
                        text,
                        rule1(),
                        rule(rule2Text, identifier2(), assignmentToken(), range(TERMINAL1_TEXT + BETWEEN + TERMINAL2_TEXT, terminal1(), between(), terminal2()), terminatorToken())),
                text
        );
    }

    @Test
    public void testInvalidRuleNameDefinitionFails() {
        this.parseThrows("123", '1', 1, 1);
    }

    @Test
    public void testInvalidRuleDefinitionFails() {
        this.parseThrows("abc!", '!', 4, 1);
    }

    @Test
    public void testParseInvalidRuleMissingRhsFails() {
        this.parseThrowsEndOfText(IDENTIFIER1 + ASSIGNMENT);
    }

    @Test
    public void testParseInvalidRuleMissingTerminatorFails() {
        this.parseThrowsEndOfText(IDENTIFIER1 + ASSIGNMENT + TERMINAL1_TEXT);
    }

    @Test
    public void testParseInvalidRuleRhsFails() {
        this.parseThrows(
                IDENTIFIER1 + ASSIGNMENT + "123",
                '1',
                IDENTIFIER1 + ASSIGNMENT + '1',
                1
        );
    }

    @Test
    public void testParseInvalidExceptionTokenFails() {
        this.parseThrows(
                IDENTIFIER1 + ASSIGNMENT + "'A'-123",
                '1',
                IDENTIFIER1 + ASSIGNMENT + "'A'-'",
                1
        );
    }

    private EbnfGrammarParserToken grammar(final String text, final EbnfParserToken... rules) {
        return EbnfGrammarParserToken.with(Lists.of(rules), text);
    }

    private EbnfRuleParserToken rule1() {
        return rule(RULE1, identifier1(), assignmentToken(), terminal1(), terminatorToken());
    }

    private EbnfRuleParserToken rule2() {
        return rule(RULE2, identifier2(), assignmentToken(), terminal2(), terminatorToken());
    }

    @Override
    public Parser<EbnfParserContext> createParser() {
        return Cast.to(EbnfGrammarParser.GRAMMAR);
    }
}
