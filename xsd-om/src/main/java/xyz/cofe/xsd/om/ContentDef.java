package xyz.cofe.xsd.om;

public sealed interface ContentDef permits ElementContent,
                                           XsdComplexContent,
                                           XsdSimpleContent {
}
