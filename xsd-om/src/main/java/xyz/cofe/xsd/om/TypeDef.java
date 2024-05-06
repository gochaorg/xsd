package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;

public sealed interface TypeDef
    extends NameAttribute,
            XsdAnnotation.AnnotationProperty
    permits XsdComplexType,
            XsdSimpleType {

    public static Result<ImList<TypeDef>, String> resolveTypeDefs(BuiltInTypes.QName typeQName, XsdSchema schemaOwner) {
        if (typeQName == null) throw new IllegalArgumentException("typeQName==null");
        if (schemaOwner == null) throw new IllegalArgumentException("schemaOwner==null");

        var ns = Result.of(typeQName.prefix(), "type prefix not found").fold(
            schemaOwner::findNamespaceByPrefix,
            ignore -> schemaOwner.getSelfNamespace()
        );

        return ns.map(
            ns0 -> ns0.getTypeDefs().filter(
                td -> td.getName().fold(
                    typeName -> typeName.value().equals(typeQName.localPart()),
                    err -> false
                )
            )
        );
    }

    public static Result<ImList<TypeDef>, String> resolveTypeDefs(BuiltInTypes.QName typeQName, Xsd typeQNameContainer) {
        if (typeQName == null) throw new IllegalArgumentException("typeQName==null");
        if (typeQNameContainer == null) throw new IllegalArgumentException("typeQNameContainer==null");

        return Result.of(
            typeQNameContainer.getSchemaOwner(),
            "schemaOwner not found"
        ).flatMap(schemaOwner -> resolveTypeDefs(typeQName, schemaOwner));
    }
}
