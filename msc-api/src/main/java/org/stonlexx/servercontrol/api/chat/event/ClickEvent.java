package org.stonlexx.servercontrol.api.chat.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class ClickEvent
{

    /**
     * The type of action to perform on click
     */
    private final Action action;
    /**
     * Depends on action
     *
     * @see Action
     */
    private final String value;

    public enum Action
    {

        /**
         * Open a url at the path given by
         */
        OPEN_URL,
        /**
         * Open a file at the path given by
         */
        OPEN_FILE,
        /**
         * Run the command given by
         */
        RUN_COMMAND,
        /**
         * Inserts the string given by
         * text box
         */
        SUGGEST_COMMAND,
        /**
         * Change to the page number given by
         */
        CHANGE_PAGE
    }
}
