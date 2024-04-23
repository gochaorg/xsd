package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;

public sealed interface Xsd extends ElemMethod permits
                                               XsdAll,
                                               XsdAnnotation,
                                               XsdAnnotation.AnnotationProperty,
                                               XsdAny,
                                               XsdAnyAttribute,
                                               XsdAppinfo,
                                               XsdAttribute,
                                               XsdAttributeGroup,
                                               XsdChoice,
                                               XsdComplexContent,
                                               XsdComplexType,
                                               XsdDocumentation,
                                               XsdElement,
                                               XsdExtension,
                                               XsdField,
                                               XsdGroup,
                                               XsdImport,
                                               XsdInclude,
                                               XsdKey,
                                               XsdKeyref,
                                               XsdList,
                                               XsdNotation,
                                               XsdRedefine,
                                               XsdRestriction,
                                               XsdSchema,
                                               XsdSelector,
                                               XsdSequence,
                                               XsdSimpleContent,
                                               XsdSimpleType,
                                               XsdUnion,
                                               XsdUnique {
}
