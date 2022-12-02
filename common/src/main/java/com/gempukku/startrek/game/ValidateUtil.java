package com.gempukku.startrek.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;

public class ValidateUtil {
    private static ObjectSet<String> effectNotMentionedFields = new ObjectSet<>();
    private static ObjectSet<String> abilityNotMentionedFields = new ObjectSet<>();

    private static ObjectSet<String> otherIgnoredFields = new ObjectSet<>();

    static {
        effectNotMentionedFields.add("type");

        abilityNotMentionedFields.add("type");
        abilityNotMentionedFields.add("text");

        otherIgnoredFields.add("comment");
    }

    public static void exactly(Array<String> parameters, int count) {
        if (count == 0 && parameters == null)
            return;
        if (parameters.size != count)
            throw new GdxRuntimeException("Number of parameters is not correct, expected " + count);
    }

    public static void atLeast(Array<String> parameters, int count) {
        if (parameters.size < count)
            throw new GdxRuntimeException("Number of parameters is not correct, expected at least " + count);
    }

    public static void between(Array<String> parameters, int minimum, int maximum) {
        if (parameters.size < minimum)
            throw new GdxRuntimeException("Number of parameters is not correct, expected at least " + minimum);
        if (parameters.size > maximum)
            throw new GdxRuntimeException("Number of parameters is not correct, expected at most " + maximum);
    }

    public static void hasExactlyOneOf(JsonValue value, String... names) {
        int count = 0;
        for (String name : names) {
            if (value.has(name))
                count++;
        }
        if (count != 1)
            throw new GdxRuntimeException("Was expecting 1 field, found " + count);
    }

    public static void ifPresentCheckFor(JsonValue value, String conditionField, String validatingField) {
        if (value.has(conditionField) && !value.has(validatingField))
            throw new GdxRuntimeException("Was expecting to see " + validatingField);
        if (!value.has(conditionField) && value.has(validatingField))
            throw new GdxRuntimeException("Was not expecting to see " + validatingField);
    }

    public static void effectExpectedFields(JsonValue effect, String[] requiredFields, String[] optionalFields) {
        validateEffectOrAbility(effect, requiredFields, optionalFields, "effect", effectNotMentionedFields);
    }

    public static void abilityExpectedFields(JsonValue ability, String[] requiredFields, String[] optionalFields) {
        validateEffectOrAbility(ability, requiredFields, optionalFields, "ability", abilityNotMentionedFields);
    }

    private static void validateEffectOrAbility(JsonValue effect, String[] requiredFields, String[] optionalFields,
                                                String type, ObjectSet<String> notMentionedFields) {
        if (effect.type() != JsonValue.ValueType.object)
            throw new GdxRuntimeException("The " + type + " should be a JSON of type object");
        for (String notMentionedField : notMentionedFields) {
            if (effect.getString(notMentionedField, null) == null)
                throw new GdxRuntimeException("Field required for " + type + " - " + notMentionedField);
        }

        ObjectSet<String> childNames = new ObjectSet<>();
        for (JsonValue child : effect) {
            String childName = child.name();
            if (!notMentionedFields.contains(childName) && !otherIgnoredFields.contains(childName))
                childNames.add(childName);
        }

        for (String required : requiredFields) {
            if (!childNames.contains(required))
                throw new GdxRuntimeException("Should contain field: " + required);
        }

        for (String childName : childNames) {
            if (!contains(requiredFields, childName) && !contains(optionalFields, childName))
                throw new GdxRuntimeException("Unrecognized field: " + childName);
        }

    }

    private static boolean contains(String[] fields, String name) {
        for (String field : fields) {
            if (field.equals(name))
                return true;
        }
        return false;
    }
}
