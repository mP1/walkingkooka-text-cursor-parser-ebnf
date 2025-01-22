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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.BigIntegerParserToken;
import walkingkooka.text.cursor.parser.FakeParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfConcatenationParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfExceptionParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGroupParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfOptionalParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRepeatedParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRuleParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EbnfParserCombinatorsTest implements ParserTesting2<Parser<FakeParserContext>, FakeParserContext>,
        PublicStaticHelperTesting<EbnfParserCombinators> {

    @Test
    @Disabled("Until proper error reporting is available")
    public void testEmptyCursorFail() {
        this.parseFailAndCheck("");
    }

    // terminal.........................................................................................................

    // TEST = "abc";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerTerminal() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"abc\";",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST="terminal-text-123";
    @Test
    public void testTransformTerminal() {
        final String text = "terminal-text-123";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=\"terminal-text-123\";\n", // grammar
                text
        );
    }

    // alt..............................................................................................................

    // TEST = "abc" | "def";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerAlternative() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"abc\" | \"def\";",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {
                    @Override
                    public Parser<FakeParserContext> alternatives(final EbnfAlternativeParserToken token,
                                                                  final Parser<FakeParserContext> parser) {
                        b.append("ALTERNATIVES " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "TERMINAL \"def\"\n" +
                        "ALTERNATIVES \"abc\" | \"def\"\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // OPTIONAL = ["optional-xyz"];
    // TEST     =   "abc" | OPTIONAL;
    @Test
    public void testTransformAlternativeOptionalFails() {
        this.parseGrammarAndGetParserThrows(
                "OPTIONAL = [\"optional-xyz\"];\n" +
                        "TEST     =   \"abc\" | OPTIONAL;",
                new EbnfParserCombinatorException("Alternatives got 1 optional(s) expected 0, OPTIONAL")
        );
    }

    // TEST    =   "abc" | "xyz";
    @Test
    public void testTransformAlternativeTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST    =   \"abc\" | \"xyz\";");

        final String text = "abc";
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text
        );

        final String text2 = "xyz";
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text2
        );
    }

    // TEST    =   "abc" | "xyz" | "qrs";
    @Test
    public void testTransformAlternativeTerminalTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST    =   \"abc\" | \"xyz\" | \"qrs\";");

        final String text = "abc";
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text
        );

        final String text2 = "xyz";
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text2
        );
    }

    // concat...........................................................................................................

    // TEST = \"abc\", \"def\";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerConcatenation() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"abc\" , \"def\";",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {
                    @Override
                    public Parser<FakeParserContext> concatenation(final EbnfConcatenationParserToken token,
                                                                   final Parser<FakeParserContext> parser) {
                        b.append("CONCAT " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "TERMINAL \"def\"\n" +
                        "CONCAT \"abc\" , \"def\"\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST="abc" ,  "def";
    @Test
    public void testTransformConcatenationTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=\"abc\", \"def\";");

        final String text1 = "abc";
        final String text2 = "def";
        final String concatText = text1 + text2;

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                concatText,
                sequence(
                        this.string(text1),
                        this.string(text2)
                ),
                concatText
        );
    }

    // TEST="abc" , "def", "ghi";
    @Test
    public void testTransformConcatenationTerminalTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=\"abc\", \"def\", \"ghi\";");

        final String text1 = "abc";
        final String text2 = "def";
        final String text3 = "ghi";
        final String concatText = text1 + text2 + text3;

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                concatText,
                sequence(
                        this.string(text1),
                        this.string(text2),
                        this.string(text3)
                ),
                concatText
        );
    }

    // TEST=["abc"] ,  "def";
    @Test
    public void testTransformConcatenationMissingOptionalTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=[\"abc\"], \"def\";");

        final String text2 = "def";

        // optional absent
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text2,
                sequence(
                        this.string(text2)
                ),
                text2
        );
    }

    // TEST=["abc"] ,  "def";
    @Test
    public void testTransformConcatenationPresentOptionalTerminalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=[\"abc\"], \"def\";");

        final String text1 = "abc";
        final String text2 = "def";
        final String concatText = text1 + text2;

        // optional present
        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                concatText,
                sequence(
                        this.string(text1),
                        this.string(text2)
                ),
                concatText
        );
    }

    // INITIAL =   "a";
    // LAST    =   "z";
    // TEST    =   [INITIAL], LAST;
    @Test
    public void testTransformConcatOfMissingOptionalIdentifierTerminalThenIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"a\";\n" +
                        "LAST    =   \"z\";\n" +
                        "TEST    =   [INITIAL], LAST;"
        );

        final ParserToken z = this.string("z");

        // missing optional a
        final String text = "z";
        final String after = "!!!";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(z),
                text,
                after
        );
    }

    // INITIAL =   ["a"];
    // LAST    =   "z";
    // TEST    =   INITIAL, LAST;
    @Test
    public void testTransformConcatOfMissingOptionalIdentifierTerminalThenIdentifierTerminal2() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   [\"a\"];\n" +
                        "LAST    =   \"z\";\n" +
                        "TEST    =   INITIAL, LAST;"
        );

        final ParserToken z = this.string("z");

        // missing optional a
        final String text = "z";
        final String after = "!!!";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(z),
                text,
                after
        );
    }

    // INITIAL =   "a";
    // LAST    =   "z";
    // TEST    =   INITIAL, [LAST];
    @Test
    public void testTransformConcatOfIdentifierTerminalThenMissingOptionalIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"a\";\n" +
                        "LAST    =   [\"z\"];\n" +
                        "TEST    =   INITIAL, LAST;"
        );

        final ParserToken a = this.string("a");

        // missing optional z
        final String text = "a";
        final String after = "!!!";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(
                        a
                ),
                text,
                after
        );
    }

    // INITIAL =   "a";
    // LAST    =   "z";
    // TEST    =   INITIAL , [LAST];
    @Test
    public void testTransformConcatOfIdentifierTerminalThenOptionalIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"a\";\n" +
                        "LAST    =   [\"z\"];\n" +
                        "TEST    =   INITIAL , LAST;"
        );

        final ParserToken a = this.string("a");
        final ParserToken z = this.string("z");

        // missing optional z
        final String text = "az";
        final String after = "!!!";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(
                        a,
                        z
                ),
                text,
                after
        );
    }

    // INITIAL =   "i" | "j" | "k";
    // PART    =   "p" | "q" | "r";
    // TEST    =   INITIAL , PART , {PART};
    @Test
    public void testTransformConcatOfIdentifierAlternativesThenIdentifierAlternativesThenRepeatingIdentifierAlternatives() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"i\" | \"j\" | \"k\";\n" +
                        "PART    =   \"p\" | \"q\" | \"r\";\n" +
                        "TEST    =   INITIAL , PART , {PART};"
        );

        final ParserToken i = this.string("i");
        final ParserToken p = this.string("p");
        final ParserToken q = this.string("q");

        // required i1 and required p1 and optional p1
        final String text = "ipq";
        final String after = "!!!";

        final ParserToken repeatedQ = ParserTokens.repeated(
                Lists.of(q),
                "q"
        );

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(
                        i, p, repeatedQ
                ),
                text,
                after
        );
    }

    // INITIAL =   "i" | "j" | "k";
    // PART    =   "p" | "q" | "r";
    // TEST    =   INITIAL , PART , {PART};
    @Test
    public void testTransformConcatOfIdentifierAlternativesThenIdentifierAlternativesThenRepeatingIdentifierAlternatives2() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"i\" | \"j\" | \"k\";\n" +
                        "PART    =   \"p\" | \"q\" | \"r\";\n" +
                        "TEST    =   INITIAL , PART , {PART};"
        );

        final ParserToken i = this.string("i");
        final ParserToken p = this.string("p");
        final ParserToken q = this.string("q");

        // required i1 and required p1 and repeating q
        final String text = "ipqqq";
        final String after = "!!!";

        final ParserToken repeatedQ = ParserTokens.repeated(
                Lists.of(
                        q, q, q
                ),
                "qqq"
        );

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                sequence(
                        i, p, repeatedQ
                ),
                text,
                after
        );
    }

    // INITIAL =   "i" | "j" | "k";
    // PART    =   "p" | "q" | "r";
    // TEST    =   INITIAL , PART , {PART};
    @Test
    public void testTransformConcatOfIdentifierAlternativesThenIdentifierAlternativesThenRepeatingIdentifierAlternativesWhenNone() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"i\" | \"j\" | \"k\";\n" +
                        "PART    =   \"p\" | \"q\" | \"r\";\n" +
                        "TEST    =   INITIAL , PART , {PART};"
        );

        this.parseFailAndCheck(
                parser,
                "D123"
        );
    }

    @Test
    public void testTransformConcatOfIdentifierTerminalThenIdentifierTerminalThenMissingRepeatingIdentifierTerminalThenMissingOptionalIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"Initial\";\n" +
                        "PART    =   \"Part\";\n" +
                        "LAST    =   [ \"Last\" ];\n" +
                        "TEST    =   INITIAL, PART, {PART}, LAST;"
        );

        this.parseFailAndCheck(
                parser,
                "InitialPart"
        );
    }

    //    INITIAL =   "Initial";
    //    PART    =   "Part";
    //    LAST    =   [ "Last" ];
    //
    //    TEST    =   INITIAL, PART, {PART}, [LAST];
    @Test
    public void testTransformConcatOfIdentifierTerminalThenIdentifierTerminalThenRepeatingIdentifierTerminalThenMissingOptionalIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"Initial\";\n" +
                        "PART    =   \"Part\";\n" +
                        "LAST    =   [ \"Last\" ];\n" +
                        "TEST    =   INITIAL, PART, {PART}, LAST;"
        );

        final ParserToken initial = this.string("Initial");
        final ParserToken part = this.string("Part");

        // without optional LAST
        final String initialPartPart = "InitialPartPart";
        final String after = "!!!";

        final ParserToken repeatedPart = ParserTokens.repeated(
                Lists.of(part),
                "Part"
        );

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                initialPartPart + after,
                sequence(
                        initial,
                        part,
                        repeatedPart
                ),
                initialPartPart,
                after
        );
    }

    //    INITIAL =   "Initial";
    //    PART    =   "Part";
    //    LAST    =   [ "Last" ];
    //
    //    TEST    =   INITIAL, PART, {PART}, [LAST];
    @Test
    public void testTransformConcatOfIdentifierTerminalThenIdentifierTerminalThenRepeatingIdentifierTerminalThenPresentOptionalIdentifierTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(
                "INITIAL =   \"Initial\";\n" +
                        "PART    =   \"Part\";\n" +
                        "LAST    =   [ \"Last\" ];\n" +
                        "TEST    =   INITIAL, PART, {PART}, LAST;"
        );

        final ParserToken initial = this.string("Initial");
        final ParserToken part = this.string("Part");
        final ParserToken last = this.string("Last");

        // includes optional L
        final String initialPartPartLast = "InitialPartPartPartLast";
        final String after = "!!!";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                initialPartPartLast + after,
                sequence(
                        initial,
                        part,
                        ParserTokens.repeated(
                                Lists.of(
                                        part,
                                        part
                                ),
                                "PartPart"
                        ),
                        last
                ),
                initialPartPartLast,
                after
        );
    }

    // exception........................................................................................................

    // TEST = \"abc\" - \"def\";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerException() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"abc\" - \"def\";",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> exception(final EbnfExceptionParserToken token,
                                                               final Parser<FakeParserContext> parser) {
                        b.append("EXCEPTION " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "TERMINAL \"def\"\n" +
                        "EXCEPTION \"abc\" - \"def\"\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // OPTIONAL=["1"];
    // TEST=OPTIONAL - "2";
    @Test
    public void testParseGrammarExceptionOptionalTerminalTerminalFails() {
        this.parseGrammarAndGetParserThrows(
                "OPTIONAL=[\"1\"];\n" +
                        "TEST=OPTIONAL - \"2\";",
                new EbnfParserCombinatorException("Exception left must not be optional got OPTIONAL")
        );
    }

    // OPTIONAL=["2"];
    // TEST="1" - OPTIONAL;
    @Test
    public void testParseGrammarExceptionTerminalOptionalTerminalFails() {
        this.parseGrammarAndGetParserThrows(
                "OPTIONAL=[\"2\"];\n" +
                        "TEST=\"1\" - OPTIONAL;",
                new EbnfParserCombinatorException("Exception right must not be optional got \"1\"")
        );
    }

    // TEST=ONLY_LETTERS - "abc";
    @Test
    public void testTransformExceptionTerminalTerminal() {
        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=ONLY_LETTERS - \"abc\";",
                "xyz"
        );
    }

    // TEST=ONLY_LETTERS - "abc";
    @Test
    public void testTransformExceptionTerminalTerminal2() {
        this.parseGrammarGetParsersThenParseFail(
                "TEST=ONLY_LETTERS - \"abc\";",
                "abc"
        );
    }

    // group............................................................................................................

    // TEST = ("abc");
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerGroup() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = (\"abc\");",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> group(final EbnfGroupParserToken token,
                                                           final Parser<FakeParserContext> parser) {
                        b.append("GROUP " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "GROUP (\"abc\")\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST=["Hello"];
    @Test
    public void testTransformGroupOptionalTerminal() {
        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=([\"Hello\"]);", // grammar
                "Hello"
        );
    }

    // TEST=("group-text-abc");
    @Test
    public void testTransformGroupTerminal() {
        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=(\"Hello\");", // grammar
                "Hello"
        );
    }

    // identifier.......................................................................................................

    // BACK_REFERENCE = "abc";
    // TEST = BACK_REFERENCE;
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerIdentifierBackwardReference() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "BACK_REFERENCE = \"abc\";  \n" +
                        "TEST = BACK_REFERENCE;\n",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> identifier(final EbnfIdentifierParserToken token,
                                                                final Parser<FakeParserContext> parser) {
                        b.append("IDENTIFIER " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE " + token.toString().replace("\n", "") + "\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "RULE BACK_REFERENCE = \"abc\";\n" +
                        "IDENTIFIER BACK_REFERENCE\n" +
                        "RULE   TEST = BACK_REFERENCE;\n",
                b.toString()
        );
    }

    // TEST = FORWARD_REFERENCE;
    // FORWARD_REFERENCE = "abc";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerIdentifierForwardReference() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE = \"abc\";  \n",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> identifier(final EbnfIdentifierParserToken token,
                                                                final Parser<FakeParserContext> parser) {
                        b.append("IDENTIFIER " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE " + token.toString().replace("\n", "") + "\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "RULE FORWARD_REFERENCE = \"abc\";\n" +
                        "IDENTIFIER FORWARD_REFERENCE\n" +
                        "RULE TEST = FORWARD_REFERENCE;\n",
                b.toString()
        );
    }

    // TEST = "abc", TEST;
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerIdentifierSelfReference() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"abc\", TEST;",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> concatenation(final EbnfConcatenationParserToken token,
                                                                   final Parser<FakeParserContext> parser) {
                        b.append("CONCAT " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> identifier(final EbnfIdentifierParserToken token,
                                                                final Parser<FakeParserContext> parser) {
                        b.append("IDENTIFIER " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE " + token.toString().replace("\n", "") + "\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "CONCAT \"abc\", TEST\n" +
                        "RULE TEST = \"abc\", TEST;\n" +
                        "IDENTIFIER TEST\n",
                b.toString()
        );
    }

    // DUPLICATED=   "duplicate";
    @Test
    public void testTransformIdentifierDuplicateRuleGrammarFails() {
        final EbnfParserCombinatorException thrown = assertThrows(
                EbnfParserCombinatorException.class,
                () -> this.parseGrammarAndGetParser("DUPLICATED=   \"duplicate\";")
        );

        this.checkEquals(
                "Rule \"DUPLICATED\" duplicated in provided parsers",
                thrown.getMessage()
        );
    }

    // RULE1 = "rule-1"
    // RULE1 = "2nd-rule-1"
    @Test
    public void testTransformIdentifierDuplicateRulePredefinedFails() {
        final EbnfParserCombinatorException thrown = assertThrows(
                EbnfParserCombinatorException.class,
                () -> this.parseGrammarAndGetParser(
                        "RULE1=   \"rule-1\";\n" +
                                "RULE1=   \"2nd-rule-1\";\n"
                )
        );

        this.checkEquals(
                "Rule \"RULE1\" duplicated in grammar",
                thrown.getMessage()
        );
    }

    // TEST=FORWARD_REFERENCE;
    // FORWARD_REFERENCE="abc123";;
    @Test
    public void testTransformIdentifierBackwardReference() {
        final String text = "abc123";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE=\"abc123\";", // grammar
                text
        );
    }

    // BACKWARD_REFERENCE="abc123";;
    // TEST=BACKWARD_REFERENCE;
    @Test
    public void testTransformIdentifierForwardReference() {
        final String text = "abc123";

        this.parseGrammarAndGetParserAndParseCheck(
                "FORWARD_REFERENCE=\"abc123\";\n" +
                        "TEST=FORWARD_REFERENCE;", // grammar
                text
        );
    }

    // TEST=FORWARD_REFERENCE;
    // FORWARD_REFERENCE=FORWARD_REFERENCE2;
    // FORWARD_REFERENCE2="abc123";
    @Test
    public void testTransformIdentifierForwardReference2() {
        final String text = "abc123";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE=FORWARD_REFERENCE2;\n" +
                        "FORWARD_REFERENCE2=\"abc123\";", // grammar
                text
        );
    }

    // TEST=FORWARD_REFERENCE;
    // FORWARD_REFERENCE=FORWARD_REFERENCE2;
    // FORWARD_REFERENCE2=FORWARD_REFERENCE3;
    // FORWARD_REFERENCE3="abc123";
    @Test
    public void testTransformIdentifierForwardReference3() {
        final String text = "abc123";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE=FORWARD_REFERENCE2;\n" +
                        "FORWARD_REFERENCE2=FORWARD_REFERENCE3;\n" +
                        "FORWARD_REFERENCE3=\"abc123\";",
                text
        );
    }

    // TEST=[FORWARD_REFERENCE], "abc";
    // FORWARD_REFERENCE="def", FORWARD_REFERENCE;
    //
    // FORWARD_REFERENCE will be a EbnfParserCombinatorProxyParser
    @Test
    public void testTransformIdentifierToSelf() {
        final String text = "abc";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=[FORWARD_REFERENCE], \"abc\";\n" +
                        "FORWARD_REFERENCE=\"def\", FORWARD_REFERENCE;",
                text,
                sequence(
                        string(text)
                ),
                text,
                ""
        );
    }

    // TEST=FORWARD_REFERENCE;
    // FORWARD_REFERENCE=FORWARD_REFERENCE2;
    // FORWARD_REFERENCE2=ONLY_LETTERS;
    @Test
    public void testTransformIdentifierForwardReferenceToPredefinedParser() {
        final String text = "abc";
        final String after = "123";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE=FORWARD_REFERENCE2;\n" +
                        "FORWARD_REFERENCE2=ONLY_LETTERS;",
                text + after,
                string(text),
                text,
                after
        );
    }

    // TEST=FORWARD_REFERENCE;
    // FORWARD_REFERENCE=FORWARD_REFERENCE2;
    // FORWARD_REFERENCE2=ONLY_LETTERS, "123";
    @Test
    public void testTransformIdentifierForwardReferenceToPredefinedParser2() {
        final String text = "abc123";
        final String after = "...";

        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=FORWARD_REFERENCE;\n" +
                        "FORWARD_REFERENCE=FORWARD_REFERENCE2;\n" +
                        "FORWARD_REFERENCE2=ONLY_LETTERS, \"123\";",
                text + after,
                sequence(
                        string("abc"),
                        number("123")
                ),
                text,
                after
        );
    }

    // optional.........................................................................................................

    // TEST = ["abc"];
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerOptional() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = [\"abc\"];",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> optional(final EbnfOptionalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("OPTIONAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "OPTIONAL [\"abc\"]\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST=["optional-text-abc"];
    @Test
    public void testTransformOptionalTerminal() {
        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=[\"optional-text-abc\"];",
                "optional-text-abc"
        );
    }

    // TEST=["optional-text-abc"];
    @Test
    public void testTransformOptionalTerminal2() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=[\"optional-text-abc\"];");

        this.parseFailAndCheck(
                parser,
                "different"
        );
    }

    @Test
    public void testTransformMissingOptionalTerminal() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST=[\"optional-text-abc\"];");

        this.parseFailAndCheck(
                parser,
                "different"
        );
    }

    // ABC="abc";
    // TEST=[ABC];
    @Test
    public void testTransformOptionalIdentifier() {
        this.parseGrammarAndGetParserAndParseCheck(
                "ABC=\"abc\";\n" +
                        "TEST=[ABC];",
                "abc"
        );
    }

    // ABC="abc";
    // TEST=[ABC];
    @Test
    public void testTransformOptionalIdentifier2() {
        this.parseGrammarGetParsersThenParseFail(
                "ABC=\"abc\";\n" +
                        "TEST=[ABC];",
                "different"
        );
    }

    // TEST=[["optional-text-abc"]];
    @Test
    public void testTransformOptionalOptionalTerminal() {
        this.parseGrammarAndGetParserAndParseCheck(
                "TEST=[[\"optional-text-abc\"]];",
                "optional-text-abc"
        );
    }

    // range............................................................................................................

    // TEST = "a".."b";
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerRangeTerminalTerminal() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST = \"a\"..\"b\";",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> range(final EbnfRangeParserToken token,
                                                           final String beginText,
                                                           final String endText) {
                        b.append("RANGE " + token + " " + beginText + " " + endText + "\n");
                        return Parsers.fake();
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"a\"\n" +
                        "TERMINAL \"b\"\n" +
                        "RANGE \"a\"..\"b\" a b\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST="a".."z";
    @Test
    public void testTransformRangeTerminalTerminal() {
        this.parseRangeAndCheck("TEST=\"a\"..\"z\";");
    }

    // FROM="a";
    //
    // TEST=FROM.."z";
    @Test
    public void testTransformRangeIdentifierTerminal() {
        this.parseRangeAndCheck(
                "FROM=\"a\";\n" +
                        "\n" +
                        "TEST=FROM..\"z\";"
        );
    }

    // TO="z";
    //
    // TEST="a"..TO;
    @Test
    public void testTransformRangeTerminalIdentifier() {
        this.parseRangeAndCheck(
                "TO=\"z\";\n" +
                        "\n" +
                        "TEST=\"a\"..TO;"
        );
    }

    // FROM    =   "a";
    // TO      =   "z";
    //
    // TEST    =   FROM..TO;
    @Test
    public void testTransformRangeIdentifierIdentifier() {
        this.parseRangeAndCheck(
                "FROM    =   \"a\";\n" +
                        "TO      =   \"z\";\n" +
                        "\n" +
                        "TEST    =   FROM..TO;"
        );
    }
    // FROM="a";
    // TO="z";
    //
    // TEST=FROM2..TO2;
    //
    // FROM2 = FROM;
    // TO2 = TO;

    @Test
    public void testTransformRangeIdentifierIdentifier2() {
        this.parseRangeAndCheck(
                "FROM=\"a\";\n" +
                        "TO=\"z\";\n" +
                        "\n" +
                        "TEST=FROM2..TO2;\n" +
                        "\n" +
                        "FROM2 = FROM;\n" +
                        "TO2 = TO;"
        );
    }

    private void parseRangeAndCheck(final String grammar) {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser(grammar);

        final String text = "m";
        final String after = "123";

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                text + after,
                this.string(text),
                text,
                after
        );

        this.parseFailAndCheck(
                parser,
                "Q"
        );
    }

    // FROM="a".."z";
    // TO="9";
    //
    // TEST=FROM..TO;
    @Test
    public void testTransformRangeInvalidBoundFails() {
        // from is a Range, terminal or identifier to terminal required
        this.parseGrammarAndGetParserThrows(
                "FROM=\"a\"..\"z\";\n" +
                        "TO=\"9\";\n" +
                        "\n" +
                        "TEST=FROM..TO;", // grammar
                new EbnfParserCombinatorException("Invalid range begin, expected identifier or terminal but got Identifier=FROM")
        );
    }

    // FROM={"a"};
    // TO="z";
    //
    // TEST=FROM..TO;
    @Test
    public void testTransformRangeInvalidBoundFails2() {
        // from is a repeated, terminal or identifier to terminal required
        this.parseGrammarAndGetParserThrows(
                "FROM={\"a\"};\n" +
                        "TO=\"z\";\n" +
                        "\n" +
                        "TEST=FROM..TO;", // grammar
                new EbnfParserCombinatorException("Invalid range begin, expected identifier or terminal but got Identifier=FROM")
        );
    }

    // FROM="a";
    // TO=["z"];
    // TEST=FROM..TO;
    @Test
    public void testTransformRangeInvalidBoundFails3() {
        // to is a Optional, terminal or identifier to terminal required
        this.parseGrammarAndGetParserThrows(
                "FROM=\"a\";\n" +
                        "TO=[\"z\"];\n" +
                        "\n" +
                        "TEST=FROM..TO;", // grammar
                new EbnfParserCombinatorException("Invalid range end, expected identifier or terminal but got Identifier=TO")
        );
    }

    // repeat............................................................................................................

    // TEST={"abc"};
    @Test
    public void testTransformEbnfParserCombinatorSyntaxTreeTransformerRepeatTerminal() {
        final StringBuilder b = new StringBuilder();

        this.parseGrammarAndGetParser(
                "TEST={\"abc\"};",
                new FakeEbnfParserCombinatorSyntaxTreeTransformer<>() {

                    @Override
                    public Parser<FakeParserContext> repeated(final EbnfRepeatedParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("REPEATED " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                              final Parser<FakeParserContext> parser) {
                        b.append("TERMINAL " + token + "\n");
                        return parser;
                    }

                    @Override
                    public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                        b.append("RULE\n");
                        return parser;
                    }
                }
        );

        this.checkEquals(
                "TERMINAL \"abc\"\n" +
                        "REPEATED {\"abc\"}\n" +
                        "RULE\n",
                b.toString()
        );
    }

    // TEST={"abc"};
    @Test
    public void testTransformRepeat() {
        final Parser<FakeParserContext> parser = this.parseGrammarAndGetParser("TEST={\"abc\"};");

        final String text = "abc";
        final String after = "123";
        final String repeatedText = text + text;

        this.parseGrammarAndGetParserAndParseCheck(
                parser,
                repeatedText + after,
                ParserTokens.repeated(
                        Lists.of(
                                this.string(text),
                                this.string(text)
                        ),
                        repeatedText
                ),
                repeatedText,
                after
        );
    }

    // HELPERS .........................................................................................................

    @Override
    public Parser<FakeParserContext> createParser() {
        return this.parseGrammarAndGetParser("TEST=\"text\";");
    }

    private void parseGrammarGetParsersThenParseFail(final String grammar,
                                                     final String text) {
        this.parseFailAndCheck(
                this.parseGrammarAndGetParser(grammar),
                text
        );
    }

    private void parseGrammarAndGetParserThrows(final String grammar,
                                                final RuntimeException expected) {
        final RuntimeException thrown = assertThrows(
                expected.getClass(),
                () -> this.parseGrammarAndGetParser(grammar)
        );

        this.checkEquals(
                expected.getMessage(),
                thrown.getMessage(),
                () -> "parseGrammar\n" + grammar
        );
    }

    private void parseGrammarAndGetParserAndParseCheck(final String grammar,
                                                       final String text) {
        this.parseGrammarAndGetParserAndParseCheck(
                this.parseGrammarAndGetParser(grammar),
                text
        );
    }

    private void parseGrammarAndGetParserAndParseCheck(final Parser<FakeParserContext> parser,
                                                       final String text) {
        this.parseAndCheck(
                parser,
                text,
                this.string(text),
                text,
                ""
        );
    }

    private void parseGrammarAndGetParserAndParseCheck(final Parser<FakeParserContext> parser,
                                                       final String text,
                                                       final ParserToken expected,
                                                       final String expectedText) {
        this.parseAndCheck(
                parser,
                text,
                expected,
                expectedText,
                ""
        );
    }

    private void parseGrammarAndGetParserAndParseCheck(final String grammar,
                                                       final String text,
                                                       final ParserToken expected,
                                                       final String expectedText,
                                                       final String textAfter) {
        this.parseAndCheck(
                this.parseGrammarAndGetParser(grammar),
                text,
                expected,
                expectedText,
                textAfter
        );
    }

    private void parseGrammarAndGetParserAndParseCheck(final Parser<FakeParserContext> parser,
                                                       final String text,
                                                       final ParserToken expected,
                                                       final String expectedText,
                                                       final String textAfter) {
        this.parseAndCheck(
                parser,
                text,
                expected,
                expectedText,
                textAfter
        );
    }

    /**
     * Parses the grammar file, uses the transformer to convert each rule into parsers and then returns the parser for the rule called "TEST".
     */
    private Parser<FakeParserContext> parseGrammarAndGetParser(final String grammar) {
        return this.parseGrammarAndGetParser(
                grammar,
                this.syntaxTreeTransformer()
        );
    }

    private Parser<FakeParserContext> parseGrammarAndGetParser(final String grammar,
                                                               final EbnfParserCombinatorSyntaxTreeTransformer<FakeParserContext> transformer) {
        final EbnfGrammarParserToken grammarToken = this.parseGrammar(grammar);

        final Function<EbnfIdentifierName, Optional<Parser<FakeParserContext>>> nameToParser = this.parseGrammarAndGetParsers(
                grammarToken,
                transformer
        );
        final Parser<FakeParserContext> test = nameToParser.apply(TEST)
                .orElse(null);
        failIfOptionalParser(
                test,
                () -> "OptionalParser returned by named parser lookup " + test
        );
        this.checkNotEquals(
                null,
                test,
                () -> "Parser " + TEST + " not found in grammar\n" + grammar
        );
        return test;
    }

    private final static EbnfIdentifierName TEST = EbnfIdentifierName.with("TEST");

    private EbnfGrammarParserToken parseGrammar(final String grammar) {
        return EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(
                        TextCursors.charSequence(grammar),
                        EbnfParserContexts.basic()
                ).get()
                .cast(EbnfGrammarParserToken.class);
    }

    private Function<EbnfIdentifierName, Optional<Parser<FakeParserContext>>> parseGrammarAndGetParsers(final EbnfGrammarParserToken grammar) {
        return this.parseGrammarAndGetParsers(
                grammar,
                this.syntaxTreeTransformer()
        );
    }

    private Function<EbnfIdentifierName, Optional<Parser<FakeParserContext>>> parseGrammarAndGetParsers(final EbnfGrammarParserToken grammar,
                                                                                                        final EbnfParserCombinatorSyntaxTreeTransformer<FakeParserContext> transformer) {
        final Map<EbnfIdentifierName, Parser<FakeParserContext>> defaults = Maps.hash();
        defaults.put(
                EbnfIdentifierName.with("ONLY_LETTERS"),
                Parsers.stringCharPredicate(
                        CharPredicates.letter(),
                        1,
                        Integer.MAX_VALUE
                ).cast()
        );
        defaults.put(
                EbnfIdentifierName.with("DUPLICATED"),
                Parsers.fake()
        );

        return grammar.combinator(
                (n) -> Optional.ofNullable(
                        defaults.get(n)
                ),
                transformer
        );
    }

    private EbnfParserCombinatorSyntaxTreeTransformer<FakeParserContext> syntaxTreeTransformer() {
        return new EbnfParserCombinatorSyntaxTreeTransformer<>() {
            @Override
            public Parser<FakeParserContext> alternatives(final EbnfAlternativeParserToken token,
                                                          final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.alternatives got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> concatenation(final EbnfConcatenationParserToken token,
                                                           final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.concatenation got " + parser
                );
                return parser.transform((sequenceParserToken, fakeParserContext) -> sequenceParserToken);
            }

            @Override
            public Parser<FakeParserContext> exception(final EbnfExceptionParserToken token,
                                                       final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.exception got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> group(final EbnfGroupParserToken token,
                                                   final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.group got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> identifier(final EbnfIdentifierParserToken token,
                                                        final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.identifier got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> optional(final EbnfOptionalParserToken token,
                                                      final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.optional got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> range(final EbnfRangeParserToken token,
                                                   final String beginText,
                                                   final String endText) {
                checkEquals("a", beginText, "beginText");
                checkEquals("z", endText, "endText");

                return Parsers.<FakeParserContext>stringCharPredicate(
                                CharPredicates.range(
                                        'a',
                                        'z'
                                ),
                                1,
                                1
                        ).setToString(token.toString())
                        .cast();
            }

            @Override
            public Parser<FakeParserContext> repeated(final EbnfRepeatedParserToken token,
                                                      final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.repeated got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> rule(final EbnfRuleParserToken token,
                                                  final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.rule got " + parser
                );
                return parser;
            }

            @Override
            public Parser<FakeParserContext> terminal(final EbnfTerminalParserToken token,
                                                      final Parser<FakeParserContext> parser) {
                failIfOptionalParser(
                        parser,
                        () -> "EbnfParserCombinatorSyntaxTreeTransformer.terminal got " + parser
                );

                return parser.transform((stringParserToken, contextIgnored) -> {
                    ParserToken result = stringParserToken;
                    try {
                        result = number(((StringParserToken) stringParserToken).value());
                    } catch (final NumberFormatException ignore) {
                    }
                    return result;
                }).cast();
            }
        };
    }

    private void failIfOptionalParser(final Parser<?> parser,
                                      final Supplier<String> message) {
        if (parser instanceof EbnfParserCombinatorOptionalParser) {
            throw new IllegalArgumentException(message.get());
        }
    }

    private BigIntegerParserToken number(final String text) {
        return ParserTokens.bigInteger(new BigInteger(text), text);
    }

    private StringParserToken string(final String text) {
        return ParserTokens.string(text, text);
    }

    private SequenceParserToken sequence(final ParserToken... tokens) {
        final List<ParserToken> list = Lists.of(
                tokens
        );

        return ParserTokens.sequence(
                list,
                ParserToken.text(
                        list
                )
        );
    }

    @Override
    public FakeParserContext createContext() {
        return new FakeParserContext();
    }

    // toString.........................................................................................................

    @Test
    public void testParserToString() {
        this.checkEquals(
                "\"text\"",
                this.parseGrammarAndGetParser("TEST=\"text\";")
                        .toString()
        );
    }

    @Test
    public void testParserToString2() {
        this.checkEquals(
                "(\"concat-terminal-1\", \"concat-terminal-2\", [\"optional-concat-3\"])",
                this.parseGrammarAndGetParser("TEST=\"concat-terminal-1\", \"concat-terminal-2\", [\"optional-concat-3\"];")
                        .toString()
        );
    }

    // Class ...........................................................................................................

    @Override
    public Class<EbnfParserCombinators> type() {
        return EbnfParserCombinators.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // PublicStaticHelperTesting........................................................................................

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Test
    public void testPublicStaticMethodsWithoutMathContextParameter() {
        this.publicStaticMethodParametersTypeCheck(MathContext.class);
    }
}
