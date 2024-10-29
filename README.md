[![Build Status](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser-ebnf/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser-ebnf?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-text-cursor-parser-ebnf.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser-ebnf/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-text-cursor-parser-ebnf.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser-ebnf/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-text-cursor-parser-ebnf)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



Support for parsing EBNF grammar files and assembling [parser combinators](https://en.wikipedia.org/wiki/Parser_combinator).

# Grammars
A grammar is a text description of the tokens of a language. The definition of any token may be a series of characters
or other tokens.

Some important attributes and ordering of tokens includes:
- *Required*: Some tokens are required.
- *Optional*: Some tokens are optional, eg whitespace within an expression.
- *Repetition*: An example of this might be a whitespace token which may be defined as any sequence of repeating
  whitespace characters.
- *Concatenation*: A concatenation is a series of required/optional tokens in a prescribed order. 
- *Alternatives*: A series of choices, tried in order until one succeeds.

The parsing framework, supports abstractions for the above mentioned concepts.

- [Parsers](https://github.com/mP1/walkingkooka-text-cursor-parser/blob/master/src/main/java/walkingkooka/text/cursor/parser/Parsers.java)



## Extended Backus Naur form

Rather than building grammars in java which can be very verbose a text DSL is preferable for readability and brevity.
A popular format is [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form), in one of its various
standard but different forms. The differences between these variations is the use of different symbols to express
the cardinality of a token.

The file below is an example of the json grammar used to define JSON in this system.

[EBNF Json grammar](https://github.com/mP1/walkingkooka-tree-json/blob/master/src/main/resources/walkingkooka/text/cursor/parser/json/json-parsers.grammar)

```ebnf
VALUE=                  NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT;
VALUE_REQUIRED=         VALUE;

ARRAY_ELEMENT=          [ WHITESPACE ], VALUE;
ARRAY_ELEMENT_REQUIRED= [ WHITESPACE ], VALUE_REQUIRED;

ARRAY=                  ARRAY_BEGIN,
                        ARRAY_REQUIRED;

ARRAY_REQUIRED=         [ ARRAY_ELEMENT, [{ [ WHITESPACE ], SEPARATOR, ARRAY_ELEMENT_REQUIRED }]],
                        [ WHITESPACE ],
                        ARRAY_END;

OBJECT_PROPERTY_REQUIRED=OBJECT_PROPERTY;
OBJECT_PROPERTY        =[ WHITESPACE ], STRING, [ WHITESPACE ], OBJECT_ASSIGNMENT, [ WHITESPACE ], VALUE_REQUIRED;

OBJECT=                 OBJECT_BEGIN,
                        OBJECT_REQUIRED;
OBJECT_REQUIRED=        [ OBJECT_PROPERTY, [{[ WHITESPACE ], SEPARATOR, OBJECT_PROPERTY_REQUIRED }]],
                        [ WHITESPACE ],
                        OBJECT_END;
```

To assist understanding the grammar:

- OPTIONAL: square brackets "[]" mean that token is optional and may appear zero or once.
- REPETITION: curly brackets "{}" denote repetition, meaning the token may appear zero or more times.
- REQUIRED:  any token without either square or curly brackets is required and must appear only once.
- ALTERNATIVES: a list of possible tokens choices separated by pipes "|", each will be tried until one is a successful match.
- SEQUENCE: a list of tokens separated by commas ",", each must appear in that order.

Note rules have a few basic limitations, with a goal of enforcing small re-usable components identified by a name which
can then be referenced by other rules within the same grammar.


## Repetition

```
REPEATING = { SPACE }
```

Defines a rule that matches zero or more spaces, lets pretend SPACE means " " or char 32 which means the above rule
matches a sequence of zero or more SPACES.



## Alternatives

```
ALTERNATIVES = "NSW" | "QLD" | "VIC" 
```

A list of alternatives. Each choice in the list is tried until one is a match and the remaining are then ignored.



## Sequence

```
SEQUENCE = FIRST, [SECOND], {THIRD}
```

A sequence which includes a combination of required, optional and repeated tokens.

- FIRST is required.
- SECOND is optional.
- THIRD may appear zero or more times.


A sequence may not also include alternatives, the alternatives must be defined in a separate rule, and that rule name included in place.

```
STATES         = "NSW" | "QLD" | "VIC" | "SA" | "WA" | "TAS" | "ACT" | "NT"
POSTAL_ADDRESS = STREET_NUMBER, STREET_NAME, STREET_TYPE, STATE
```



## [text.cursor.parser.ebnf.*](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf/tree/master/src/main/java/walkingkooka/text/cursor/parser/ebnf)
This package contains parsers and parser tokens that may be used to parse a well formed ebnf file including comments
into a `EbnfGrammarParserToken` for further processing.



### [Transform EbnfGrammarParserToken into parsers](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf/blob/master/src/main/java/walkingkooka/text/cursor/parser/ebnf/EbnfGrammarParserToken.java)
This token holds an entire Ebnf text file. The `EbnfGrammarParserToken.combinator(...)` method then accepts
[EbnfParserCombinatorSyntaxTreeTransformer](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf/blob/master/src/main/java/walkingkooka/text/cursor/parser/ebnf/combinator/EbnfParserCombinatorSyntaxTreeTransformer.java)
which can then perform tasks such as creating parsers. As mentioned previously this is how the grammar file above is
turned into parsers.



## [Transform Ebnf grammar text file into CharPredicates](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate)
The `EbnfGrammarCharPredicates.fromGrammar(...)` accepts a EBNF text file and returns a Map of name to `CharPredicate`. This is
another use case of the possibilities of grammars and using a visitor to turn tokens into something else.



## Internet standards
As mentioned previously many internet standards use EBNF or a flavour to express text format. The files can then be
used to create `CharPredicate` or `Parsers`.

Some examples include:

- [Http](https://tools.ietf.org/html/rfc7230)
- [BNF for URL schemes](https://www.w3.org/Addressing/URL/5_BNF.html)

Its relatively easy to find more simply search for `EBNF` followed by the name of the technology or artifact.

