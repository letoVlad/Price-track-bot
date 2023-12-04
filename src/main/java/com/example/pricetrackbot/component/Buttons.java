package com.example.pricetrackbot.component;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Buttons {
    private static final InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Start");
    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");
    private static final InlineKeyboardButton MOST_POSTS = new InlineKeyboardButton("Max messages");

    public static InlineKeyboardMarkup inlineMarkup() {
        START_BUTTON.setCallbackData("/start");
        HELP_BUTTON.setCallbackData("/help");
        MOST_POSTS.setCallbackData("/most_posts");

        List<InlineKeyboardButton> rowInline = List.of(START_BUTTON, HELP_BUTTON, MOST_POSTS);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    public InlineKeyboardMarkup selectionAnimalButtons() {
        InlineKeyboardButton wildberries = new InlineKeyboardButton("Wildberries");

        //присваивание "бирок" которые будут возвращатся при нажатии
        wildberries.setCallbackData("Wildberries");

        //Разметка кнопок
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtons = List.of(wildberries);

        //Для добавления кнопок в одну линию стоит в массив добавить только один массив со всеми кнопками
        List<List<InlineKeyboardButton>> rowsInLine = List.of(keyboardButtons);

        //Добавление в разметку массив с кнопками
        keyboardMarkup.setKeyboard(rowsInLine);
        return keyboardMarkup;
    }
}
