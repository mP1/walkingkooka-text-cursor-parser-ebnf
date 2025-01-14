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
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

public final class EbnfRuleParserTest extends EbnfParserTestCase2<EbnfRuleParserToken> {

    @Test
    public void testParseIdentifierOnlyFails() {
        this.parseThrowsEndOfText(IDENTIFIER1_TEXT);
    }

    @Test
    public void testParseIdentifierAndAssignmentOnlyFails() {
        this.parseThrowsEndOfText(IDENTIFIER1_TEXT + ASSIGNMENT);
    }

    @Test
    public void testParseAssignmentOnlyFails() {
        this.parseFailAndCheck(ASSIGNMENT);
    }

    @Test
    public void testParseDoubleTerminalFails() {
        this.parseThrows(IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + ASSIGNMENT + TERMINAL1_TEXT, ASSIGNMENT.charAt(0), 21, 1);
    }

    @Test
    public void testParseIdentifierWhitespaceAssignmentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + WHITESPACE1 + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(text,
                rule(text, identifier1(), whitespace1(), assignmentToken(), terminal1(), terminatorToken()),
                text);
    }

    @Test
    public void testParseIdentifierCommentAssignmentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + COMMENT1 + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(text,
                rule(text, identifier1(), comment1(), assignmentToken(), terminal1(), terminatorToken()),
                text);
    }

    @Test
    public void testParseIdentifierWhitespaceCommentAssignmentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + WHITESPACE1 + COMMENT1 + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), whitespace1(), comment1(), assignmentToken(), terminal1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierWhitespaceCommentWhitespaceAssignmentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + WHITESPACE1 + COMMENT1 + WHITESPACE2 + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), whitespace1(), comment1(), whitespace2(), assignmentToken(), terminal1(), terminatorToken()),
                text
        );
    }

    // assignment

    @Test
    public void testParseIdentifierAssignmentWhitespaceTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), terminal1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentCommentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + COMMENT1 + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), comment1(), terminal1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentWhitespaceCommentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + COMMENT1 + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), comment1(), terminal1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentWhitespaceCommentWhitespaceTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + COMMENT1 + WHITESPACE2 + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), comment1(), whitespace2(), terminal1(), terminatorToken()),
                text
        );
    }

    // terminator

    @Test
    public void testParseIdentifierAssignmentTerminalWhitespaceTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + WHITESPACE1 + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), terminal1(), whitespace1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentTerminalCommentTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + COMMENT1 + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), terminal1(), comment1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentTerminalWhitespaceCommentTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + WHITESPACE1 + COMMENT1 + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), terminal1(), whitespace1(), comment1(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentTerminalWhitespaceCommentWhitespaceTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + WHITESPACE1 + COMMENT1 + WHITESPACE2 + TERMINATOR;
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), terminal1(), whitespace1(), comment1(), whitespace2(), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseIdentifierAssignmentTerminalTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;
        this.parseAndCheck(
                text + WHITESPACE1,
                rule(text, identifier1(), assignmentToken(), terminal1(), terminatorToken()),
                text,
                WHITESPACE1
        );
    }

    @Test
    public void testParseIdentifierAssignmentIdentifierTerminator() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + IDENTIFIER2_TEXT + TERMINATOR;
        this.parseAndCheck(
                text + WHITESPACE1,
                rule(text, identifier1(), assignmentToken(), identifier2(), terminatorToken()),
                text,
                WHITESPACE1
        );
    }

    @Test
    public void testParseAlternatives() {
        final String altText = TERMINAL1_TEXT + ALTERNATIVE + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + altText + TERMINATOR;

        final EbnfParserToken alt = EbnfParserToken.alternative(Lists.of(terminal1(), altToken(), terminal2()), altText);
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), alt, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseAlternativesGroup() {
        final String altText = openGroupToken() + TERMINAL1_TEXT + closeGroupToken() + ALTERNATIVE + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + altText + TERMINATOR;

        final ParserToken group = EbnfParserToken.group(Lists.of(openGroupToken(), terminal1(), closeGroupToken()), OPEN_GROUP + TERMINAL1_TEXT + CLOSE_GROUP);
        final EbnfParserToken alt = EbnfParserToken.alternative(Lists.of(group, altToken(), terminal2()), altText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), alt, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseAlternativesOptional() {
        final String altText = openOptionalToken() + TERMINAL1_TEXT + closeOptionalToken() + ALTERNATIVE + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + altText + TERMINATOR;

        final ParserToken opt = EbnfParserToken.optional(Lists.of(openOptionalToken(), terminal1(), closeOptionalToken()), OPEN_OPTIONAL + TERMINAL1_TEXT + CLOSE_OPTIONAL);
        final EbnfParserToken alt = EbnfParserToken.alternative(Lists.of(opt, altToken(), terminal2()), altText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), alt, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseAlternativesRepeat() {
        final String altText = openRepeatToken() + TERMINAL1_TEXT + closeRepeatToken() + ALTERNATIVE + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + altText + TERMINATOR;

        final ParserToken repeated = EbnfParserToken.repeated(Lists.of(openRepeatToken(), terminal1(), closeRepeatToken()), OPEN_REPEAT + TERMINAL1_TEXT + CLOSE_REPEAT);
        final EbnfParserToken alt = EbnfParserToken.alternative(Lists.of(repeated, altToken(), terminal2()), altText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), alt, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseConcatenation() {
        final String concatText = TERMINAL1_TEXT + CONCAT + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + concatText + TERMINATOR;

        final EbnfParserToken concat = EbnfParserToken.concatenation(Lists.of(terminal1(), concatToken(), terminal2()), concatText);
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), concat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseConcatenationsGroup() {
        final String concatText = openGroupToken() + TERMINAL1_TEXT + closeGroupToken() + CONCAT + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + concatText + TERMINATOR;

        final ParserToken group = EbnfParserToken.group(Lists.of(openGroupToken(), terminal1(), closeGroupToken()), OPEN_GROUP + TERMINAL1_TEXT + CLOSE_GROUP);
        final EbnfParserToken concat = EbnfParserToken.concatenation(Lists.of(group, concatToken(), terminal2()), concatText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), concat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseConcatenationsOptional() {
        final String concatText = openOptionalToken() + TERMINAL1_TEXT + closeOptionalToken() + CONCAT + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + concatText + TERMINATOR;

        final ParserToken opt = EbnfParserToken.optional(Lists.of(openOptionalToken(), terminal1(), closeOptionalToken()), OPEN_OPTIONAL + TERMINAL1_TEXT + CLOSE_OPTIONAL);

        final EbnfParserToken concat = EbnfParserToken.concatenation(Lists.of(opt, concatToken(), terminal2()), concatText);
        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), concat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseConcatenationsRepeat() {
        final String concatText = openRepeatToken() + TERMINAL1_TEXT + closeRepeatToken() + CONCAT + TERMINAL2_TEXT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + concatText + TERMINATOR;

        final ParserToken repeated = EbnfParserToken.repeated(Lists.of(openRepeatToken(), terminal1(), closeRepeatToken()), OPEN_REPEAT + TERMINAL1_TEXT + CLOSE_REPEAT);
        final EbnfParserToken concat = EbnfParserToken.concatenation(Lists.of(repeated, concatToken(), terminal2()), concatText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), concat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseGroup() {
        final String groupText = OPEN_GROUP + TERMINAL1_TEXT + CLOSE_GROUP;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + groupText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken group = EbnfParserToken.group(Lists.of(openGroupToken(), terminal, closeGroupToken()), groupText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), group, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseWhitespaceGroup() {
        final String groupText = OPEN_GROUP + TERMINAL1_TEXT + CLOSE_GROUP;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + groupText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken group = EbnfParserToken.group(Lists.of(openGroupToken(), terminal, closeGroupToken()), groupText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), group, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseOptional() {
        final String optionalText = OPEN_OPTIONAL + TERMINAL1_TEXT + CLOSE_OPTIONAL;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + optionalText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken optional = EbnfParserToken.optional(Lists.of(openOptionalToken(), terminal, closeOptionalToken()), optionalText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), optional, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseWhitespaceOptional() {
        final String optionalText = OPEN_OPTIONAL + TERMINAL1_TEXT + CLOSE_OPTIONAL;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + optionalText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken optional = EbnfParserToken.optional(Lists.of(openOptionalToken(), terminal, closeOptionalToken()), optionalText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), optional, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseRepeat() {
        final String repeatText = OPEN_REPEAT + TERMINAL1_TEXT + CLOSE_REPEAT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + repeatText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken repeat = EbnfParserToken.repeated(Lists.of(openRepeatToken(), terminal, closeRepeatToken()), repeatText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), repeat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseWhitespaceRepeat() {
        final String repeatText = OPEN_REPEAT + TERMINAL1_TEXT + CLOSE_REPEAT;
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + WHITESPACE1 + repeatText + TERMINATOR;

        final EbnfParserToken terminal = terminal1();
        final EbnfParserToken repeat = EbnfParserToken.repeated(Lists.of(openRepeatToken(), terminal, closeRepeatToken()), repeatText);

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), whitespace1(), repeat, terminatorToken()),
                text
        );
    }

    @Test
    public void testParseTerminatorComments() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + TERMINATOR;

        this.parseAndCheck(
                text + COMMENT1,
                rule(text, identifier1(), assignmentToken(), terminal1(), terminatorToken()),
                text,
                COMMENT1
        );
    }

    @Test
    public void testParseIncludesRange() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + TERMINAL1_TEXT + BETWEEN + TERMINAL2_TEXT + TERMINATOR;

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), range(TERMINAL1_TEXT + BETWEEN + TERMINAL2_TEXT, terminal1(), between(), terminal2()), terminatorToken()),
                text
        );
    }

    @Test
    public void testParseExceptionFails() {
        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + EXCEPTION + TERMINAL2_TEXT + TERMINATOR;

        this.parseThrows(
                text,
                EXCEPTION.charAt(0),
                8,
                1
        );
    }

    @Test
    public void testParseTokenThenException() {
        final String exceptionText = TERMINAL1_TEXT + EXCEPTION + TERMINAL2_TEXT;
        final EbnfParserToken exception = EbnfParserToken.exception(Lists.of(terminal1(), exceptionToken(), terminal2()), exceptionText);

        final String text = IDENTIFIER1_TEXT + ASSIGNMENT + exceptionText + TERMINATOR;

        this.parseAndCheck(
                text,
                rule(text, identifier1(), assignmentToken(), exception, terminatorToken()),
                text
        );
    }

    @Override
    public Parser<EbnfParserContext> createParser() {
        return EbnfGrammarParser.RULE;
    }

    @Override
    String text() {
        return "identifier123=\"terminal456\";";
    }

    @Override
    EbnfRuleParserToken token(final String text) {
        return rule(text,
                EbnfParserToken.identifier(IDENTIFIER1, IDENTIFIER1_TEXT),
                assignmentToken(),
                EbnfParserToken.terminal(TERMINAL1, TERMINAL1_TEXT),
                terminatorToken());
    }
}
