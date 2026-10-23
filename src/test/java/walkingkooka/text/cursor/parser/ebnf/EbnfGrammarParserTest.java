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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.printer.TreePrintableTesting;

// TODO EbnfGrammarParser - ClassTesting2
public final class EbnfGrammarParserTest extends EbnfParserTestCase
        implements TreePrintableTesting {

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
    public void testParseWhitespaceAfterRules() {
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
    public void testParseRuleWithRange() {
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
    public void testParseInvalidRuleNameDefinitionFails() {
        this.parseThrowsInvalidCharacterException(
                "123",
                '1',
                1,
                1
        );
    }

    @Test
    public void testParseInvalidRuleDefinitionFails() {
        this.parseThrowsInvalidCharacterException(
                "abc!",
                '!',
                4,
                1
        );
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
        this.parseThrowsInvalidCharacterException(
                IDENTIFIER1 + ASSIGNMENT + "123",
                '1',
                IDENTIFIER1 + ASSIGNMENT + '1',
                1
        );
    }

    @Test
    public void testParseInvalidExceptionTokenFails() {
        this.parseThrowsInvalidCharacterException(
                IDENTIFIER1 + ASSIGNMENT + "'A'-123",
                '1',
                IDENTIFIER1 + ASSIGNMENT + "'A'-'",
                1
        );
    }

    private GrammarEbnfParserToken grammar(final String text,
                                           final EbnfParserToken... rules) {
        return GrammarEbnfParserToken.with(
                Lists.of(rules),
                text
        );
    }

    private RuleEbnfParserToken rule1() {
        return rule(
                RULE1,
                identifier1(),
                assignmentToken(),
                terminal1(),
                terminatorToken()
        );
    }

    private RuleEbnfParserToken rule2() {
        return rule(
                RULE2,
                identifier2(),
                assignmentToken(),
                terminal2(),
                terminatorToken()
        );
    }

    @Override
    public EbnfGrammarParser createParser() {
        return new EbnfGrammarParser();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
                this.createParser(),
                "EbnfGrammarParser\n" +
                        "  TransformingParser\n" +
                        "    parser\n" +
                        "      SequenceParser\n" +
                        "        {WHITESPACE | COMMENT}, ({WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail) | {WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail) | Fail), {{WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail)}, {WHITESPACE | COMMENT}\n" +
                        "          0\n" +
                        "            RepeatingParser\n" +
                        "              {WHITESPACE | COMMENT}\n" +
                        "                min\n" +
                        "                  0\n" +
                        "                max\n" +
                        "                  2147483647\n" +
                        "                parser\n" +
                        "                  AlternativesParser\n" +
                        "                    WHITESPACE | COMMENT\n" +
                        "                      0\n" +
                        "                        TransformingParser\n" +
                        "                          parser\n" +
                        "                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                          transformer\n" +
                        "                            Lambda\n" +
                        "                      1\n" +
                        "                        TransformingParser\n" +
                        "                          parser\n" +
                        "                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                          transformer\n" +
                        "                            Lambda\n" +
                        "          1\n" +
                        "            AlternativesParser\n" +
                        "              {WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail) | {WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail) | Fail\n" +
                        "                0\n" +
                        "                  TransformingParser\n" +
                        "                    parser\n" +
                        "                      SequenceParser\n" +
                        "                        {WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail)\n" +
                        "                          0\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          1\n" +
                        "                            TransformingParser\n" +
                        "                              parser\n" +
                        "                                SequenceParser\n" +
                        "                                  'A'..'Z' || 'a'..'z', ({'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | Fail)\n" +
                        "                                    0\n" +
                        "                                      'A'..'Z' || 'a'..'z' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    1\n" +
                        "                                      AlternativesParser\n" +
                        "                                        {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | Fail\n" +
                        "                                          0\n" +
                        "                                            RepeatingParser\n" +
                        "                                              {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'}\n" +
                        "                                                min\n" +
                        "                                                  0\n" +
                        "                                                max\n" +
                        "                                                  2147483647\n" +
                        "                                                parser\n" +
                        "                                                  'A'..'Z' || 'a'..'z' || \"0123456789\" || '_' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                          1\n" +
                        "                                            ReportingParser\n" +
                        "                                              condition\n" +
                        "                                                ALWAYS\n" +
                        "                                              reporter\n" +
                        "                                                Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                              transformer\n" +
                        "                                Lambda\n" +
                        "                          2\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          3\n" +
                        "                            AlternativesParser\n" +
                        "                              assign | assign | Fail\n" +
                        "                                0\n" +
                        "                                  TransformingParser\n" +
                        "                                    parser\n" +
                        "                                      '=' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    transformer\n" +
                        "                                      Lambda\n" +
                        "                                1\n" +
                        "                                  ReportingParser\n" +
                        "                                    condition\n" +
                        "                                      ALWAYS\n" +
                        "                                    reporter\n" +
                        "                                      Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                          4\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          5\n" +
                        "                            rhs (walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParser$1)\n" +
                        "                          6\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          7\n" +
                        "                            AlternativesParser\n" +
                        "                              termination | termination | Fail\n" +
                        "                                0\n" +
                        "                                  TransformingParser\n" +
                        "                                    parser\n" +
                        "                                      ';' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    transformer\n" +
                        "                                      Lambda\n" +
                        "                                1\n" +
                        "                                  ReportingParser\n" +
                        "                                    condition\n" +
                        "                                      ALWAYS\n" +
                        "                                    reporter\n" +
                        "                                      Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                    transformer\n" +
                        "                      Lambda\n" +
                        "                1\n" +
                        "                  ReportingParser\n" +
                        "                    condition\n" +
                        "                      ALWAYS\n" +
                        "                    reporter\n" +
                        "                      Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "          2\n" +
                        "            RepeatingParser\n" +
                        "              {{WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail)}\n" +
                        "                min\n" +
                        "                  0\n" +
                        "                max\n" +
                        "                  2147483647\n" +
                        "                parser\n" +
                        "                  TransformingParser\n" +
                        "                    parser\n" +
                        "                      SequenceParser\n" +
                        "                        {WHITESPACE | COMMENT}, IDENTIFIER, {WHITESPACE | COMMENT}, (assign | assign | Fail), {WHITESPACE | COMMENT}, rhs, {WHITESPACE | COMMENT}, (termination | termination | Fail)\n" +
                        "                          0\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          1\n" +
                        "                            TransformingParser\n" +
                        "                              parser\n" +
                        "                                SequenceParser\n" +
                        "                                  'A'..'Z' || 'a'..'z', ({'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | Fail)\n" +
                        "                                    0\n" +
                        "                                      'A'..'Z' || 'a'..'z' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    1\n" +
                        "                                      AlternativesParser\n" +
                        "                                        {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'} | Fail\n" +
                        "                                          0\n" +
                        "                                            RepeatingParser\n" +
                        "                                              {'A'..'Z' || 'a'..'z' || \"0123456789\" || '_'}\n" +
                        "                                                min\n" +
                        "                                                  0\n" +
                        "                                                max\n" +
                        "                                                  2147483647\n" +
                        "                                                parser\n" +
                        "                                                  'A'..'Z' || 'a'..'z' || \"0123456789\" || '_' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                          1\n" +
                        "                                            ReportingParser\n" +
                        "                                              condition\n" +
                        "                                                ALWAYS\n" +
                        "                                              reporter\n" +
                        "                                                Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                              transformer\n" +
                        "                                Lambda\n" +
                        "                          2\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          3\n" +
                        "                            AlternativesParser\n" +
                        "                              assign | assign | Fail\n" +
                        "                                0\n" +
                        "                                  TransformingParser\n" +
                        "                                    parser\n" +
                        "                                      '=' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    transformer\n" +
                        "                                      Lambda\n" +
                        "                                1\n" +
                        "                                  ReportingParser\n" +
                        "                                    condition\n" +
                        "                                      ALWAYS\n" +
                        "                                    reporter\n" +
                        "                                      Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                          4\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          5\n" +
                        "                            rhs (walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParser$1)\n" +
                        "                          6\n" +
                        "                            RepeatingParser\n" +
                        "                              {WHITESPACE | COMMENT}\n" +
                        "                                min\n" +
                        "                                  0\n" +
                        "                                max\n" +
                        "                                  2147483647\n" +
                        "                                parser\n" +
                        "                                  AlternativesParser\n" +
                        "                                    WHITESPACE | COMMENT\n" +
                        "                                      0\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                                      1\n" +
                        "                                        TransformingParser\n" +
                        "                                          parser\n" +
                        "                                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                                          transformer\n" +
                        "                                            Lambda\n" +
                        "                          7\n" +
                        "                            AlternativesParser\n" +
                        "                              termination | termination | Fail\n" +
                        "                                0\n" +
                        "                                  TransformingParser\n" +
                        "                                    parser\n" +
                        "                                      ';' (walkingkooka.text.cursor.parser.CharPredicateCharacterParser)\n" +
                        "                                    transformer\n" +
                        "                                      Lambda\n" +
                        "                                1\n" +
                        "                                  ReportingParser\n" +
                        "                                    condition\n" +
                        "                                      ALWAYS\n" +
                        "                                    reporter\n" +
                        "                                      Fail (walkingkooka.text.cursor.parser.BasicParserReporter)\n" +
                        "                    transformer\n" +
                        "                      Lambda\n" +
                        "          3\n" +
                        "            RepeatingParser\n" +
                        "              {WHITESPACE | COMMENT}\n" +
                        "                min\n" +
                        "                  0\n" +
                        "                max\n" +
                        "                  2147483647\n" +
                        "                parser\n" +
                        "                  AlternativesParser\n" +
                        "                    WHITESPACE | COMMENT\n" +
                        "                      0\n" +
                        "                        TransformingParser\n" +
                        "                          parser\n" +
                        "                            whitespace{1,2147483647} (walkingkooka.text.cursor.parser.CharPredicateStringParser)\n" +
                        "                          transformer\n" +
                        "                            Lambda\n" +
                        "                      1\n" +
                        "                        TransformingParser\n" +
                        "                          parser\n" +
                        "                            \"(*\"*\"*)\" (walkingkooka.text.cursor.parser.SurroundStringParser)\n" +
                        "                          transformer\n" +
                        "                            Lambda\n" +
                        "    transformer\n" +
                        "      Lambda\n"
        );
    }
}
