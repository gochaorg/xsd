package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая реализация
 *
 * @param name       имя
 * @param typeParams параметры типы
 * @param baseTypes  базовый тип/типы
 */
public record Generic(
    String name,
    ImList<TypeParam> typeParams,
    ImList<Type> baseTypes
) implements GenericType,
             ExtendType,
             NamedType {

    @Override
    public String getTypeName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);

        NumToSym numToSym = new NumToSym.NumToSymBasic();

        Map<TypeParam, String> map =
            typeParams.enumerate().toMap(eidx -> {
                String name = numToSym.numToSym(eidx.index());
                TypeParam tp = eidx.value();

                var constraints = tp.constraints()
                    .map( ctype -> ctype instanceof NamedType nt ? nt.getTypeName() : "")
                    .foldLeft( "", (acc,it) -> acc.isEmpty() ? it : acc+"+"+it );
                constraints = constraints.isBlank() ? "" : ":"+ constraints;

                String sign = switch (tp.coPos()){
                    case InVariant -> "";
                    case Param -> "+";
                    case Result -> "-";
                };

                name = sign + name + constraints;
                return Tuple2.of(
                    tp,
                    name
                );
            });

        if(!map.isEmpty()){
            sb.append("<");
            sb.append(
                typeParams
                    .map(map::get)
                    .foldLeft("", (acc, it) -> !acc.isEmpty() ? acc+", "+it : it )
            );
            sb.append(">");
        }

        return sb.toString();
    }
}
