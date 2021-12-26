package org.stonlexx.servercontrol.api.utility;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.stonlexx.servercontrol.api.utility.query.ResponseHandler;

import java.util.*;

@UtilityClass
public class Placeholders {

    private final Table<Class<?>, String, ResponseHandler<Object, ?>> placeholderHandlers
            = HashBasedTable.create();


    public <H> String replacePlaceholders(@NonNull H objectHandle, @NonNull String textToHandle) {
        return parseText(textToHandle).asString(objectHandle);
    }

    public PlaceholderParsedText parseText(@NonNull String textToParse) {
        return new PlaceholderParsedText(textToParse);
    }

    public <H> void register(@NonNull Class<H> classHandle,
                             @NonNull String placeholder,
                             @NonNull ResponseHandler<Object, H> responseHandler) {

        placeholderHandlers.put(classHandle, placeholder, responseHandler);
    }

    @AllArgsConstructor
    public class PlaceholderParsedText {

        private String currentText;

        private final Collection<String> ignorePlaceholders = new ArrayList<>();
        private final Map<String, String> localPlaceholders = new HashMap<>();


        public PlaceholderParsedText addLocalPlaceholder(@NonNull String placeholder, @NonNull Object value) {
            localPlaceholders.put(placeholder, value.toString());

            return this;
        }

        public PlaceholderParsedText ignorePlaceholder(@NonNull String placeholder) {
            ignorePlaceholders.add(placeholder.toLowerCase(Locale.ROOT));

            return this;
        }

        @SuppressWarnings("all")
        public <H> String asString(@NonNull H objectHandle) {
            String newText = currentText;

            // Registered placeholders
            for (Map.Entry<String, ResponseHandler<Object, ?>> entry
                    : placeholderHandlers.row(objectHandle.getClass()).entrySet()) {

                String placeholder = entry.getKey();

                // Check ignored placeholders
                if (ignorePlaceholders.contains(placeholder.toLowerCase(Locale.ROOT))) {
                    continue;
                }

                ResponseHandler<Object, H> placeholderHandler = ((ResponseHandler<Object, H>) entry.getValue());
                Object placeholderValue = placeholderHandler.handleResponse(objectHandle);

                if (placeholderValue != null) {
                    newText = newText.replace(placeholder, placeholderValue.toString());
                }
            }

            // Local placeholders
            for (Map.Entry<String, String> entry : localPlaceholders.entrySet()) {

                String placeholder = entry.getKey();

                // Check ignored placeholders
                if (ignorePlaceholders.contains(placeholder.toLowerCase(Locale.ROOT))) {
                    continue;
                }

                String placeholderValue = entry.getValue();
                if (placeholderValue != null) {

                    newText = newText.replace(placeholder, placeholderValue);
                }
            }

            return newText;
        }
    }

}
