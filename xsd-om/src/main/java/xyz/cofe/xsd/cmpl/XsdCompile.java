package xyz.cofe.xsd.cmpl;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xsd.om.BuiltInTypes;
import xyz.cofe.xsd.om.ElementContent;
import xyz.cofe.xsd.om.TypeDef;
import xyz.cofe.xsd.om.XsdAll;
import xyz.cofe.xsd.om.XsdAnnotation;
import xyz.cofe.xsd.om.XsdAny;
import xyz.cofe.xsd.om.XsdAttribute;
import xyz.cofe.xsd.om.XsdChoice;
import xyz.cofe.xsd.om.XsdComplexContent;
import xyz.cofe.xsd.om.XsdComplexType;
import xyz.cofe.xsd.om.XsdDocumentation;
import xyz.cofe.xsd.om.XsdElement;
import xyz.cofe.xsd.om.XsdExtension;
import xyz.cofe.xsd.om.XsdGroup;
import xyz.cofe.xsd.om.XsdRestriction;
import xyz.cofe.xsd.om.XsdSequence;
import xyz.cofe.xsd.om.XsdSimpleContent;
import xyz.cofe.xsd.om.XsdSimpleType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.cofe.xsd.text.Indent.indent;

public class XsdCompile {
    private final AtomicInteger typeNameSeqId = new AtomicInteger();
    private final Map<TypeDef, BuiltInTypes.NCNAME> typeNames = new HashMap<>();

    private BuiltInTypes.NCNAME typeNameOf(TypeDef typeDef) {
        BuiltInTypes.NCNAME name = typeNames.get(typeDef);
        if (name != null) return name;

        name = typeDef.getName().fold(n -> n, err -> new BuiltInTypes.NCNAME("_Unnamed" + typeNameSeqId.incrementAndGet()));
        typeNames.put(typeDef, name);

        return name;
    }

    public String compile(XsdElement el) {
        return el.getRefType().fold(td -> {
            var text = "type " + typeNameOf(td).value() + "\n";

            var ann = td.getAnnotations().fmap(XsdAnnotation::getDocumentations).map(XsdDocumentation::getText)
                .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);

            if (!ann.isBlank()) {
                text = text + "annotation: \n";
                text = text + indent("    ", ann) + "\n";
            }

            if (td instanceof XsdComplexType ct) {
                text = text + compile(ct) + "\n";
            } else if (td instanceof XsdSimpleType st) {
                text = text + compile(st) + "\n";
            }

            return text;
        }, err -> err);
    }

    private String compile(XsdComplexType ct) {
        var content = ct.getContentDef().map(content0 -> {
            if (content0 instanceof XsdComplexContent cc) {
                return compile(cc);
            } else if (content0 instanceof ElementContent ec) {
                return compile(ec);
            } else if (content0 instanceof XsdSimpleContent sc) {
                return compile(sc);
            }
            return "??";
        }).orElse("");

        var ctName = ct.getName().map(BuiltInTypes.NCNAME::value).fold(a -> a, b -> b);
        var attr = compileAttributes(ct.getAttributes());

        var ann = ct.getAnnotations().fmap(XsdAnnotation::getDocumentations).map(XsdDocumentation::getText)
            .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);

        var str = "XsdComplexType " + ctName;
        if (!ann.isBlank()) str = str + "\n" + indent("// ", ann);
        if (!attr.isBlank()) str = str + "\nattr {\n" + indent("  ", attr) + "\n}";
        if (!content.isBlank()) str = str + "\ncontent {\n" + indent("  ", content) + "\n}";

        return str;
    }

    private String compile(ElementContent ec) {
        var attrs = compileAttributes(ec.getAttributes());

        return "ElementContent {\n" +
            indent("  ", attrs) + "\n" +
            "}";
    }

    private String compile(XsdComplexContent cc) {
        return cc.getNested().fold(nested -> {
            if (nested instanceof XsdExtension ext) {
                return compile(ext);
            } else if (nested instanceof XsdRestriction rst) {
                return compile(rst);
            }
            return "!!! XsdComplexContent getNested";
        }, err -> "!! XsdComplexContent getNested: " + err);
    }

    private String compile(TypeDef td) {
        if (td instanceof XsdSimpleType st) return compile(st);
        if (td instanceof XsdComplexType ct) return compile(ct);
        return "??? compile( TypeDef td )";
    }

    private String compile(XsdExtension ext) {
        var extend = Result.from(ext.getBaseAttribute(), ()->"")
            .fmap(ignore -> ext.getRefType())
            .map(this::compile)
            .fold(a -> a, b -> b);

        var nested = ext.getNested().map(nst -> {
            if (nst instanceof XsdGroup g) {
                return compile(g);
            } else if (nst instanceof XsdSequence s) {
                return compile(s);
            } else if (nst instanceof XsdAll a) {
                return compile(a);
            } else if (nst instanceof XsdChoice c) {
                return compile(c);
            }
            return "???";
        }).orElse("_");

        var attrs = compileAttributes(ext.getAttributes());

        var body =
            "body of XsdExtension(" + ext.getParentTypeDef().map(td -> td.getName().fold(n -> n.value(), b -> b)).orElse("?") + ") {\n" +
                indent("  ", nested) + "\n" +
                indent("  ", attrs) + "\n" +
                "}";

        if (!extend.isBlank()) {
            return "extend from " + ext.getRefType().fmap(rt -> rt.getName().map(BuiltInTypes.NCNAME::value)).fold(a -> a, b -> b) + " {\n" +
                indent("  ", extend) + "\n" +
                "}\n" +
                body;
        }

        return body;
    }

    private static String compileAttributes(ImList<XsdAttribute> attrs) {
        var fieldDef = attrs
            .map(attr -> {
                    var doc = attr.getAnnotations()
                        .fmap(XsdAnnotation::getDocumentations)
                        .map(XsdDocumentation::getText)
                        .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);


                    var defenition = attr.getName().map(BuiltInTypes.NCNAME::value)
                        .fold(n -> n, e -> e) +

                        " : " +

                        attr.getType().map(qName -> qName.prefix().map(p -> p + "::").orElse("") + qName.localPart())
                            .fold(n -> n, e -> e);

                    if (!doc.isBlank()) {
                        return
                            indent("// ", doc) + "\n" +
                                defenition + "\n";
                    }

                    return defenition;
                }
            )
            .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);

        return fieldDef;
    }

    private String compile(XsdGroup rst) {
        return "XsdGroup";
    }

    private String compile(XsdSequence seq) {
        var seqBody = seq.getNested().map(ell -> {
            if (ell instanceof XsdAll a) {
                return compile(a);
            } else if (ell instanceof XsdAny a) {
                return "__any__";
            } else if (ell instanceof XsdChoice a) {
                return compile(a);
            } else if (ell instanceof XsdGroup a) {
                return compile(a);
            } else if (ell instanceof XsdSequence a) {
                return compile(a);
            } else if (ell instanceof XsdElement el) {
                var fieldDef = el.getName().map(BuiltInTypes.NCNAME::value).fold(a -> a, b -> b) +
                    " : " +
                    el.getType()
                        .map(qn -> {
                            var typeName = qn.prefix().map(s -> s + "::").orElse("") + qn.localPart();

                            var minOcc = el.getMinOccurs().fold(v -> v.value().intValue(), ignore -> 1);

                            // maxOcc = -1 - максимальная граница не задана
                            var maxOcc = el.getMaxOccurs().fold(v -> v.fold(i -> i.value().intValue(), ingore2 -> -1), ignore -> 1);

                            if (minOcc == 1 && maxOcc == 1) return typeName;
                            if (minOcc == 0 && maxOcc == 1) return "Optional<" + typeName + ">";

                            if (maxOcc < 0) {
                                return typeName + "+List{" + minOcc + "..*}";
                            }

                            return typeName + "+List{" + minOcc + ".." + maxOcc + "}";
                        })
                        .fold(a -> a, b -> b);

                var doc = el.getAnnotations()
                    .fmap(XsdAnnotation::getDocumentations)
                    .map(XsdDocumentation::getText)
                    .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);

                if (!doc.isBlank())
                    return
                        indent("// ", doc) + "\n" +
                            fieldDef + "\n";

                return fieldDef;
            }
            return "?";
        }).foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + "\n" + it);

        return "seq {\n" +
            indent("  ", seqBody) + "\n" +
            "}";
    }

    private String compile(XsdAll rst) {
        return "XsdAll";
    }

    private String compile(XsdChoice rst) {
        return "XsdChoice";
    }

    private String compile(XsdRestriction rst) {
        return "XsdRestriction";
    }

    private String compile(XsdSimpleContent sc) {
        return "XsdSimpleContent\n";
    }

    private String compile(XsdSimpleType st) {
        return "XsdSimpleType\n";
    }
}
