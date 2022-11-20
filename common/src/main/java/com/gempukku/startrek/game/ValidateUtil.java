package com.gempukku.startrek.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectSet;

public class ValidateUtil {
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

    public static void effectExpectedFields(JsonValue effect, String[] requiredFields, String[] optionalFields) {
        if (effect.type() != JsonValue.ValueType.object)
            throw new GdxRuntimeException("Ability should be a JSON of type object");
        String type = effect.getString("type");
        if (type == null)
            throw new GdxRuntimeException("Type is required field for abilities");
        ObjectSet<String> childNames = new ObjectSet<>();
        for (JsonValue child : effect) {
            String childName = child.name();
            if (!childName.equals("type"))
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

    public static void abilityExpectedFields(JsonValue ability, String[] requiredFields, String[] optionalFields) {
        if (ability.type() != JsonValue.ValueType.object)
            throw new GdxRuntimeException("Ability should be a JSON of type object");
        String type = ability.getString("type");
        if (type == null)
            throw new GdxRuntimeException("Type is required field for abilities");
        String text = ability.getString("text");
        if (text == null)
            throw new GdxRuntimeException("Text is required field for abilities");
        ObjectSet<String> childNames = new ObjectSet<>();
        for (JsonValue child : ability) {
            String childName = child.name();
            if (!childName.equals("text") && !childName.equals("type"))
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
