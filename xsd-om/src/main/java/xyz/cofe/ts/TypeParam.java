package xyz.cofe.ts;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.coll.im.Result.NoValue;

import static xyz.cofe.coll.im.Result.ok;

/**
 * Объявление параметра типа
 */
public final class TypeParam {
    public TypeParam(ImList<Type> constraints, CoPos coPos) {
        this.constraints = constraints;
        this.coPos = coPos;
    }

    public static TypeParam createParam(Type constraint){
        if( constraint==null ) throw new IllegalArgumentException("constraint==null");
        return new TypeParam(ImList.of(constraint), CoPos.Param);
    }

    public static TypeParam createResult(Type constraint){
        if( constraint==null ) throw new IllegalArgumentException("constraint==null");
        return new TypeParam(ImList.of(constraint), CoPos.Result);
    }

    private final ImList<Type> constraints;

    /**
     * Возвращает ограничения на тип
     *
     * @return ограничения на тип
     */
    public ImList<Type> constraints() {
        return this.constraints;
    }

    private final CoPos coPos;

    /**
     * Возвращает вариантную позицию
     *
     * @return вариантная позиция
     */
    public CoPos coPos() {
        return coPos;
    }

    /**
     * Проверка допустимости типа
     *
     * @param typeValue тип
     * @return допустимо использовать в качестве параметра или нет
     */
    public Result<NoValue, String> isAssignableFrom(TypeValue typeValue) {
        if (typeValue == null) throw new IllegalArgumentException("typeValue==null");
        if (typeValue instanceof TypeVar tv) return isAssignableFrom(tv);
        if (typeValue instanceof Type t) return isAssignableFrom(t);
        return Result.error("!! bug");
    }

    private Result<NoValue, String> isAssignableFrom(TypeVar typeVar) {
        if (coPos != typeVar.typeParam().coPos()) {
            return Result.error("CoVaraint position not match, expect " + coPos + ", found " + typeVar.typeParam().coPos());
        }

        if (constraints.isEmpty()) return ok(NoValue.instance);

        ImList<Result<NoValue, String>> constraintCheck =
            constraints.enumerate().map(en -> {
                var constrIdx = en.index();
                var expectType = en.value();

                var produceTypes = typeVar.typeParam().constraints();
                if (produceTypes.isEmpty()) {
                    return Result.error(
                        "constraint#" + constrIdx +
                            "\n  expect type: " + expectType + ", but varType has not constraints"
                    );
                }

                var prodTypes = produceTypes.map(prodType -> checkConstraint((int) constrIdx, expectType, prodType));
                if (prodTypes.filter(Result::isOk).isEmpty()) {
                    return Result.error(
                        "constraint#" + constrIdx +
                            "\n  expect type: " + expectType + ", but varType has not valid constraints:" +
                            prodTypes.foldLeft(
                                "",
                                (acc, it) -> acc.isBlank()
                                    ? it.fold(_succ -> "", err -> err)
                                    : acc + "\n" + it.fold(_succ -> "", err -> err))
                    );
                }

                return ok();
            });

        return foldErr(constraintCheck);
    }

    private Result<NoValue, String> isAssignableFrom(Type type) {
        if (constraints.isEmpty()) return ok(NoValue.instance);

        ImList<Result<NoValue, String>> contraintCheck = constraints.enumerate().map(en ->
            checkConstraint((int)en.index(), en.value(), type)
        );

        return foldErr(contraintCheck);
    }

    private static Result<NoValue, String> foldErr(ImList<Result<NoValue, String>> contraintCheck) {
        return contraintCheck.foldLeft(
            Result.<String>ok(),
            (acc, it) -> acc.fold(
                succ1 -> it,
                err1 -> it.fold(
                    succ2 -> acc,
                    err2 -> Result.error(err1 + "\n" + err2)
                )
            )
        );
    }

    private Result<NoValue, String> checkConstraint(int constrIdx, Type expectType, Type type) {
        Result<Boolean, String> coRes = expectType.isAssignableFrom(type);
        Result<Boolean, String> contrRes = type.isAssignableFrom(expectType);

        if (coPos == CoPos.InVariant) {
            if (coRes.fold(v->v,i->false) && contrRes.fold(v->v,i->false)) return ok();
            return Result.<NoValue, String>error(
                "constraint#" + constrIdx +
                    "\n  expect invaraint:" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + coRes +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contrRes
            );
        } else if (coPos == CoPos.Param && !coRes.fold(v->v,i->false)) {
            return Result.<NoValue, String>error(
                "constraint#" + constrIdx +
                    "\n  expect Param(CoVariant):" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + coRes +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contrRes
            );
        } else if (coPos == CoPos.Result && !contrRes.fold(v->v,i->false)) {
            return Result.<NoValue, String>error(
                "constraint#" + constrIdx +
                    "\n  expect Result(ContrVaraint):" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + coRes +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contrRes
            );
        }

        return Result.<String>ok();
    }
}
