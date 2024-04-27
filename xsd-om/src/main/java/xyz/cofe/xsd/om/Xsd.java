package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;

import java.util.Optional;

public sealed interface Xsd extends ElemMethod permits ElementsLayout.NestedProperty,
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
    Optional<Xsd> getParent();

    default Optional<XsdSchema> getSchemaOwner() {
        Xsd xsd = this;
        while (true) {
            if (xsd instanceof XsdSchema s) return Optional.of(s);
            var p = xsd.getParent();
            if (p.isPresent()) {
                xsd = p.get();
            } else {
                return Optional.empty();
            }
        }
    }
}
