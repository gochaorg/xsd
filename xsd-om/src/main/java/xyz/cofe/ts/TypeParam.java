package xyz.cofe.ts;

import xyz.cofe.im.iter.EachToEnum;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Result.NoValue;

/**
 * Объявление параметра типа
 */
public final class TypeParam {
    public TypeParam(ImList<Type> constraints, CoPos coPos) {
        this.constraints = constraints;
        this.coPos = coPos;
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
        return Result.err("!! bug");
    }

    private Result<NoValue, String> isAssignableFrom(TypeVar typeVar) {
        if (coPos != typeVar.typeParam().coPos()) {
            return Result.err("CoVaraint position not match, expect " + coPos + ", found " + typeVar.typeParam().coPos());
        }

        if (constraints.isEmpty()) return Result.ok(NoValue.instance);

        ImList<Result<NoValue, String>> constraintCheck =
            constraints.enumerate().map(en -> {
                var constrIdx = en.index();
                var expectType = en.value();

                var produceTypes = typeVar.typeParam().constraints();
                if (produceTypes.isEmpty()) {
                    return Result.err(
                        "constraint#" + constrIdx +
                            "\n  expect type: " + expectType + ", but varType has not constraints"
                    );
                }

                var prodTypes = produceTypes.map(prodType -> checkConstraint(constrIdx, expectType, prodType));
                if (prodTypes.filter(Result::isOk).isEmpty()) {
                    return Result.err(
                        "constraint#" + constrIdx +
                            "\n  expect type: " + expectType + ", but varType has not valid constaints:" +
                            prodTypes.foldLeft(
                                "",
                                (acc, it) -> acc.isBlank()
                                    ? it.fold(_succ -> "", err -> err)
                                    : acc + "\n" + it.fold(_succ -> "", err -> err))
                    );
                }

                return Result.ok();
            });

        return foldErr(constraintCheck);
    }

    private Result<NoValue, String> isAssignableFrom(Type type) {
        if (constraints.isEmpty()) return Result.ok(NoValue.instance);

        ImList<Result<NoValue, String>> contraintCheck = constraints.enumerate().map(en ->
            checkConstraint(en.index(), en.value(), type)
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
                    err2 -> Result.err(err1 + "\n" + err2)
                )
            )
        );
    }

    private Result<NoValue, String> checkConstraint(int constrIdx, Type expectType, Type type) {
        var co = expectType.isAssignableFrom(type);
        var contr = type.isAssignableFrom(expectType);

        if (coPos == CoPos.InVariant) {
            if (co && contr) return Result.ok();
            return Result.<NoValue, String>err(
                "constraint#" + constrIdx +
                    "\n  expect invaraint:" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + co +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contr
            );
        } else if (coPos == CoPos.Param && !co) {
            return Result.<NoValue, String>err(
                "constraint#" + constrIdx +
                    "\n  expect Param(CoVariant):" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + co +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contr
            );
        } else if (coPos == CoPos.Result && !contr) {
            return Result.<NoValue, String>err(
                "constraint#" + constrIdx +
                    "\n  expect Result(ContrVaraint):" +
                    "\n  Co: " + expectType + " isAssignableFrom " + type + " = " + co +
                    "\n  Contr: " + type + " isAssignableFrom " + expectType + " = " + contr
            );
        }

        return Result.<String>ok();
    }
}
